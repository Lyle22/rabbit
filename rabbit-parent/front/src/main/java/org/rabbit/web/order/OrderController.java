package org.rabbit.web.order;

import org.rabbit.common.code.ResponseResult;
import org.rabbit.entity.order.Order;
import org.rabbit.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller for Order
 * @author geestu
 */
@RestController
@RequestMapping("order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@ExceptionHandler(Exception.class)
	@RequestMapping("create")
	@ResponseBody
	public Object create(@RequestBody List<Order> orders) {
		orderService.createOrders(orders); 
		ResponseResult<Order> responseResult = new ResponseResult<Order>();
		responseResult.setDataList(orders);
		responseResult.setCode("200");
		return responseResult;
	}
	
	@RequestMapping("query")
	@ResponseBody
	public Object query() {
		List<Order> dataList = orderService.list(); 
		ResponseResult<Order> responseResult = new ResponseResult<Order>();
		responseResult.setDataList(dataList);
		responseResult.setCode("200");
		return responseResult;
	}
	
}
