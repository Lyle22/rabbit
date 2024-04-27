package org.rabbit.workflow.exception;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableClassLoadingException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.rabbit.common.contains.*;

/**
 * The type Global exception handler.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Provide global error handling for all controller
     *
     * @param exception the exception
     * @return com.wclsolution.docpal.api.security.ErrorResponse error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Result<Object> handleGlobalException(Exception exception) {
        exception.printStackTrace();
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler({FlowableClassLoadingException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public final @ResponseBody
    Result<Object> handleFlowableClassLoadingException(FlowableClassLoadingException exception) {
        log.error(exception.getMessage());
        return Result.error("Cannot be found the bean class");
    }

    @ExceptionHandler({FlowableIllegalArgumentException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public final @ResponseBody
    Result<Object> handleFlowableIllegalArgumentException(FlowableIllegalArgumentException exception) {
        log.error(exception.getMessage());
        return Result.error("There are illegal parameters in the process definition");
    }


    /**
     * Provide custom error handling for all controller
     *
     * @param exception the exception
     * @return error response
     */
    @ExceptionHandler({WorkflowException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public final @ResponseBody
    Result<Object> handleDocPalCustomException(WorkflowException exception) {
        return Result.error(exception.getErrorMessage());
    }
}
