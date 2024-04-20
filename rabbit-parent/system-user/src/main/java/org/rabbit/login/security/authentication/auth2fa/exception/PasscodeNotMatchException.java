package org.rabbit.login.security.authentication.auth2fa.exception;

import org.springframework.security.core.AuthenticationException;

public class PasscodeNotMatchException extends AuthenticationException {

    public PasscodeNotMatchException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PasscodeNotMatchException(String msg) {
        super(msg);
    }
}
