package org.rabbit.service.template.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.rabbit.entity.template.EmailTemplate;

/**
 * Email Template Mapper
 *
 * @author nine rabbit
 */
@Mapper
public interface EmailTemplateMapper extends BaseMapper<EmailTemplate> {

}
