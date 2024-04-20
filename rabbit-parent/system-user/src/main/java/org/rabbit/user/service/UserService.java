package org.rabbit.user.service;

import org.rabbit.entity.user.User;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.rabbit.user.dao.UserDao;


@Service
public class UserService extends ServiceImpl<UserDao, User>{
	
    public Page<User> selectUserPage(Page<User> page, User user) {
        page.setRecords(baseMapper.selectUserList(page,user));
        return page;
    }
}
