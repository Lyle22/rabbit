package org.rabbit.service.order.impl;

import org.rabbit.service.order.OrderHeaderService;

import com.google.common.collect.Lists;

import java.util.List;

import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.order.OrderHeader;
import org.rabbit.service.order.OrderHeaderAbstractService;

public class OrderHeaderServiceImpl extends OrderHeaderAbstractService implements OrderHeaderService{

	public OrderHeaderServiceImpl(PayWay payWay) {
		super(payWay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<OrderHeader> payOrder(List<OrderHeader> orderHeaders){
		
		return Lists.newArrayList();
	}
}
