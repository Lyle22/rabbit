package org.rabbit.login.service;

import org.rabbit.login.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class UserService {

    public User get(String userId) {
        return User.builder().id(userId).build();
    }

    public User getUserById(String userId) {
        log.debug("query user info ID:{}", userId);
        return User.builder().id(userId).build();
    }

}
