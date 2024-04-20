package org.rabbit.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.entity.system.SystemSetting;
import org.rabbit.service.system.ISystemSettingService;
import org.rabbit.service.system.dao.SystemSettingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nine
 */
@Slf4j
@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting> implements ISystemSettingService {

    private final SystemSettingMapper dao;

    public SystemSettingServiceImpl(SystemSettingMapper dao) {
        this.dao = dao;
    }

    @Override
    public List<SystemSetting> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<SystemSetting> findBySystemId(String id) {
        return dao.findBySystemId(id);
    }

    @Override
    public List<SystemSetting> findBySystemIdType(String systemIdType) {
        return dao.findBySystemType(systemIdType);
    }

    @Override
    public List<SystemSetting> findBySystemIdTypeAndSystemId(String systemIdType, String id) {
        return dao.findBySystemTypeAndSystemId(systemIdType, id);
    }

    @Override
    public boolean save(SystemSetting entity) {
        return super.save(entity);
    }
}
