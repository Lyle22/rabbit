package org.rabbit.login.security.authentication.auth2fa;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Login2fa auth token
 *
 * @author nine
 */
public class Login2faAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String token;

    /**
     * Instantiates a new authentication token.
     *
     * @param principal   the principal
     * @param credentials the credentials
     * @param token
     */
    public Login2faAuthenticationToken(Object principal, Object credentials, String token) {
        super(principal, credentials);
        this.token = token;
    }

    /**
     * Instantiates a new authentication token.
     *
     * @param principal   the principal
     * @param credentials the credentials
     * @param authorities the authorities
     * @param token
     */
    public Login2faAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String token) {
        super(principal, credentials, authorities);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
