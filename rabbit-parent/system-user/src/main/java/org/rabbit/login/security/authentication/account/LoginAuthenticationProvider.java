package org.rabbit.login.security.authentication.account;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.rabbit.SystemAuthConfiguration.ROLE_USER;

/**
 * the type Login Authentication Provider
 *
 * @author nine rabbit
 */
@Slf4j
@Component
public class LoginAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(@NotNull Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        log.debug("Login with username=[" + username + "], password=[" + password + "]");
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Missing password");
        }
        try {
            CustomBasicAuthInterceptor customBasicAuthInterceptor = new CustomBasicAuthInterceptor(username, password);
            String sessionId = customBasicAuthInterceptor.getToken();

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(ROLE_USER));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, authorities);
            LoginAuthenticationDetails authenticationDetails = new LoginAuthenticationDetails();
            authenticationDetails.setLoginSessionId(sessionId);
            token.setDetails(authenticationDetails);
            return token;
        } catch (Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return aClass.equals(LoginAuthenticationToken.class);
    }
}
