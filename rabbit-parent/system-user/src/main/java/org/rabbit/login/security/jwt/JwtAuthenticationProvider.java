package org.rabbit.login.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.rabbit.entity.user.User;
import org.rabbit.login.config.AuthConfigs;
import org.rabbit.login.contants.Authority;
import org.rabbit.login.models.LoginAuthenticationStore;
import org.rabbit.login.security.authentication.account.LoginAuthenticationDetails;
import org.rabbit.login.security.jwt.exception.JwtExpiredTokenException;
import org.rabbit.login.security.jwt.exception.JwtTokenOtherException;
import org.rabbit.login.security.jwt.exception.JwtTokenVerificationException;
import org.rabbit.login.service.LoginAuthenticationStoreService;
import org.rabbit.service.user.impl.LoginUserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static java.util.Arrays.stream;

/**
 * The type Jwt authentication provider.
 *
 * @author nine
 */
@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    final AuthConfigs authConfigs;
    final LoginAuthenticationStoreService loginAuthenticationStoreService;
    final LoginUserService userService;

    public JwtAuthenticationProvider(AuthConfigs authConfigs, LoginAuthenticationStoreService loginAuthenticationStoreService, LoginUserService userService) {
        this.authConfigs = authConfigs;
        this.loginAuthenticationStoreService = loginAuthenticationStoreService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        try {
            Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret().getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(jwtAuthenticationToken.getToken());

            String username = decodedJWT.getSubject();
            Date expiredAt = decodedJWT.getExpiresAt();

            String SessionId = decodedJWT.getClaim(Authority.SESSION_ID_KEY).asString();
            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

            // Get Session ID from redis
            User user = userService.getById(decodedJWT.getSubject());
            LoginAuthenticationStore authenticationStore = loginAuthenticationStoreService.getSession(SessionId, user.getId());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            LoginAuthenticationDetails loginAuthenticationDetails = new LoginAuthenticationDetails();
            loginAuthenticationDetails.setLoginSessionId(authenticationStore.getAuthSessionId());
            loginAuthenticationDetails.setJwtTokenExpiredAt(expiredAt.toInstant());
            authenticationToken.setDetails(loginAuthenticationDetails);
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
    public boolean supports(@NotNull Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
