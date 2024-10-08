package org.rabbit.common.exception;

import lombok.Data;

/**
 * The type custom exception.
 *
 * @author nine
 */
@Data
public class CustomException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;
    private StackTraceElement[] stackTraceElements;

    /**
     * Instantiates a new custom exception.
     *  @param errorMessage       the error message
     *
     */
    public CustomException(String errorMessage) {
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
    public CustomException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // getters and setters
}
