package org.rabbit.common.code;

import java.util.List;

import org.rabbit.common.base.BaseResult;
import org.rabbit.common.enums.BaseEnum;

import lombok.Data;

@Data
public class ResponseResult<T> {

	private String msg;

	private String code;

	private T data;

	private List<T> dataList;

	public ResponseResult(String msg, String code, T data, List<T> dataList) {
		this.msg = msg;
		this.code = code;
		this.data = data;
		this.dataList = dataList;
	}

	public ResponseResult(String msg, String code, T data) {
		this.msg = msg;
		this.code = code;
		this.data = data;
	}

	public ResponseResult(String code, T data) {
		this.code = code;
		this.data = data;
	}

	public ResponseResult() {
		super();
	}
	
	/**
	 * 返回成功的数据对象，默认返回成功码 200
	 * 
	 * @param data
	 */
	public ResponseResult(T data) {
		this.code = BaseEnum.SUCCESS.getResultCode();
		this.data = data;
	}

	public ResponseResult<T> bulidResult(String msg, String code, T data) {
		ResponseResult<T> responseResult = new ResponseResult<T>();
		responseResult.setMsg(msg);
		responseResult.setCode(code);
		responseResult.setData(data);
		return responseResult;
	}

	/**
	 * 成功
	 * 
	 * @return
	 */
	public ResponseResult<T> success() {
		return success(null);
	}

	/**
	 * 成功
	 * 
	 * @param data
	 * @return
	 */
	public ResponseResult<T> success(T data) {
		ResponseResult<T> res = new ResponseResult<T>();
		res.setCode(BaseEnum.SUCCESS.getResultCode());
		res.setMsg(BaseEnum.SUCCESS.getResultMsg());
		res.setData(data);
		return res;
	}

	/**
	 * 失败
	 */
	public ResponseResult<T> error(BaseResult baseResult) {
		ResponseResult<T> res = new ResponseResult<T>();
		res.setCode(baseResult.getResultCode());
		res.setMsg(baseResult.getResultMsg());
		return res;
	}

	/**
	 * 失败
	 */
	public ResponseResult<T> error(String code, String message) {
		ResponseResult<T> res = new ResponseResult<T>();
		res.setCode(code);
		res.setMsg(message);
		return res;
	}

	/**
	 * 失败
	 */
	public ResponseResult<T> error(String message) {
		ResponseResult<T> res = new ResponseResult<T>();
		res.setCode(BaseEnum.SERVER_BUSY.getResultCode());
		res.setMsg(message);
		return res;
	}

}
