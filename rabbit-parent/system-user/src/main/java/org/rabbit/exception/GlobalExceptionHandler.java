package org.rabbit.exception;

import javax.servlet.http.HttpServletRequest;

import org.rabbit.common.code.ResponseResult;
import org.rabbit.common.enums.BaseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
     * http请求的方法不正确
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseResult<BizException> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        return new ResponseResult<BizException>().error(BaseEnum.BAD_METHOD);
    }

	/**
	 * 处理自定义的业务异常
	 * 
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = BizException.class)
	@ResponseBody
	public ResponseResult<BizException> bizExceptionHandler(HttpServletRequest req, BizException e) {
		logger.error("发生业务异常！原因是：{}", e.getErrorMsg());
		return new ResponseResult<BizException>().bulidResult(e.getErrorMsg(), e.getErrorCode(), e);
	}

	/**
	 * 处理空指针的异常
	 * 
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = NullPointerException.class)
	@ResponseBody
	public ResponseResult<BizException> exceptionHandler(HttpServletRequest req, NullPointerException e) {
		logger.error("发生空指针异常！原因是:", e);
		return new ResponseResult<BizException>().error(BaseEnum.NULL_EXCEPTION.getResultCode(), e.getMessage());
	}
	
	/**
     * 请求参数不全
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseResult<BizException> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        logger.error("请求参数不全:【"+e.getMessage()+"】");
        return new ResponseResult<BizException>().error(BaseEnum.MISS_REQUEST_PARAMETER);
    }
    
    /**
     * 请求参数实体不全
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseResult<BizException> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
    	logger.error("请求参数不全:【"+e.getMessage()+"】");
    	return new ResponseResult<BizException>().error(BaseEnum.MISS_REQUEST_PARAMETER);
    }
	
	/**
     * 请求参数类型不正确
     */
    @ExceptionHandler(value = TypeMismatchException.class)
    @ResponseBody
    public ResponseResult<BizException> typeMismatchExceptionHandler(TypeMismatchException e) {
        logger.error("请求参数类型不正确:【"+e.getMessage()+"】");
        return new ResponseResult<BizException>().error(BaseEnum.TYPE_MISMATCH.getResultCode(), e.getMessage());
    }

	/**
     * 非法输入
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public ResponseResult<BizException> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        logger.error("非法输入:【"+e.getMessage()+"】");
        return new ResponseResult<BizException>().error(BaseEnum.BODY_NOT_MATCH.getResultCode(), e.getMessage());
    }
    
	/**
	 * 处理其他异常
	 * 
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public ResponseResult<BizException> exceptionHandler(HttpServletRequest req, Exception e) {
		logger.error("未知异常！原因是:", e);
		return new ResponseResult<BizException>().error(BaseEnum.ERNAL_SERVER_ERROR.getResultCode(), e.getMessage());
	}
}