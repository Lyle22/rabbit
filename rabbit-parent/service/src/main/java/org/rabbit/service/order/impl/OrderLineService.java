package org.rabbit.service.order.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.rabbit.common.enums.OrderLineStatus;
import org.rabbit.entity.order.OrderHeader;
import org.rabbit.entity.order.OrderLine;
import org.rabbit.service.order.dao.OrderLineDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class OrderLineService {

	@Autowired
	private OrderLineDao orderLineDao;

	/**
	 * 查询订单行集合
	 *
	 * @param headerId
	 *            订单头
	 * @return
	 *
	 */
	public List<OrderLine> getByHeaderId(Integer headerId) {
		OrderLine orderLine = new OrderLine();
		orderLine.setHeaderId(headerId);
		return orderLineDao.selectByOrderLine(orderLine);
	}

	/**
	 * 插入订单行集合数据
	 *
	 * @param orderLines
	 *            订单行数据
	 * @return 插入集合
	 *
	 */
	public List<OrderLine> save(List<OrderLine> orderLines, Integer headerId) {
		if (CollectionUtils.isEmpty(orderLines) || null == headerId) {
			return Lists.newArrayList();
		}
		List<OrderLine> saveList = Lists.newArrayList();
		// 遍历循环插入订单行数据
		for (OrderLine orderLine : orderLines) {
			orderLine.setHeaderId(headerId);
			orderLine.setLineStatus(OrderLineStatus.UNPAID);
			orderLine.setCreateDate(new Date());
			orderLine.setUpdateDate(new Date());
			orderLine.setDelFlag("0");
			orderLineDao.insert(orderLine);
			saveList.add(orderLine);
		}
		return saveList;
	}

	/**
	 * 逻辑删除订单行
	 *
	 * @param headerId
	 *            订单头id
	 * @return
	 *
	 */
	public boolean deleteByHeaderId(Integer headerId) {
		List<OrderLine> orderLines = this.getByHeaderId(headerId);
		int count = 0;
		if (CollectionUtils.isNotEmpty(orderLines)) {
			for (OrderLine record : orderLines) {
				record.setDelFlag("1");
				record.setUpdateDate(new Date());
				count += orderLineDao.updateByPrimaryKey(record);
			}
		}

		return count > 0 ? true : false;
	}

	/**
	 * 更新订单行
	 *
	 * @param orderLine
	 */
	public OrderLine updateByPrimaryKey(OrderLine orderLine) {
		if (null != orderLine) {
			orderLine.setUpdateDate(new Date());
			orderLineDao.updateByPrimaryKey(orderLine);
		}
		return orderLine;
	}

	/**
	 * 更新订单集合
	 *
	 * @param orderLines
	 *
	 */
	public List<OrderLine> update(List<OrderLine> orderLines) {
		List<OrderLine> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(orderLines)) {
			for (OrderLine orderLine : orderLines) {
				list.add(this.updateByPrimaryKey(orderLine));
			}
		}
		return list;
	}

	/**
	 * 更新订单行状态
	 *
	 * @param orderLines
	 *            订单集合
	 * @param lineStatus
	 *            状态
	 * @return
	 *
	 */
	public List<OrderLine> updateStatus(List<OrderLine> orderLines, OrderLineStatus lineStatus) {
		List<OrderLine> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(orderLines)) {
			for (OrderLine orderLine : orderLines) {
				orderLine.setLineStatus(lineStatus);
				list.add(this.updateByPrimaryKey(orderLine));
			}
		}
		return list;
	}

	/**
	 * 查询订单行集合
	 *
	 * @param orderLine
	 *            订单行实体
	 * @return
	 *
	 */
	public List<OrderLine> selectByOrderLine(OrderLine orderLine) {
		return this.orderLineDao.selectByOrderLine(orderLine);
	}

	/**
	 * 查询订单头的总金额
	 *
	 * @param headerId
	 *            订单头id
	 * @return
	 *
	 */
	public BigDecimal selectTotalAmount(Integer headerId) {
		return this.orderLineDao.selectTotalAmount(headerId);
	}

	/**
	 * 查询订单总金额
	 *
	 * @param orderHeaders
	 * @return
	 *
	 */
	public BigDecimal selectTotalAmount(List<OrderHeader> orderHeaders) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderHeader orderHeader : orderHeaders) {
			totalAmount = totalAmount.add(this.selectTotalAmount(orderHeader.getId()));
		}
		return totalAmount;
	}

	/**
	 * 把订单行失效掉
	 *
	 * @param ids
	 *            订单行id
	 * @return
	 *
	 */
	public boolean disable(List<Integer> ids) {
		if (CollectionUtils.isNotEmpty(ids)) {
			int count = 0;
			for (Integer id : ids) {
				count += orderLineDao.disable(id);
			}
			return count == ids.size() ? true : false;
		}
		return false;
	}

}
