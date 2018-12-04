package org.rabbit.service.order.dao;

import java.util.List;

import org.rabbit.entity.order.OrderHeader;

public interface OrderHeaderDao {

	int deleteByPrimaryKey(OrderHeader orderHeader);

	int insert(OrderHeader orderHeader);

	int updateByPrimaryKey(OrderHeader orderHeader);

	int updateOrderStatusById(OrderHeader orderHeader);

	List<OrderHeader> selectByOrderHeader(OrderHeader orderHeader);

	OrderHeader selectByPrimaryKey(Integer id);

	List<OrderHeader> selectByRequestNum(OrderHeader orderHeader);

	int updateByRequestNum(OrderHeader orderHeader, String requestNum);

}
