package org.rabbit.common.base;

/**
 * 返回属性基类
 * 
 * @author zzg
 * @since 2019-12-20
 */
public interface BaseResult {
	
	/** 错误码 */
	String getResultCode();

	/** 错误描述 */
	String getResultMsg();
}
