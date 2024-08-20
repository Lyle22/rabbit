package org.rabbit.login.security.authentication.publics;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * The type public authentication token.
 *
 * @author nine rabbit
 */
public class PublicAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String token;

    /**
     * Instantiates a new public authentication token.
     *
     * @param token the token
     */
    public PublicAuthenticationToken(String token) {
        super(null, null);
        this.token = token;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
