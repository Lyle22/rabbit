package org.rabbit.login.controller;

import org.rabbit.SystemAuthConfiguration;
import org.rabbit.common.Result;
import org.rabbit.login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author nine
 */
@Slf4j
@RestController
@PreAuthorize(value = SystemAuthConfiguration.ROLE_USER)
@RequestMapping("${API_VERSION}/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "info")
    public Result<Object> get() {
        System.out.println("get info");
        return Result.ok(userService.getUserById("123"));
    }

}
