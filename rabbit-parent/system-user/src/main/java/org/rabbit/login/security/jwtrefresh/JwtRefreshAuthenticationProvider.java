package org.rabbit.login.security.jwtrefresh;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.rabbit.login.config.AuthConfigs;
import org.rabbit.login.contants.Authority;
import org.rabbit.login.entity.User;
import org.rabbit.login.models.LoginAuthenticationStore;
import org.rabbit.login.security.authentication.account.CustomBasicAuthInterceptor;
import org.rabbit.login.security.jwt.JwtAuthenticationToken;
import org.rabbit.login.security.jwt.exception.JwtExpiredTokenException;
import org.rabbit.login.security.jwt.exception.JwtTokenOtherException;
import org.rabbit.login.security.jwt.exception.JwtTokenVerificationException;
import org.rabbit.login.service.LoginAuthenticationStoreService;
import org.rabbit.login.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Jwt authentication provider.
 *
 * @author nine
 */
@Component
@Slf4j
public class JwtRefreshAuthenticationProvider implements AuthenticationProvider {

    private final AuthConfigs authConfigs;

    @Value("${app.url}")
    private String appServerURL;

    final LoginAuthenticationStoreService loginAuthenticationStoreService;
    final UserService userService;

    public JwtRefreshAuthenticationProvider(AuthConfigs authConfigs, LoginAuthenticationStoreService loginAuthenticationStoreService, UserService userService) {
        this.authConfigs = authConfigs;
        this.loginAuthenticationStoreService = loginAuthenticationStoreService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(jwtAuthenticationToken.getToken());
            String oldSessionId = decodedJWT.getClaim(Authority.SESSION_ID_KEY).asString();
            // Get Session ID from Redis
            User user = userService.get(decodedJWT.getSubject());
            LoginAuthenticationStore authenticationStore = loginAuthenticationStoreService.getSession(oldSessionId, user.getId());
            CustomBasicAuthInterceptor customBasicAuthInterceptor = new CustomBasicAuthInterceptor(authenticationStore.getAuthSessionId());

            final String newSessionId = customBasicAuthInterceptor.getToken();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            JwtRefreshAuthenticationDetails jwtRefreshAuthenticationDetails = new JwtRefreshAuthenticationDetails();
            jwtRefreshAuthenticationDetails.setLoginSessionId(newSessionId);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), "null", authorities);
            authenticationToken.setDetails(jwtRefreshAuthenticationDetails);

            loginAuthenticationStoreService.deleteSession(oldSessionId, user.getId());
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
    public boolean supports(@NonNull Class<?> authentication) {
        log.info("aClass=[" + authentication.getName() + "]");
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
