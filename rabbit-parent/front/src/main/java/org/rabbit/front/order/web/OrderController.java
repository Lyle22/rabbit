package org.rabbit.front.order.web;

import org.apache.commons.lang3.time.DateUtils;
import org.rabbit.common.code.ResponseResult;
import org.rabbit.entity.order.Order;
import org.rabbit.service.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单中心控制类
 * @author geestu
 *
 */
@RestController
@RequestMapping("order")
public class OrderController {

	private OrderService orderService;

	@RequestMapping("create")
	public Object create(@RequestBody List<Order> orders) {
		orderService.createOrders(orders); 
		ResponseResult<Order> responseResult = new ResponseResult<Order>();
		responseResult.setDataList(orders);
		responseResult.setCode("1000");
		return responseResult;
	}
	
	@PostMapping("save")
	public ResponseResult<Order> save(@RequestBody Order order) {
		Order entity = Order.builder()
				.amount(new BigDecimal(100))
				.buyNum(new BigDecimal(2))
				.cancelDate(DateUtils.addDays(new Date(), 3))
				.build();
		orderService.save(entity);
		return new ResponseResult<Order>(entity);
	} 
	
}
