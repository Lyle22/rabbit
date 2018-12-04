package org.rabbit.common.code;

import java.util.List;

public class ResponseResult<T> {
	
	private String msg;
	
	private int code;
	
	private T data;
	
	private List<T> dataList;
	
	public ResponseResult(String msg, int code, T data, List<T> dataList) {
		this.msg = msg;
		this.code = code;
		this.data = data;
		this.dataList = dataList;
	}

	public ResponseResult(String msg, int code, T data) {
		this.msg = msg;
		this.code = code;
		this.data = data;
	}

	public ResponseResult(int code, T data) {
		this.code = code;
		this.data = data;
	}

	public ResponseResult<T> bulidResult(String msg, int code, T data) {
		ResponseResult<T> responseResult = new ResponseResult<T>();
		responseResult.setMsg(msg);
		responseResult.setCode(code);
		responseResult.setData(data);
		return responseResult;
	}

	public ResponseResult() {
		super();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	
}
