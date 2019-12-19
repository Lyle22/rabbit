package org.rabbit.front.order.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 返回的结果类
 * 
 * @author zzg
 * @since 2019-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResultDTO {
	
	/**
	 * 返回码
	 */
	private int code;
	
	/**
	 * 返回消息
	 */
	private String msg;
	
	/**
	 * 数据结果集
	 */
	private Object data;
	
	/**
	 * 消息详细说明
	 */
	private String reMessage;

	public ResultDTO(int code, String msg) {
		new ResultDTO(code, msg, null, null);
	}

	public ResultDTO(int code, String msg, Object data) {
		new ResultDTO(code, msg, data, null);
	}
	
	public ResultDTO(int code, String msg, Object data, String reMessage) {
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.reMessage = reMessage;
	}
}
