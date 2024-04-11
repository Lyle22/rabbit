package com.rabbit.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbit.RedisConfig.RedisUtil;
import com.rabbit.user.service.UserService;
import com.rabbit.viewmodel.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.rabbit.common.code.ResponseResult;
import org.rabbit.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = { @Autowired})
public class UserController {

    private final UserService userService;
    private final RedisUtil redisUtil;

    @RequestMapping("redis")
    public Object redis(Model model) {
        redisUtil.set("username", "正光", 10000);
        return redisUtil.get("username");
    }

    @RequestMapping("get")
    public String getRedis(Model model) {
        return redisUtil.get("username").toString();
    }

    @PostMapping(value = "page")
    public Page<User> selectPage(@RequestBody UserRequestDTO requestDTO, Model model) {
        // 表示当前页，而10表示每页的显示显示的条目数
        Page<User> page = new Page<User>(requestDTO.getPageNum(), requestDTO.getPageSize());
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
