package org.rabbit.front.order.web;

import java.util.List;

import org.rabbit.entity.order.OrderHeader;
import org.rabbit.service.order.OrderHeaderService;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {

	private OrderHeaderService orderHeaderService;

	public Object payOrder(List<OrderHeader> orderHeaders) {

		return orderHeaderService.payOrder(orderHeaders);
	}
}
