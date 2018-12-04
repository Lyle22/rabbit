package org.rabbit.service.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.order.OrderHeader;
import org.rabbit.service.order.impl.OnlinePayServiceImpl;
import org.rabbit.service.order.impl.WechatServiceImpl;

import com.google.common.collect.Lists;

public abstract class OrderHeaderAbstractService {
	
	private OrderHeaderService orderHeaderService;
	
	public OrderHeaderAbstractService(PayWay payWay) {
		switch (payWay) {
		case ALIPAY:
			orderHeaderService = new OnlinePayServiceImpl(PayWay.ALIPAY);
			break;
		case WECHAT:
			orderHeaderService = new WechatServiceImpl(PayWay.WECHAT);
			break;
		case ONLINE:
			orderHeaderService = new OnlinePayServiceImpl(PayWay.ONLINE);
			break;

		default: orderHeaderService = new OnlinePayServiceImpl(PayWay.ONLINE);
			break;
		}
	}

	public List<OrderHeader> createOrder(List<OrderHeader> orderHeaders){
		
		return Lists.newArrayList();
	}
	
	public List<OrderHeader> updateOrder(List<OrderHeader> orderHeaders){
		
		return Lists.newArrayList();
	}
	
	public List<OrderHeader> payOrder(List<OrderHeader> orderHeaders){
		
		return Lists.newArrayList();
	}
	
	public Map<String, Object> calcelOrder(List<OrderHeader> orderHeaders){
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public List<OrderHeader> queryOrder(List<OrderHeader> orderHeaders){
		
		return Lists.newArrayList();
	}
	
}
