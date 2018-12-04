package org.rabbit.front.order.web;

import java.util.List;

import javax.annotation.Resource;

import org.rabbit.common.code.ResponseResult;
import org.rabbit.entity.order.OrderHeader;
import org.rabbit.service.order.impl.OrderHeaderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {

	private OrderHeaderService orderHeaderService;

	@RequestMapping("pay")
	@ResponseBody
	public Object payOrder(List<OrderHeader> orderHeaders) {
		orderHeaderService.payOrder(orderHeaders);
		OrderHeader orderHeader = new OrderHeader();
		return new ResponseResult<OrderHeader>(1000, orderHeader);
	}
	
	@RequestMapping("create")
	@ResponseBody
	public Object create(@RequestBody List<OrderHeader> orderHeaders) {
		orderHeaderService.createOrders(orderHeaders); 
		ResponseResult<OrderHeader> responseResult = new ResponseResult<OrderHeader>();
		responseResult.setDataList(orderHeaders);
		responseResult.setCode(1000);
		return responseResult;
	}
	
	@RequestMapping("update")
	@ResponseBody
	public Object update(@RequestBody List<OrderHeader> orderHeaders) {
		orderHeaderService.update(orderHeaders);
		ResponseResult<OrderHeader> responseResult = new ResponseResult<OrderHeader>();
		responseResult.setDataList(orderHeaders);
		responseResult.setCode(1000);
		return responseResult;
	}
	
	@RequestMapping("disable")
	@ResponseBody
	public Object disable(@RequestBody List<Integer> ids) {
		boolean flag =  orderHeaderService.disable(ids);
		ResponseResult<OrderHeader> responseResult = new ResponseResult<OrderHeader>();
		responseResult.setCode(flag == true ? 1000 : 550);
		return responseResult;
	} 
}
