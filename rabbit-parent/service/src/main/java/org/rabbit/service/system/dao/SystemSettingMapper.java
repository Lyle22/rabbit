package org.rabbit.service.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.rabbit.entity.system.SystemSetting;

import java.util.List;

/**
 * @author nine
 */
public interface SystemSettingMapper extends BaseMapper<SystemSetting> {

    /**
     * Find by id list.
     *
     * @param id the id
     * @return the list
     */
    List<SystemSetting> findById(String id);

    /**
     * Find by system id list.
     *
     * @param systemId the id
     * @return the list
     */
    List<SystemSetting> findBySystemId(String systemId);

    /**
     * Find by system id type list.
     *
     * @param systemType the system id type
     * @return the list
     */
    List<SystemSetting> findBySystemType(String systemType);

    /**
     * Find by system id type and system id list.
     *
     * @param systemType the system id type
     * @param systemId           the id
     * @return the list
     */
    List<SystemSetting> findBySystemTypeAndSystemId(String systemType, String systemId);
}
