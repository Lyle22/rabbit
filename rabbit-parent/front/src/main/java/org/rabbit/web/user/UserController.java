package org.rabbit.web.user;

import org.rabbit.entity.order.Order;
import org.rabbit.service.redis.RedisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * User controller
 *
 * @since 1.0.0
 * @author nine
 */
@RestController
@RequestMapping("user")
public class UserController {

	private final RedisService redisService;

	public UserController(RedisService redisService) {
		this.redisService = redisService;
	}

	@PostMapping(value = "add")
	public Object create() {
		Order order = new Order();
	    order.setAmount(new BigDecimal(100));
		order.setBuyNum(new BigDecimal(1));
		order.setOrderNumber("100000000000");
		redisService.set("list", order);
		return redisService.get("list");
	}

}
