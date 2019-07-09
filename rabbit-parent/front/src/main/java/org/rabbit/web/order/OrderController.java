package org.rabbit.web.order;

import java.util.List;

import org.rabbit.common.code.ResponseResult;
import org.rabbit.entity.order.Order;
import org.rabbit.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.api.ApiController;

/**
 * 订单中心控制类
 * @author geestu
 *
 */
@Controller
@RequestMapping("order")
public class OrderController extends ApiController {

	@Autowired
	private OrderService orderService;

	@RequestMapping("create")
	@ResponseBody
	public Object create(@RequestBody List<Order> orders) {
		orderService.createOrders(orders); 
		ResponseResult<Order> responseResult = new ResponseResult<Order>();
		responseResult.setDataList(orders);
		responseResult.setCode(1000);
		return responseResult;
	}
	
	@RequestMapping("query")
	@ResponseBody
	public Object query() {
		List<Order> dataList = orderService.list(); 
		ResponseResult<Order> responseResult = new ResponseResult<Order>();
		responseResult.setDataList(dataList);
		responseResult.setCode(1000);
		return responseResult;
	}
	
}
