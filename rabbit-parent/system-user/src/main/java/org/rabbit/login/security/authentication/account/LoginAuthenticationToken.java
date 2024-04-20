package org.rabbit.login.security.authentication.account;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * The type authentication token.
 *
 * @author nine
 */
public class LoginAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * Instantiates a new authentication token.
     *
     * @param principal   the principal
     * @param credentials the credentials
     */
    public LoginAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    /**
     * Instantiates a new authentication token.
     *
     * @param principal   the principal
     * @param credentials the credentials
     * @param authorities the authorities
     */
    public LoginAuthenticationToken(
            Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities
    ) {
        super(principal, credentials, authorities);
    }
}
