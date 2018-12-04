package org.rabbit.service.order.impl;

import org.rabbit.service.order.PayDTO;
import org.rabbit.service.order.dao.OrderHeaderDao;
import org.rabbit.service.trade.TradeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.rabbit.common.enums.GenerateNumberUtils;
import org.rabbit.common.enums.OrderLineStatus;
import org.rabbit.common.enums.OrderStatus;
import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.order.OrderHeader;
import org.rabbit.entity.order.OrderLine;
import org.rabbit.entity.order.OrderRequestNum;
import org.rabbit.entity.trade.TradeInfo;

@Service
public class OrderHeaderService {

	private Logger logger = LoggerFactory.getLogger(OrderHeaderService.class);

	@Autowired
	private OrderHeaderDao orderHeaderDao;

	@Autowired
	private OrderLineService orderLineService;

	@Autowired
	private OrderRequestNumService orderRequestNumService;

	@Autowired
	private TradeInfoService tradeInfoService;

	public List<OrderHeader> payOrder(List<OrderHeader> orderHeaders) {

		return Lists.newArrayList();
	}

	/**
	 * 删除订单头
	 *
	 * @param orderHeader
	 *            订单头
	 * @return int 删除数量
	 *
	 */
	public boolean deleteByPrimaryKey(OrderHeader orderHeader) {
		// 删除订单行数据
		boolean flag = orderLineService.deleteByHeaderId(orderHeader.getId());
		if (flag) {
			// 删除订单头数据
			int num = orderHeaderDao.deleteByPrimaryKey(orderHeader);
			return num > 0 ? true : false;
		}
		return false;
	}

	/**
	 * 保存订单头
	 *
	 * @param orderHeader
	 *            订单头
	 * @return orderHeader 订单头（包含订单行数据）
	 *
	 */
	public OrderHeader create(OrderHeader orderHeader) {
		orderHeader.setStatus(OrderStatus.UNPAID);
		orderHeader.setUpdateDate(new Date());
		orderHeader.setCreateDate(new Date());
		orderHeader.setOrderDate(new Date());
		orderHeader.setDelFlag(0);
		orderHeaderDao.insert(orderHeader);
		orderHeader.setOrderNumber(DateFormatUtils.format(new Date(), "yyyyMMdd") + orderHeader.getId());
		orderHeaderDao.updateByPrimaryKey(orderHeader);
		// 保存订单行
		List<OrderLine> orderLines = orderLineService.save(orderHeader.getOrderLines(), orderHeader.getId());
		orderHeader.setOrderLines(orderLines);
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
	public List<OrderHeader> createOrders(List<OrderHeader> orderHeaders) {
		List<OrderHeader> list = Lists.newArrayList();
		for (OrderHeader entity : orderHeaders) {
			list.add(this.create(entity));
		}
		return orderHeaders;
	}

	/**
	 * 调用订单支付
	 *
	 * @param orderHeaders
	 *            一次支付请求的订单集合
	 * @param PayWay
	 *            支付方式
	 * @param extraMap
	 *            额外参数
	 * @return 订单支付集合
	 *
	 */
	public List<OrderHeader> payOrder(List<OrderHeader> orderHeaders, PayWay PayWay, Map<String, Object> extraMap) {
		List<OrderHeader> list = Lists.newArrayList();
		if (CollectionUtils.isEmpty(orderHeaders)) {
			return list;
		}
		BigDecimal totalAmount = orderLineService.selectTotalAmount(orderHeaders);
		Integer firstOrderId = orderHeaders.get(0).getId();
		OrderRequestNum orderRequestNum = orderRequestNumService.selectByOrderId(firstOrderId);
		String requestNum = null;
		if (null == orderRequestNum) {
			// 没有则 生成支付请求编号
			requestNum = GenerateNumberUtils.generateRequestNum(firstOrderId);
		} else {
			requestNum = orderRequestNum.getRequestNum();
		}
		extraMap.put("amount", totalAmount);
		extraMap.put("outTradeNo", requestNum);
		extraMap.put("PayWay", PayWay);
		// 初始化订单信息，生成请求编号 汇款识别码 和 失效时间

		// 调用支付系统接口
		PayDTO payDTO = invokePay(requestNum, totalAmount, PayWay, extraMap);

		if (payDTO.getPaid()) {
			// 如支付成功，更新订单状态
			list = updateStatus(orderHeaders, OrderStatus.PAID);
		} else {
			// 判断如果是订单已关闭， 则生成新的请求编号重新发起一次支付
			requestNum = GenerateNumberUtils.generateRequestNum(firstOrderId);
			payDTO = invokePay(requestNum, totalAmount, PayWay, extraMap);
		}

		return list;
	}

	private PayDTO invokePay(String requestNum, BigDecimal totalAmount, PayWay PayWay, Map<String, Object> extraMap) {
		PayDTO payDTO = new PayDTO();
		return payDTO;
	}

	/**
	 * 根据主键更新信息
	 *
	 * @param orderHeader
	 * @return
	 */
	public OrderHeader updateByPrimaryKey(OrderHeader orderHeader) {
		orderHeader.setUpdateDate(new Date());
		int result = orderHeaderDao.updateByPrimaryKey(orderHeader);
		// 更新订单行
		if (result > 0) {
			orderLineService.update(orderHeader.getOrderLines());
		}
		return orderHeaderDao.selectByPrimaryKey(orderHeader.getId());
	}

	/**
	 * 根据主键更新信息
	 *
	 * @param orderHeader
	 * @return
	 */
	public boolean updateByRequestNum(OrderHeader orderHeader, String requestNum) {
		if (StringUtils.isEmpty(requestNum)) {
			return false;
		}
		orderHeader.setUpdateDate(new Date());
		orderHeaderDao.updateByRequestNum(orderHeader, requestNum);
		return true;
	}

	/**
	 * 更新数据集合
	 *
	 * @param orderHeaders
	 * @return
	 */
	public List<OrderHeader> update(List<OrderHeader> orderHeaders) {
		List<OrderHeader> list = Lists.newArrayList();
		for (OrderHeader entity : orderHeaders) {
			list.add(this.updateByPrimaryKey(entity));
		}
		return list;
	}

	/**
	 * 更新状态
	 *
	 * @param orderHeaders
	 *            订单头集合
	 * @param status
	 *            状态
	 * @return
	 */
	private List<OrderHeader> updateStatus(List<OrderHeader> orderHeaders, OrderStatus status) {
		List<OrderHeader> list = Lists.newArrayList();
		for (OrderHeader entity : orderHeaders) {
			OrderHeader orderHeader = orderHeaderDao.selectByPrimaryKey(entity.getId());
			orderHeader.setStatus(status);
			orderHeader.setUpdateDate(new Date());
			int result = orderHeaderDao.updateByPrimaryKey(orderHeader);
			if (result > 0) {
				this.updateOrderLineStatus(entity.getOrderLines(), orderHeader.getStatus());
			}
			list.add(orderHeader);
		}
		return list;
	}

	/**
	 * 更新订单行状态
	 *
	 * @param orderLines
	 *            订单行
	 * @param status
	 *            状态
	 * @return
	 */
	public List<OrderLine> updateOrderLineStatus(List<OrderLine> orderLines, OrderStatus status) {
		List<OrderLine> list = Lists.newArrayList();
		switch (status) {
		case CANCELED:
			list = orderLineService.updateStatus(orderLines, OrderLineStatus.CANCELED);
			break;
		case DISABLED:
			list = orderLineService.updateStatus(orderLines, OrderLineStatus.CANCELED);
			break;
		case PAID:
			list = orderLineService.updateStatus(orderLines, OrderLineStatus.COMPLETED);
			break;
		case PENDING_PAY:
			list = orderLineService.updateStatus(orderLines, OrderLineStatus.COMPLETED);
			break;
		case UNPAID:
			list = orderLineService.updateStatus(orderLines, OrderLineStatus.UNPAID);
			break;
		default:
			logger.error("修改订单行状态失败，订单头：{}， 状态： {}", orderLines.get(0).getHeaderId(), status);
			break;
		}
		return list;
	}

	/**
	 * 更新状态
	 *
	 * @param orderHeader
	 * @return
	 */
	public OrderHeader updateOrderStatusById(OrderHeader orderHeader) {
		orderHeader.setUpdateDate(new Date());
		orderHeaderDao.updateOrderStatusById(orderHeader);
		return orderHeader;
	}

	/**
	 * 取消订单
	 *
	 * @param orderHeader
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean remove(Integer id) {
		OrderRequestNum orderRequestNum = orderRequestNumService.selectByOrderId(id);
		if (null == orderRequestNum) {
			return false;
		}
		int result = 0;
		// 查询交易表记录
		TradeInfo tradeInfo = tradeInfoService.selectByOrderRequestNum(orderRequestNum.getRequestNum());
		// 交易已经成功支付，无法取消订单
		if (null != tradeInfo && ("TRADE_SUCCESS").equals(tradeInfo.getTradeStatus())) {
			return false;
		}

		tradeInfo.setTradeStatus("TRADE_CANCEL");
		if (tradeInfoService.updateTradeInfo(tradeInfo) > 0) {
			OrderHeader orderHeader = new OrderHeader();
			orderHeader.setId(id);
			orderHeader.setUpdateDate(new Date());
			orderHeader.setDelFlag(1);
			orderHeader.setStatus(OrderStatus.CANCELED);
			result = orderHeaderDao.updateOrderStatusById(orderHeader);
		}
		if (result > 0) {
			return orderLineService.deleteByHeaderId(id);
		}
		return true;
	}

	/**
	 * 查询订单头信息（包含订单行）
	 *
	 * @param orderHeader
	 * @return
	 */
	public List<OrderHeader> selectByOrderHeader(OrderHeader orderHeader) {
		return orderHeaderDao.selectByOrderHeader(orderHeader);
	}

	/**
	 * 查询订单头信息（包含订单行）
	 *
	 * @param id
	 * @return
	 *
	 */
	public OrderHeader selectByPrimaryKey(Integer id) {
		return orderHeaderDao.selectByPrimaryKey(id);
	}

	public List<OrderHeader> selectByRequestNum(OrderHeader orderHeader) {
		return orderHeaderDao.selectByRequestNum(orderHeader);
	}

	/**
	 * 根据Ids集合查询订单头的数据
	 *
	 * @param ids
	 *            订单头集合
	 * @return
	 *
	 */
	public List<OrderHeader> selectById(List<Integer> ids) {
		List<OrderHeader> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ids)) {
			for (Integer id : ids) {
				list.add(orderHeaderDao.selectByPrimaryKey(id));
			}
		}
		return list;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean disable(List<Integer> ids) {
		boolean flag = false;
		if (CollectionUtils.isNotEmpty(ids)) {
			for (Integer id : ids) {
				OrderHeader orderHeader = new OrderHeader();
				orderHeader.setId(id);
				orderHeader.setStatus(OrderStatus.DISABLED);
				orderHeader.setDelFlag(1);
				orderHeaderDao.updateByPrimaryKey(orderHeader);
			}
		}
		return flag;
	}
}
