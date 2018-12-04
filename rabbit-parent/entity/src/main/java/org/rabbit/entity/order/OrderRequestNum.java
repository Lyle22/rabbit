package org.rabbit.entity.order;

import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.base.BaseEntity;

public class OrderRequestNum extends BaseEntity{

	private Integer orderId;
	
	private String requestNum;
	
	private PayWay payWay;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getRequestNum() {
		return requestNum;
	}

	public void setRequestNum(String requestNum) {
		this.requestNum = requestNum;
	}

	public PayWay getPayWay() {
		return payWay;
	}

	public void setPayWay(PayWay payWay) {
		this.payWay = payWay;
	}
	
	
}
