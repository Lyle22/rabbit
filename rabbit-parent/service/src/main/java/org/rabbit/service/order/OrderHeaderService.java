package org.rabbit.service.order;

import java.util.List;
import java.util.Map;

import org.rabbit.entity.order.OrderHeader;


public interface OrderHeaderService {

	public List<OrderHeader> createOrder(List<OrderHeader> orderHeaders);
	
	public List<OrderHeader> updateOrder(List<OrderHeader> orderHeaders);
	
	public List<OrderHeader> payOrder(List<OrderHeader> orderHeaders);
	
	public Map<String, Object> calcelOrder(List<OrderHeader> orderHeaders);
	
	public List<OrderHeader> queryOrder(List<OrderHeader> orderHeaders);
	
}
