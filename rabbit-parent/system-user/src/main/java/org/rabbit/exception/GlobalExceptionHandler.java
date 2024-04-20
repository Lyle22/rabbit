package org.rabbit.exception;

import org.rabbit.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type Global exception handler.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Provide global error handling for all controller
     *
     * @param exception the exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Result<Object> handleGlobalException(Exception exception) {
        exception.printStackTrace();
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(ClientException.class)
    public @ResponseBody
    Result<Object> handleAclException(ClientException exception) {
        exception.printStackTrace();
        return Result.error(exception.getMessage());
    }

}
