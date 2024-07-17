package org.rabbit.service.form.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.common.contains.StatusValue;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.entity.form.CaseTable;
import org.rabbit.entity.user.User;
import org.rabbit.service.form.CaseTableConfig;
import org.rabbit.service.form.ICaseTableService;
import org.rabbit.service.form.mapper.CaseTableMapper;
import org.rabbit.service.form.models.CaseTableResponseDTO;
import org.rabbit.service.form.models.CmmnPlanFormDTO;
import org.rabbit.service.jooq.MTColumnInfo;
import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.MTFieldTypeMapping;
import org.rabbit.service.jooq.impl.AbstractJooqService;
import org.rabbit.service.jooq.impl.JooqService;
import org.rabbit.service.jooq.models.CaseTableRequestDTO;
import org.rabbit.service.jooq.models.TableRequestDTO;
import org.rabbit.service.user.ILoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * case table 服务实现类
 * </p>
 *
 * @author nine
 * @since 2024-07-14
 */
@Service
public class CaseTableServiceImpl extends ServiceImpl<CaseTableMapper, CaseTable> implements ICaseTableService {
    @Autowired
    private ILoginUserService loginUserService;

    @Autowired
    private JooqService jooqService;

    @Autowired private CaseTableConfig caseTableConfig;

    @Override
    public String getTableNamePrefix() {
        return CaseTableConfig.TABLE_NAME_PREFIX;
    }

    @Override
    public List<MTFieldTypeMapping> findFieldMappings() {
        return caseTableConfig.getColumnMapping();
    }

    @Override
    public List<MTFieldInfo> findFixedFields() {
        return CaseTableConfig.getFixedFields();
    }

    @Override
    public void verifyFieldSyntax(List<MTFieldInfo> fields) {
        // 1. 是否检查字段类型符合要求
        List<MTFieldTypeMapping> fieldTypeMappings = findFieldMappings();
        fields.forEach(field -> {
            caseTableConfig.matchValue(field.getDataType());
        });
        // 2. 应该检查字段列表的总长度是否符合要求: Table length can be specified as a value from 0 to 65,535 bytes
    }

    private CaseTable findByLabel(@NonNull String label) {
        CaseTable entity = new CaseTable();
        entity.setLabel(label);
        QueryWrapper<CaseTable> waiter = new QueryWrapper<CaseTable>();
        return baseMapper.selectOne(waiter);
    }


    private CaseTable findByTableName(@NonNull String tableName) {
        CaseTable entity = new CaseTable();
        entity.setTableName(tableName);
        QueryWrapper<CaseTable> waiter = new QueryWrapper<CaseTable>();
        return baseMapper.selectOne(waiter);
    }

    /**
     * Verification duplicate label
     *
     * @param label label
     * @return boolean return true if duplicate otherwise false
     */
    private boolean isDuplicateName(String label, String id) {
        Optional<CaseTable> opl = Optional.ofNullable(findByLabel(label));
        return opl.filter(table -> null == id || (table.getId().compareTo(id) != 0)).isPresent();
    }

    private void checkArgs(CaseTable entity) {
        if (StringUtils.isAnyBlank(entity.getLabel())) {
            throw new IllegalArgumentException( "Label can not be null.");
        }
        if (isDuplicateName(entity.getLabel(), entity.getId())) {
            throw new IllegalArgumentException( "Label already exists");
        }
    }

    @Override
    public String generateTableName(CmmnPlanFormDTO cmmnPlanFormDTO) {
        if (StringUtils.isNotBlank(cmmnPlanFormDTO.getCasetable())) {
            return CaseTableConfig.TABLE_NAME_PREFIX + cmmnPlanFormDTO.getCasetable();
        } else {
            return this.generateTableName(cmmnPlanFormDTO.getName());
        }
    }

    public String generateTableName(@NotNull String label) {
        return CaseTableConfig.TABLE_NAME_PREFIX + CommonUtils.replaceSpecialChar(label).toLowerCase();
    }

    public String generateTableName(CaseTableRequestDTO request) {
        if (StringUtils.isNotBlank(request.getTableName())) {
            request.setTableName(this.generateTableName(request.getTableName()));
        } else {
            String tableName = this.generateTableName(request.getLabel());
            request.setTableName(tableName);
        }
        return request.getTableName();
    }

    public CaseTable checkCaseTable(CaseTableRequestDTO request) {
        CaseTable caseTable = null;
        if (StringUtils.isNotBlank(request.getTableName())) {
            return findByTableName(this.generateTableName(request.getTableName()));
        }
        if (StringUtils.isNotBlank(request.getLabel())) {
            return findByTableName(this.generateTableName(request.getLabel()));
        }
        return caseTable;
    }

    @Override
    public CaseTable create(CaseTableRequestDTO request) {
        Assert.notNull(request.getCaseTypeId(), "case type id cannot be null");
        // Pre-check parameters
        if (StringUtils.isAnyBlank(request.getTableName(), request.getLabel())) {
            throw new IllegalArgumentException( "Missing parameters");
        }
        CaseTable entity = request.to();
        entity.setTableName(this.generateTableName(request));
        if (StringUtils.isBlank(request.getLabel())) {
            entity.setLabel(entity.getTableName());
            request.setLabel(entity.getTableName());
        }
        if (isDuplicateName(entity.getLabel(), entity.getId())) {
            entity.setLabel(entity.getTableName());
            request.setLabel(entity.getTableName());
        }
        if (jooqService.checkTableNameIfExists(entity.getTableName())) {
            throw new IllegalArgumentException( "case table already exists");
        }
        TableRequestDTO tableRequest = request.toTableRequest();
        // 是否检查字段类型符合要求
        verifyFieldSyntax(tableRequest.getFields());
        jooqService.validationUnique(tableRequest.getFields());
        // Execute create table sql
        boolean creation = jooqService.createDataTable(tableRequest);
        if (creation) {
            User user = loginUserService.getCurrentUser();
            entity.setModifiedBy(user.getUserId());
            entity.setCreatedBy(user.getUserId());
            entity.setCreatedDate(Instant.now());
            entity.setModifiedDate(Instant.now());
            entity.setStatus(StatusValue.ACTIVE);
            baseMapper.insert(entity);
            return entity;
        }
        throw new IllegalArgumentException( "Creation failed");
    }

    @Override
    public CaseTable getById(String id) {
        CaseTable caseTable = getById(id);
        return caseTable;
    }

    @Override
    public CaseTable getByTableName(String businessLabel) {
        String tableName = this.generateTableName(businessLabel);
        return findByTableName(tableName);
    }

    @Override
    public CaseTableResponseDTO getTableStructure(@NotNull String id) {
        CaseTable caseTable = getById(id);
        Assert.notNull(caseTable, "case table does not exist");
        CaseTableResponseDTO response = CaseTableResponseDTO.builder().build().assignValue(caseTable);
        List<MTColumnInfo> columns = jooqService.getTableStructure(caseTable.getTableName());
        response.setFields(columns);
        return response;
    }

    @Override
    public CaseTableResponseDTO addFields(CaseTableRequestDTO request) {
        Assert.notNull(request.getId(), "id must not be null");
        Assert.state(CollectionUtils.isNotEmpty(request.getFields()), "fields must not be null");
        CaseTable caseTable = getById(request.getId());
        Assert.notNull(caseTable, "case table does not exists");
        List<MTColumnInfo> columns = jooqService.getTableStructure(caseTable.getTableName());
        List<String> fieldNames = columns.stream().map(MTColumnInfo::getColumnName).collect(Collectors.toList());
        List<MTFieldInfo> fieldInfoList = request.getFields();
        for (MTFieldInfo fieldInfo : fieldInfoList) {
            if (!fieldNames.contains(fieldInfo.getFieldName())) {
                // added a new column to case table
                jooqService.addField(caseTable.getTableName(), fieldInfo);
            }
        }
        CaseTableResponseDTO response = CaseTableResponseDTO.builder().build().assignValue(caseTable);
        response.setFields(jooqService.getTableStructure(caseTable.getTableName()));
        return response;
    }

    @Override
    public boolean addSingleField(String caseTableId, MTFieldInfo fieldInfo) {
        CaseTable caseTable = getById(caseTableId);
        List<MTColumnInfo> columns = jooqService.getTableStructure(caseTable.getTableName());
        List<String> fieldNames = columns.stream().map(MTColumnInfo::getColumnName).collect(Collectors.toList());
        if (!fieldNames.contains(fieldInfo.getFieldName())) {
            // create new column to master table
            return jooqService.addField(caseTable.getTableName(), fieldInfo);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    public boolean dropTable(String id) {
        CaseTable caseTable = getById(id);
        Assert.notNull(caseTable, "case table does not exist");
        // check data is exists?
        List<LinkedHashMap<String, Object>> dataList = jooqService.findAllRecords(caseTable.getTableName());
        if (CollectionUtils.isNotEmpty(dataList)) {
            throw new IllegalArgumentException( "UnSupport delete table, because it has already records");
        }
        if (jooqService.dropTable(caseTable.getTableName(), true)) {
            baseMapper.deleteById(caseTable.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<CaseTable> findAllByCaseTypeId(String caseTypeId) {
        Assert.notNull(caseTypeId, "case type id cannot be null");
        CaseTable entity = new CaseTable();
        entity.setCaseTypeId(caseTypeId);
        QueryWrapper<CaseTable> waiter = new QueryWrapper<CaseTable>();
        return baseMapper.selectList(waiter);
    }

    @Override
    public List<CaseTable> findByIds(Set<String> caseTableIds) {
        return baseMapper.selectBatchIds(caseTableIds);
    }

    @Override
    public boolean dropColumn(String caseTableId, String columnName) {
        CaseTable caseTable = getById(caseTableId);
        // Check if not data?
        List<LinkedHashMap<String, Object>> list = jooqService.queryAllRecord(caseTable.getTableName());
        if (!list.isEmpty()) {
            throw new IllegalArgumentException( "Delete field is not allowed when data exists");
        }
        List<MTColumnInfo> columns = jooqService.getTableStructure(caseTable.getTableName());
        boolean found = false;
        for (MTColumnInfo column : columns) {
            if (column.getColumnName().equals(columnName)) {
                found = jooqService.dropField(caseTable.getTableName(), columnName);
            }
        }
        return found;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    public CaseTable save(CaseTableRequestDTO request) {
        CaseTable caseTable = checkCaseTable(request);
        if (caseTable == null) {
            return create(request);
        }
        // Alter table structure
        List<MTColumnInfo> columns = jooqService.getTableStructure(caseTable.getTableName());
        // 比较字段列表的差异 >> 只新增数据表
        List<MTFieldInfo> fields = request.getFields();
        // 是否检查字段类型符合要求
        verifyFieldSyntax(fields);
        // 检查下是否存在数据
        List<LinkedHashMap<String, Object>> dataList = jooqService.queryRecord(caseTable.getTableName(), "id", "id");
        if (dataList.isEmpty()) {
            List<MTFieldInfo> fixedFields = CaseTableConfig.getFixedFields();
            String finalTableName = caseTable.getTableName();
            columns.forEach(column -> {
                boolean notExists = fields.stream().noneMatch(fieldInfo -> fieldInfo.getFieldName().toLowerCase().equals(column.getColumnName().toLowerCase()));
                boolean nonFixed = fixedFields.stream().noneMatch(fieldInfo -> fieldInfo.getFieldName().toLowerCase().equals(column.getColumnName().toLowerCase()));
                if (notExists && nonFixed) {
                    // alter table to delete column
                    jooqService.dropField(finalTableName, column.getColumnName());
                }
            });
        } else {
            // 仅仅做排除法,对于已经创建的字段, 不允许再次更改结构(例如: 修改类型/长度/唯一列等等)
            fields.removeIf(fieldInfo -> columns.stream().anyMatch(columnInfo -> columnInfo.getColumnName().toLowerCase().equals(fieldInfo.getFieldName().toLowerCase())));
        }
        if (CollectionUtils.isNotEmpty(fields)) {
            request.setFields(fields);
            request.setId(caseTable.getId());
            addFields(request);
        }
        caseTable.setStatus(StatusValue.ACTIVE);
        caseTable.setModifiedDate(Instant.now());
        baseMapper.updateById(caseTable);
        return caseTable;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    public boolean insertRecord(CaseTableRequestDTO request) {
        Assert.notNull(request.getData(), "data can not empty.");
        Assert.notNull(request.getId(), "ID can not empty.");
        CaseTable tableInfo = getById(request.getId());
        User userDTO = loginUserService.getCurrentUserDTO();
        List<MTColumnInfo> fields = jooqService.getTableStructure(tableInfo.getTableName());
        // insert multiple record
        return jooqService.insertRecord(tableInfo.getTableName(), request.getData(), fields, userDTO.getUserId());
    }

    @Override
    public boolean insertRecord(String tableName, List<Map<String, Object>> data) {
        Assert.notNull(data, "data can not empty.");
        Assert.notNull(tableName, "ID can not empty.");
        CaseTable tableInfo = findByTableName(tableName);
        if (tableInfo == null) {
            throw new IllegalArgumentException( "case table does not exists");
        }
        User userDTO = loginUserService.getCurrentUserDTO();
        List<MTColumnInfo> fields = jooqService.getTableStructure(tableInfo.getTableName());
        // insert multiple record
        return jooqService.insertRecord(tableInfo.getTableName(), data, fields, userDTO.getUserId());
    }

    @Override
    public boolean updateRecord(String tableName, Map<String, Object> dataMap) {
        Assert.state(!dataMap.isEmpty(), "data can not empty.");
        Assert.notNull(tableName, "table name can not empty.");
        CaseTable caseTable = findByTableName(tableName);
        if (caseTable == null) {
            throw new IllegalArgumentException( "case table does not exists");
        }
        User userDTO = loginUserService.getCurrentUserDTO();
        List<MTColumnInfo> tableFields = jooqService.getTableStructure(caseTable.getTableName());
        String recordId = (String) dataMap.get(AbstractJooqService.ID);
        Map<String, MTColumnInfo> columnInfoMap = tableFields.stream().collect(Collectors.toMap(MTColumnInfo::getColumnName, a -> a, (k1, k2) -> k1));
        return jooqService.updateRecord(caseTable.getTableName(), recordId, dataMap, userDTO.getUserId(), columnInfoMap);
    }

    /**
     * Pagination Search (Master Table)
     *
     * @param request the request DTO of (Master Table)
     * @return PaginationDTO<MasterTable>
     */
    @Override
    public PaginationDTO<Map<String, Object>> recordPage(CaseTableRequestDTO request) {
        Assert.notNull(request.getId(), "ID can not empty.");
        CaseTable tableInfo = getById(request.getId());
        // Obtain all fields
        List<MTColumnInfo> columns = jooqService.getTableStructure(tableInfo.getTableName());
        // Build query condition
        Map<String, Object> condition = new HashMap<>();
        if (StringUtils.isNotBlank(request.getQ())) {
            for (MTColumnInfo column : columns) {
                condition.put(column.getColumnName(), request.getQ());
            }
        }
        return jooqService.page(request.getPageNum(), request.getPageSize(), tableInfo.getTableName(), condition);
    }

    @Override
    public List<LinkedHashMap<String, Object>> findCaseRecords(CaseTableRequestDTO request) {
        List<CaseTable> list = findAllByCaseTypeId(request.getCaseTypeId());
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        for (CaseTable caseTable : list) {
            if (caseTable.getLabel().equals(request.getLabel())) {
                resultList = jooqService.findAllRecords(caseTable.getTableName());
            }
        }
        return resultList;
    }

    @Override
    public List<LinkedHashMap<String, Object>> findTableRecord(String tableLabel, Map<String, Object> where) {
        CaseTable caseTable = getByTableName(tableLabel);
        if (null == caseTable) {
            throw new IllegalArgumentException( "case table does not exists");
        }
        return jooqService.queryData(caseTable.getTableName(), new HashMap<>());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
    public Boolean deleteByIds(List<String> caseTableIds) {
        List<CaseTable> caseTables = findByIds((Set<String>) caseTableIds);
        for (CaseTable caseTable : caseTables) {
            // drop all table
            jooqService.dropTable(caseTable.getTableName(), true);
            baseMapper.deleteById(caseTable.getId());
        }
        return true;
    }

    @Override
    public boolean disableById(String caseTableId) {
        CaseTable caseTable = getById(caseTableId);
        if (null != caseTable) {
            caseTable.setStatus(StatusValue.REMOVED);
            baseMapper.updateById(caseTable);
            return true;
        }
        return false;
    }

}
