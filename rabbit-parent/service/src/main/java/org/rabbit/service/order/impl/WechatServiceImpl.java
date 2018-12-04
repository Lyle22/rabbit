package org.rabbit.service.order.impl;

import org.rabbit.common.enums.PayWay;
import org.rabbit.service.order.OrderHeaderAbstractService;
import org.rabbit.service.order.OrderHeaderService;

public class WechatServiceImpl extends OrderHeaderAbstractService implements OrderHeaderService{

	public WechatServiceImpl(PayWay payWay) {
		super(payWay);
		// TODO Auto-generated constructor stub
	}


}
