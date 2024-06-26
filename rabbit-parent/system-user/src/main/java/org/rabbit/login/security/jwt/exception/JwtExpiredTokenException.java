package org.rabbit.login.security.jwt.exception;

import org.rabbit.login.security.jwt.JwtToken;
import org.springframework.security.core.AuthenticationException;

/**
 * The type Jwt expired token exception.
 */
public class JwtExpiredTokenException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;

    private JwtToken token;

    /**
     * Instantiates a new Jwt expired token exception.
     *
     * @param msg the msg
     */
    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    /**
     * Instantiates a new Jwt expired token exception.
     *
     * @param token the token
     * @param msg   the msg
     * @param t     the t
     */
    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    /**
     * Token string.
     *
     * @return the string
     */
    public String token() {
        return this.token.getToken();
    }
}
