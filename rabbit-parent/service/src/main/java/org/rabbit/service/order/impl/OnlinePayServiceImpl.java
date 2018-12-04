package org.rabbit.service.order.impl;

import java.util.List;

import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.order.OrderHeader;
import org.rabbit.service.order.OrderHeaderAbstractService;
import org.rabbit.service.order.OrderHeaderService;

import com.google.common.collect.Lists;

public class OnlinePayServiceImpl extends OrderHeaderAbstractService implements OrderHeaderService{
	
	public OnlinePayServiceImpl(PayWay payWay) {
		super(payWay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<OrderHeader> payOrder(List<OrderHeader> orderHeaders){
		
		return Lists.newArrayList();
	}
	
}
