package com.rabbit.user.dao;

import java.util.List;

import org.rabbit.entity.user.User;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserDao extends BaseMapper<User> {
	
	List<User> selectUserList(Page<User> page, User user);
}
