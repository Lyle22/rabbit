package org.rabbit.service.form.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.rabbit.common.contains.Constants;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.entity.form.FormFieldMapping;
import org.rabbit.entity.form.FormInfo;
import org.rabbit.service.form.IFieldMappingService;
import org.rabbit.service.form.mapper.FormFieldMappingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 类职责: 定义表单的信息和维护它的属性映射关系
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FieldMappingServiceImpl  extends ServiceImpl<FormFieldMappingMapper, FormFieldMapping> implements IFieldMappingService{

    private final FormFieldMappingMapper repository;

    @Override
    public boolean create(FormInfo formInfo, List<FormFieldMapping> fieldMappings) {
        if (CollectionUtils.isEmpty(fieldMappings)) {
            return false;
        }
        // 保存属性映射关系列表
        for (FormFieldMapping fieldMapping : fieldMappings) {
            // generate column name
            String columnName = CommonUtils.replaceSpecialChar(fieldMapping.getFieldName());
            fieldMapping.setColumnName(columnName);
            fieldMapping.setStatus(Constants.StatusValue.ACTIVE);
            fieldMapping.setFormInfoId(formInfo.getId());
            baseMapper.insert(fieldMapping);
        }
        return true;
    }

    @Override
    public boolean delete(FormInfo formInfo) {
        List<FormFieldMapping> list = findAllByFormInfoId(formInfo.getId());
        for (FormFieldMapping mapping : list) {
            baseMapper.deleteById(mapping.getId());
        }
        return true;
    }

    @Override
    public List<FormFieldMapping> findAllByFormInfoId(@NotNull String formInfoId) {
        Assert.notNull(formInfoId, "form id can not be null");
        FormFieldMapping entity = new FormFieldMapping();
        entity.setFormInfoId(formInfoId);
        QueryWrapper<FormFieldMapping> ex = new QueryWrapper(entity);
        return baseMapper.selectList(ex);
    }

    @Override
    public List<FormFieldMapping> findActive(@NotNull String formInfoId) {
        List<FormFieldMapping> list = findAllByFormInfoId(formInfoId);
        return list.stream().filter(item -> Constants.StatusValue.ACTIVE.equals(item.getStatus())).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
    public boolean changeFields(FormInfo formInfo, List<FormFieldMapping> fields) {
        // 根据Id去重也就是保证字段名唯一
        List<FormFieldMapping> tableFields = fields.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FormFieldMapping::getFieldName))), ArrayList::new)
        );
        if (CollectionUtils.isEmpty(tableFields)) {
            // 如果案例定义不存在表字段,则禁用此案例表所有字段
            disable(tableFields);
            return false;
        }
        List<FormFieldMapping> existFieldMappings = findActive(formInfo.getId());
        // need to remove fields
        if (CollectionUtils.isNotEmpty(existFieldMappings)) {
            // 如果现在存在的表字段不存在于案例定义之中,则禁用此字段
            List<FormFieldMapping> disableFields = existFieldMappings.stream().filter(item ->
                    tableFields.stream().noneMatch(typeInfo -> item.getFieldName().equals(typeInfo.getFieldName()))
            ).collect(Collectors.toList());
            disable(disableFields);
        }
        // 保存属性映射关系列表
        Map<String, FormFieldMapping> nameMap = existFieldMappings.stream().collect(Collectors.toMap(FormFieldMapping::getFieldName, a -> a, (k1, k2) -> k1));
        for (FormFieldMapping fieldMapping : tableFields) {
            FormFieldMapping sameNameMapping = nameMap.get(fieldMapping.getFieldName());
            if (null != sameNameMapping) {
                sameNameMapping.setStatus(Constants.StatusValue.ACTIVE);
                baseMapper.updateById(sameNameMapping);
            } else {
                String columnName = CommonUtils.replaceSpecialChar(fieldMapping.getFieldName()).toLowerCase();
                fieldMapping.setColumnName(columnName);
                fieldMapping.setStatus(Constants.StatusValue.ACTIVE);
                fieldMapping.setFormInfoId(formInfo.getId());
                baseMapper.insert(fieldMapping);
            }
        }
        return true;
    }

    public boolean disable(List<FormFieldMapping> disableFields) {
        if (CollectionUtils.isNotEmpty(disableFields)) {
            for (FormFieldMapping disableField : disableFields) {
                disableField.setStatus(Constants.StatusValue.DEACTIVATED);
                baseMapper.updateById(disableField);
            }
        }
        return true;
    }

    @Override
    public boolean insertFormData(String tableLabel, Map<String, Object> dataMap) {
        return false;
    }
}
