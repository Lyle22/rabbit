package org.rabbit.exception;

import lombok.Data;
import org.rabbit.common.exception.ErrorCode;

/**
 * The type Auth exception.
 *
 * @author nine rabbit
 */
@Data
public class AuthException extends RuntimeException {

    private ErrorCode errorCode;

    private String errorMessage;

    private StackTraceElement[] stackTraceElements;

    public AuthException(ErrorCode errorCode, String errorMessage, StackTraceElement[] stackTraceElements) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTraceElements = stackTraceElements;
    }

    public AuthException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
