package org.rabbit.service.logs.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.rabbit.entity.logs.EmailLog;

/**
 * @author nine rabbit
 */
@Mapper
public interface EmailLogMapper extends BaseMapper<EmailLog> {

}
