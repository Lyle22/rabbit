package org.rabbit.service.order.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.rabbit.common.enums.OrderStatus;
import org.rabbit.entity.order.Order;
import org.rabbit.service.order.OrderService;
import org.rabbit.service.order.dao.OrderMapper;
import org.rabbit.service.trade.TradeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

	private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderMapper orderDao;

	@Autowired
	private OrderRequestNumServiceImpl orderRequestNumService;

	@Autowired
	private TradeInfoService tradeInfoService;

	public List<Order> payOrder(List<Order> orderHeaders) {
		return Lists.newArrayList();
	}

	/**
	 * 保存订单头
	 *
	 * @param orderHeader
	 *            订单头
	 * @return orderHeader 订单头（包含订单行数据）
	 *
	 */
	public Order create(Order orderHeader) {
		orderHeader.setStatus(OrderStatus.UNPAID);
		orderHeader.setUpdateDate(new Date());
		orderHeader.setCreateDate(new Date());
		orderHeader.setDelFlag(0);
		orderDao.insert(orderHeader);
		orderHeader.setOrderNumber(DateFormatUtils.format(new Date(), "yyyyMMdd") + orderHeader.getId());
		orderDao.updateByPrimaryKey(orderHeader);
		return orderHeader;
	}

	/**
	 * 保存订单集合
	 *
	 * @param orderHeaders
	 *            订单集合
	 * @return
	 *
	 */
	public List<Order> createOrders(List<Order> orderHeaders) {
		List<Order> list = Lists.newArrayList();
		for (Order entity : orderHeaders) {
			list.add(this.create(entity));
		}
		return orderHeaders;
	}
	
}
