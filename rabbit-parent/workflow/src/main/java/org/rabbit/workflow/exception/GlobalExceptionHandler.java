package org.rabbit.workflow.exception;

import lombok.extern.slf4j.Slf4j;
import org.rabbit.common.contains.Result;
import org.rabbit.exception.MyControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type Global exception handler.
 *
 * @author nine rabbit
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends MyControllerAdvice {

    /**
     * Provide global error handling for all controller
     *
     * @param exception the exception
     * @return ErrorResponse error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Result<Object> handleGlobalException(Exception exception) {
        exception.printStackTrace();
        return Result.error(exception.getMessage());
    }
//
//    @ExceptionHandler({FlowableClassLoadingException.class})
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    public final @ResponseBody
//    Result<Object> handleFlowableClassLoadingException(FlowableClassLoadingException exception) {
//        log.error(exception.getMessage());
//        return Result.error("Cannot be found the bean class");
//    }
//
//    @ExceptionHandler({FlowableIllegalArgumentException.class})
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    public final @ResponseBody
//    Result<Object> handleFlowableIllegalArgumentException(FlowableIllegalArgumentException exception) {
//        log.error(exception.getMessage());
//        return Result.error("There are illegal parameters in the process definition");
//    }

    /**
     * Provide custom error handling for all controller
     *
     * @param exception the exception
     * @return error response
     */
    @ExceptionHandler({WorkflowException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public final @ResponseBody
    Result<Object> handleWorkflowException(WorkflowException exception) {
        return Result.error(exception.getErrorMessage());
    }
}
