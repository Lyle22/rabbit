package org.rabbit.workflow.exception;

import lombok.Data;
import org.rabbit.common.exception.ErrorCode;

/**
 * The type Doc pal custom exception.
 */
@Data
public class WorkflowException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;
    private StackTraceElement[] stackTraceElements;

    /**
     * Instantiates a new Doc pal custom exception.
     *
     * @param errorCode          the error code
     * @param errorMessage       the error message
     * @param stackTraceElements the stack trace elements
     */
    public WorkflowException(ErrorCode errorCode, String errorMessage, StackTraceElement[] stackTraceElements) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTraceElements = stackTraceElements;
    }

    /**
     * Instantiates a new Doc pal custom exception.
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     */
    public WorkflowException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // getters and setters
}
