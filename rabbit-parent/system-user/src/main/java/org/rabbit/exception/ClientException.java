package org.rabbit.exception;

import org.rabbit.common.ResponseEnum;

/**
 * the type of ClientException
 *
 * @author nine
 */
public class ClientException extends RuntimeException {

    private ResponseEnum errorCode;
    private String errorMessage;
    private StackTraceElement[] stackTraceElements;

    public ClientException(ResponseEnum errorCode, String errorMessage, StackTraceElement[] stackTraceElements) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTraceElements = stackTraceElements;
    }

    public ClientException(ResponseEnum errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ClientException(ResponseEnum errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMsg();
    }

}
