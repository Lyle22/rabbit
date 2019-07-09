package org.rabbit.web.user;

import java.math.BigDecimal;
import java.util.Date;

import org.rabbit.entity.order.Order;
import org.rabbit.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	private RedisService redisService;

	@RequestMapping("add")
	@ResponseBody
	public Object create() {
		Order order = new Order();
	    order.setAmount(new BigDecimal(100));
		order.setBuyNum(new BigDecimal(1));
		order.setCreateDate(new Date());
		order.setOrderNumber("100000000000");
		redisService.set("list", order);
		return redisService.get("list");
	}

}
