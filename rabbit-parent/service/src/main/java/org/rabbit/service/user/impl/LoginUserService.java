package org.rabbit.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.entity.user.User;
import org.rabbit.service.user.ILoginUserService;
import org.rabbit.service.user.dao.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author nine rabbit
 */
@Slf4j
@Service
public class LoginUserService extends ServiceImpl<UserMapper, User> implements ILoginUserService {


    @Override
    public User getCurrentUserDTO() {
        return null;
    }

    @Override
    public User getCurrentUser() {
        return null;
    }
}
