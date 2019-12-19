package org.rabbit.service.order.dao;

import org.rabbit.entity.order.OrderRequestNum;

public interface OrderRequestNumDao {

	int updateByRequestNum(OrderRequestNum record);
	
	int insert(OrderRequestNum record);
	
}
