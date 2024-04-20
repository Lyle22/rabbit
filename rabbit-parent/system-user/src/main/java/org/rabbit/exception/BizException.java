package org.rabbit.exception;

import org.rabbit.common.base.BaseResult;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * business exception
 *
 * @author nine rabbit
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BizException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 错误码
	 */
	protected String errorCode;
	/**
	 * 错误信息
	 */
	protected String errorMsg;

	public BizException() {
		super();
	}

	public BizException(BaseResult error) {
		super(error.getResultCode());
		this.errorCode = error.getResultCode();
		this.errorMsg = error.getResultMsg();
	}

	public BizException(BaseResult error, Throwable cause) {
		super(error.getResultCode(), cause);
		this.errorCode = error.getResultCode();
		this.errorMsg = error.getResultMsg();
	}

	public BizException(String errorMsg) {
		super(errorMsg);
		this.errorMsg = errorMsg;
	}

	public BizException(String errorCode, String errorMsg) {
		super(errorCode);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public BizException(String errorCode, String errorMsg, Throwable cause) {
		super(errorCode, cause);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}