package org.rabbit.entity.order;

import java.util.Date;
import java.util.List;

import org.rabbit.common.enums.OrderStatus;
import org.rabbit.entity.base.BaseEntity;

import com.google.common.collect.Lists;

public class OrderHeader extends BaseEntity{

	private String orderNumber;
	
	private String remitCode;
	
	private Date orderDate;
	
	private Date cancelDate;
	
	private OrderStatus status;
	
	private List<OrderLine> orderLines = Lists.newArrayList();
	
	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRemitCode() {
		return remitCode;
	}

	public void setRemitCode(String remitCode) {
		this.remitCode = remitCode;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
}
