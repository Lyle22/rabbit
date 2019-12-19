package org.rabbit.common.enums;

import java.util.concurrent.TimeUnit;

public enum ExpireTime {

	// 未读消息的有效期为30天
	UNREAD_MSG(30L, TimeUnit.DAYS),
	ORDER_PAY(30L, TimeUnit.MINUTES);

	/**
	 * 过期时间
	 */
	private Long time;
	/**
	 * 时间单位
	 */
	private TimeUnit timeUnit;

	ExpireTime(Long time, TimeUnit timeUnit) {
		this.time = time;
		this.timeUnit = timeUnit;
	}

	public Long getTime() {
		return time;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

}
