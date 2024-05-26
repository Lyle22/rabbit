package org.rabbit.login.security.authentication.auth2fa;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.entity.user.User;
import org.rabbit.login.config.AuthConfigs;
import org.rabbit.login.contants.Authority;
import org.rabbit.login.models.LoginAuthenticationStore;
import org.rabbit.login.security.authentication.auth2fa.exception.PasscodeNotMatchException;
import org.rabbit.login.service.LoginAuthenticationStoreService;
import org.rabbit.service.user.impl.LoginUserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * The authentication provider with 2FA support
 *
 * @author nine
 */
@Component
@Slf4j
public class Login2faAuthenticationProvider implements AuthenticationProvider {

    final AuthConfigs authConfigs;
    final LoginAuthenticationStoreService loginAuthenticationStoreService;
    final LoginUserService userService;

    public Login2faAuthenticationProvider(AuthConfigs authConfigs, LoginAuthenticationStoreService loginAuthenticationStoreService, LoginUserService userService) {
        this.authConfigs = authConfigs;
        this.loginAuthenticationStoreService = loginAuthenticationStoreService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        Login2faAuthenticationToken login2FaAuthenticationToken = (Login2faAuthenticationToken) authentication;
        final String passcode = (String) authentication.getCredentials();

        Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(login2FaAuthenticationToken.getToken());
        } catch (Exception ex) {
            throw new BadCredentialsException("authentication failed [" + ex + "]");
        }

        // get login session id from redis
        String sessionId = decodedJWT.getClaim(Authority.SESSION_ID_KEY).asString();
        User user = userService.getById(decodedJWT.getSubject());
        LoginAuthenticationStore loginAuthenticationStore = loginAuthenticationStoreService.getSession(sessionId, user.getId());

        if (loginAuthenticationStore == null) {
            throw new BadCredentialsException(" authentication failed");
        }
        if (loginAuthenticationStore.getPasscode() == null || !loginAuthenticationStore.getPasscode().equals(passcode)) {
            throw new PasscodeNotMatchException("Incorrect Passcode=[" + passcode + "]");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), "not required", authorities);
        Login2faAuthenticationDetails authenticationDetails = new Login2faAuthenticationDetails();
        authenticationDetails.setLoginSessionId(loginAuthenticationStore.getAuthSessionId());
        token.setDetails(authenticationDetails);

        // Remove Login Session ID from redis
        loginAuthenticationStoreService.deleteSession(sessionId, user.getId());
        return token;
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return aClass.equals(Login2faAuthenticationToken.class);
    }
}
