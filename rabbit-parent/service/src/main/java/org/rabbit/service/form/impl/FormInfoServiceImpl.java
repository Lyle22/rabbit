package org.rabbit.service.form.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.common.contains.Constants;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.entity.form.FormFieldMapping;
import org.rabbit.entity.form.FormInfo;
import org.rabbit.entity.user.User;
import org.rabbit.service.form.CaseTableConfig;
import org.rabbit.service.form.IFieldMappingService;
import org.rabbit.service.form.IFormInfoService;
import org.rabbit.service.form.mapper.FormInfoMapper;
import org.rabbit.service.form.models.FormInfoDTO;
import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.MTFieldTypeMapping;
import org.rabbit.service.jooq.impl.AbstractJooqService;
import org.rabbit.service.jooq.impl.JooqService;
import org.rabbit.service.jooq.models.TableRequestDTO;
import org.rabbit.service.user.ILoginUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nine rabbit
 **/
@Slf4j
@Service
public class FormInfoServiceImpl extends ServiceImpl<FormInfoMapper, FormInfo> implements IFormInfoService {
    private final ILoginUserService loginUserService;
    private final JooqService jooqService;
    private final CaseTableConfig caseTableConfig;
    private final IFieldMappingService fieldMappingService;

    public FormInfoServiceImpl(ILoginUserService loginUserService, JooqService jooqService, CaseTableConfig caseTableConfig, IFieldMappingService fieldMappingService) {
        this.loginUserService = loginUserService;
        this.jooqService = jooqService;
        this.caseTableConfig = caseTableConfig;
        this.fieldMappingService = fieldMappingService;
    }

    public String getTableNamePrefix() {
        return DEFAULT_TABLE_NAME_PREFIX;
    }

    public List<MTFieldTypeMapping> findFieldMappings() {
        return AbstractJooqService.getFieldTypeMapping();
    }

    public List<MTFieldInfo> findFixedFields() {
        return AbstractJooqService.getFixedFields();
    }

    public void verifyFieldSyntax(List<MTFieldInfo> fields) {
        for (MTFieldInfo fieldInfo : fields) {
            if (CommonUtils.checkSpecialChar(fieldInfo.getFieldName())) {
                // 字段名称请不要有特殊字符
                throw new IllegalArgumentException(
                        String.format("Please don't have special characters in the label. [%s]", fieldInfo.getFieldName()));
            }
            if (StringUtils.isBlank(caseTableConfig.matchValue(fieldInfo.getDataType()))) {
                throw new IllegalArgumentException( String.format("dataType does not exists. [%s]", fieldInfo.getDataType()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
    public FormInfo create(String tableLabel, String tableNamePrefix, String bizId, List<FormFieldMapping> fieldMappings) {
        Assert.state(null != bizId, "business id cannot be null");
        Assert.state(null != tableLabel, "table label cannot be null");
        String tableName = getTableName(tableLabel, tableNamePrefix);
        FormInfo optional = findByTableName(tableName);
        if (null == optional) {
            throw new IllegalArgumentException( "duplicate table, please change table label");
        }
        for (FormFieldMapping mapping : fieldMappings) {
            // 字段名称请不要有特殊字符
            if (CommonUtils.checkSpecialChar(mapping.getFieldName())) {
                throw new IllegalArgumentException(
                        String.format("Please don't have special characters in the label. [%s]", mapping.getFieldName()));
            }
            // 检查非法类型
            if (StringUtils.isBlank(caseTableConfig.matchValue(mapping.getDataType()))) {
                throw new IllegalArgumentException( String.format("dataType does not exists. [%s]", mapping.getDataType()));
            }
        }
        // 不能出现重复主键
        List<String> names = fieldMappings.stream().map(FormFieldMapping::getFieldName).collect(Collectors.toList());
        if (names.size() != fieldMappings.size()) {
            throw new IllegalArgumentException( "duplicate field name");
        }
        if (jooqService.checkTableNameIfExists(tableName)) {
            throw new IllegalArgumentException( "case table already exist");
        }

        FormInfo formInfo = new FormInfo();
        formInfo.setLabel(tableLabel);
        formInfo.setTableName(tableName);
        if (StringUtils.isBlank(tableNamePrefix)) {
            formInfo.setTableNamePrefix(DEFAULT_TABLE_NAME_PREFIX);
        } else {
            formInfo.setTableNamePrefix(tableNamePrefix);
        }
        formInfo.setStatus(Constants.StatusValue.ACTIVE);
        formInfo.setBizId(bizId);
        User userDTO = loginUserService.getCurrentUserDTO();
        formInfo.setCreatedBy(userDTO.getUserId());
        formInfo.setModifiedBy(userDTO.getUserId());
        formInfo.setCreatedDate(Instant.now());
        formInfo.setModifiedDate(Instant.now());
        baseMapper.insert(formInfo);
        // save field mapping list
        fieldMappingService.create(formInfo, fieldMappings);
        // create database table
        TableRequestDTO tableRequest = new TableRequestDTO();
        tableRequest.setTable(formInfo.getTableName());
        tableRequest.setFields(AbstractJooqService.getValueTableFixedFields());
        jooqService.createDataTable(tableRequest);
        return formInfo;
    }

    @Override
    public FormInfo findByLabel(String tableLabel, String tableNamePrefix) {
        String tableName = getTableName(tableLabel, tableNamePrefix);
        FormInfo formInfo = new FormInfo();
        formInfo.setTableName(tableName);
        QueryWrapper<FormInfo> ex = new QueryWrapper(formInfo);
        return baseMapper.selectOne(ex);
    }

    @Override
    public String getTableName(String tableLabel, String tableNamePrefix) {
        Optional<FormInfo> optional = Optional.ofNullable(findByLabel(tableLabel, null));
        if (optional.isPresent()) {
            return optional.get().getTableName();
        } else {
            if (StringUtils.isBlank(tableNamePrefix)) {
                tableNamePrefix = DEFAULT_TABLE_NAME_PREFIX;
            }
            return tableNamePrefix + CommonUtils.replaceSpecialChar(tableLabel).toLowerCase();
        }
    }

    @Override
    public List<FormFieldMapping> getTableStructure(String tableLabel, String tableNamePrefix) {
        String tableName = getTableName(tableLabel, tableNamePrefix);
        FormInfo formInfo = Optional.ofNullable(findByTableName(tableName))
                .orElseThrow(() -> new IllegalArgumentException( "Table does not exist"));
        return fieldMappingService.findActive(formInfo.getId());
    }

    public FormInfo findByTableName(String tableName) {
        FormInfo formInfo = new FormInfo();
        formInfo.setTableName(tableName);
        QueryWrapper<FormInfo> ex = new QueryWrapper(formInfo);
        return baseMapper.selectOne(ex);
    }

    public FormInfo findByBizId(String bizId) {
        FormInfo formInfo = new FormInfo();
        formInfo.setBizId(bizId);
        formInfo.setStatus(Constants.StatusValue.ACTIVE);
        QueryWrapper<FormInfo> ex = new QueryWrapper(formInfo);
        return baseMapper.selectOne(ex);
    }

    @Override
    public FormInfo getByBizId(String bizId) {
        FormInfo formInfo = new FormInfo();
        formInfo.setBizId(bizId);
        formInfo.setStatus(Constants.StatusValue.ACTIVE);
        QueryWrapper<FormInfo> ex = new QueryWrapper(formInfo);
        return baseMapper.selectOne(ex);
    }

    @Override
    public FormInfoDTO getReleaseByBizId(String bizId) {
        FormInfo formInfo = new FormInfo();
        formInfo.setBizId(bizId);
        formInfo.setStatus(Constants.StatusValue.ACTIVE);
        QueryWrapper<FormInfo> ex = new QueryWrapper(formInfo);
        List<FormInfo> list = baseMapper.selectList(ex);
        if (CollectionUtils.isNotEmpty(list)) {
            FormInfoDTO formInfoDTO = FormInfoDTO.transform(list.get(0));
            formInfoDTO.setFieldMappings(fieldMappingService.findActive(formInfoDTO.getId()));
            return formInfoDTO;
        } else {
            throw new IllegalArgumentException( "Please be advised that the form have not yet been published.");
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
    public FormInfo update(FormInfo formInfo, List<FormFieldMapping> fieldMappings) {
        User userDTO = loginUserService.getCurrentUserDTO();
        formInfo.setStatus(Constants.StatusValue.ACTIVE);
        formInfo.setModifiedBy(userDTO.getUserId());
        formInfo.setModifiedDate(Instant.now());
        baseMapper.insert(formInfo);
        // save field mapping list
        fieldMappingService.changeFields(formInfo, fieldMappings);
        return null;
    }

    @Override
    public boolean deleteByBizId(String bizId) {
        FormInfo formInfo = getByBizId(bizId);
        boolean drop = jooqService.dropTable(formInfo.getTableName(), true);
        if (drop) {
            fieldMappingService.delete(formInfo);
            baseMapper.deleteById(formInfo.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean insertRecord(FormInfoDTO formInfoDTO, String bizNo, String bizType, Map<String, Object> dataMap) {
        List<FormFieldMapping> fieldMappings = fieldMappingService.findActive(formInfoDTO.getId());
        Map<String, String> nameMap = fieldMappings.stream().collect(Collectors.toMap(FormFieldMapping::getFieldName, FormFieldMapping::getColumnName));
        List<Map<String, Object>> dataList = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String columnName = nameMap.get(entry.getKey());
            if (StringUtils.isBlank(columnName)) {
                log.error("can not find column name when field name is {}", entry.getKey());
                continue;
            }
            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put(BUSINESS_NO, bizNo);
            rowMap.put(BUSINESS_TYPE, bizType);
            rowMap.put(COLUMN_NAME, columnName);
            rowMap.put(COLUMN_VALUE, entry.getValue());
            dataList.add(rowMap);
        }
        return jooqService.insertRecord(formInfoDTO.getTableName(), dataList);
    }

    @Override
    public boolean updateRecord(FormInfo formInfo, String recordId, Map<String, Object> dataMap) {
        return false;
    }

    @Override
    public PaginationDTO<List<LinkedHashMap<String, Object>>> pageQueryRecord(FormInfo formInfo, int pageNum, int pageSize, Map<String, Object> whereMap) {
        return null;
    }

    @Override
    public List<LinkedHashMap<String, Object>> findRecords(String tableLabel, Map<String, Object> where) {


        return null;
    }

    private List<MTFieldInfo> transform(List<FormFieldMapping> formList) {
        List<FormFieldMapping> tableFields = formList;
        List<MTFieldInfo> fixedFields = CaseTableConfig.getFixedFields();
        tableFields.removeIf(fieldInfo -> fixedFields.stream().anyMatch(fix -> fix.getFieldName().equals(fieldInfo.getId())));
        List<MTFieldInfo> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(tableFields)) {
            List<FormFieldMapping> fields = tableFields.stream().distinct().collect(Collectors.toList());
            for (FormFieldMapping planTableField : fields) {
                String columnName = planTableField.getId();
                String dataType = planTableField.getDataType();
                MTFieldInfo fieldInfo = new MTFieldInfo();
                fieldInfo.setFieldName(columnName);
                fieldInfo.setDataType(caseTableConfig.matchValue(dataType));
                fieldInfo.setUnique(false);
                fieldInfo.setRequired(false);
                fieldInfo.setPrimaryKey(false);
                result.add(fieldInfo);
            }
            result.addAll(fixedFields);
        }
        return result;
    }
}
