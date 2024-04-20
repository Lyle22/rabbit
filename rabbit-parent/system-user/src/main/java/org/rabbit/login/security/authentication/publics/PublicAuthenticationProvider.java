package org.rabbit.login.security.authentication.publics;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.rabbit.login.config.AuthConfigs;
import org.rabbit.login.security.jwt.exception.JwtExpiredTokenException;
import org.rabbit.login.security.jwt.exception.JwtTokenOtherException;
import org.rabbit.login.security.jwt.exception.JwtTokenVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * The type public authentication provider.
 *
 * @author nine
 */
@Component
@Slf4j
public class PublicAuthenticationProvider implements AuthenticationProvider {

    private final AuthConfigs authConfigs;

    public PublicAuthenticationProvider(AuthConfigs authConfigs) {
        this.authConfigs = authConfigs;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PublicAuthenticationToken publicAuthenticationToken = (PublicAuthenticationToken) authentication;
        try {
            Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret().getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(publicAuthenticationToken.getToken());
            String shareId = decodedJWT.getClaim("shareId").asString();
            Date expiredAt = decodedJWT.getExpiresAt();

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_PUBLIC"));

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken("administrator", null, authorities);
            PublicAuthenticationDetails publicAuthenticationDetails = new PublicAuthenticationDetails();
            publicAuthenticationDetails.setShareId(shareId);
            publicAuthenticationDetails.setJwtTokenExpiredAt(expiredAt.toInstant());
            authenticationToken.setDetails(publicAuthenticationDetails);
            return authenticationToken;
        } catch (SignatureVerificationException ex) {
            throw new JwtTokenVerificationException("Token Signature Verification Failed.");
        } catch (TokenExpiredException ex) {
            throw new JwtExpiredTokenException(ex.getMessage());
        } catch (Exception ex) {
            throw new JwtTokenOtherException(ex.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (PublicAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
