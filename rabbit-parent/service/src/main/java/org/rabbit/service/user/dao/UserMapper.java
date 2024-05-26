package org.rabbit.service.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.rabbit.entity.user.User;

/**
 * @author nine
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


}
