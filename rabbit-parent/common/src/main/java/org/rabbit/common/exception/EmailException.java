package org.rabbit.common.exception;

import lombok.Data;

/**
 * The type Email Exception
 *
 * @author nine rabbit
 */
@Data
public class EmailException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;
    private StackTraceElement[] stackTraceElements;

    /**
     * Instantiates a new custom exception.
     *
     * @param errorCode          the error code
     * @param errorMessage       the error message
     * @param stackTraceElements the stack trace elements
     */
    public EmailException(ErrorCode errorCode, String errorMessage, StackTraceElement[] stackTraceElements) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTraceElements = stackTraceElements;
    }

    /**
     * Instantiates a new custom exception.
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     */
    public EmailException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // getters and setters
}
