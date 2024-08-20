package org.rabbit.service.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.rabbit.entity.system.SystemConfig;

import java.util.List;

/**
 * @author nine
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * Find by id list.
     *
     * @param id the id
     * @return the list
     */
    List<SystemConfig> findById(String id);

    /**
     * Find by system id list.
     *
     * @param systemId the id
     * @return the list
     */
    List<SystemConfig> findBySystemId(String systemId);

    /**
     * Find by system id type list.
     *
     * @param systemType the system id type
     * @return the list
     */
    List<SystemConfig> findBySystemType(String systemType);

    /**
     * Find by system id type and system id list.
     *
     * @param systemType the system id type
     * @param systemId           the id
     * @return the list
     */
    List<SystemConfig> findBySystemTypeAndSystemId(String systemType, String systemId);
}
