package org.rabbit.login.service;

import org.rabbit.login.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class LoginUserService {

    public LoginUser get(String userId) {
        return LoginUser.builder().id(userId).build();
    }

    public LoginUser getUserById(String userId) {
        log.debug("query user info ID:{}", userId);
        return LoginUser.builder().id(userId).build();
    }

}
