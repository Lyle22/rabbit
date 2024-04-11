package com.rabbit.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbit.RedisConfig.RedisUtil;
import com.rabbit.user.service.UserService;
import org.rabbit.common.code.ResponseResult;
import org.rabbit.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired	private UserService userService;
	
	@Autowired	private RedisUtil redisUtil;
	
	@RequestMapping("redis")
	public Object redis(Model model) {
		redisUtil.set("username", "正光", 10000);
		return redisUtil.get("username");
	}
	
	@RequestMapping("get")
	public String getRedis(Model model) {
		return redisUtil.get("username").toString();
	}

	@RequestMapping("page")
	public Object selectPage(Model model) {
		Page<User> page = new Page<User>(1, 10); // 1表示当前页，而10表示每页的显示显示的条目数
		page = userService.selectUserPage(page, User.builder().username("Jack").build());
		return page;
	}

	@GetMapping("/{id}")
	public ResponseResult<User> get(@PathVariable String id) {
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("user", "userinfo");
		User user = User.builder().email("1917892@qq.com").username("zzg").mobile("13254545777").build();

		return new ResponseResult<User>(user);
	}

	@PostMapping("save")
	public ResponseResult<User> save(@RequestBody User entity) {
		if (null == entity.getId()) {
			// 新增用户
			User user = User.builder().email("1917892@qq.com").username("zzg").mobile("13254545777").build();
			userService.save(user);
		} else {
			// 更新用户
		}
		return new ResponseResult<User>(entity);
	}

	@GetMapping("checkTicket")
	public ResponseResult<String> chekcTicket(@RequestParam String userTicket) {
		// 检查票据
		return new ResponseResult<String>("");
	}

	@GetMapping("check")
	public ResponseResult<String> check() {
		// 检查票据
		return new ResponseResult<String>("check");
	}
}
