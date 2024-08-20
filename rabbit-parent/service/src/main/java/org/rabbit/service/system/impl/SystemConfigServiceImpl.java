package org.rabbit.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.entity.system.SystemConfig;
import org.rabbit.service.system.ISystemConfigService;
import org.rabbit.service.system.dao.SystemConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The class of system configuration service
 *
 * @author nine rabbit
 */
@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements ISystemConfigService {

    private final SystemConfigMapper dao;

    public SystemConfigServiceImpl(SystemConfigMapper dao) {
        this.dao = dao;
    }

    @Override
    public List<SystemConfig> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<SystemConfig> findBySystemId(String id) {
        return dao.findBySystemId(id);
    }

    @Override
    public List<SystemConfig> findBySystemType(String systemIdType) {
        return dao.findBySystemType(systemIdType);
    }

    @Override
    public List<SystemConfig> findBySystemTypeAndSystemId(String systemIdType, String systemId) {
        return dao.findBySystemTypeAndSystemId(systemIdType, systemId);
    }

    @Override
    public boolean save(SystemConfig entity) {
        return super.save(entity);
    }
}
