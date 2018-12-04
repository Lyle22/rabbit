package org.rabbit.service.order.dao;

import java.math.BigDecimal;
import java.util.List;

import org.rabbit.entity.order.OrderLine;

public interface OrderLineDao {

	int deleteByPrimaryKey(OrderLine orderLine);

	int insert(OrderLine orderLine);

	int updateByPrimaryKey(OrderLine orderLine);

	int updateOrderStatusById(OrderLine orderLine);

	List<OrderLine> selectByOrderLine(OrderLine orderLine);

	List<OrderLine> selectByPrimaryKey(Integer id);

	BigDecimal selectTotalAmount(Integer headerId);

	int disable(Integer id);
}
