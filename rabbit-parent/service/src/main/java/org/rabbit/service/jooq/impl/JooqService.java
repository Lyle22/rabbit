package org.rabbit.service.jooq.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.SQLDataType;
import org.jooq.util.mysql.MySQLDataType;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.common.utils.DateUtil;
import org.rabbit.service.jooq.*;
import org.rabbit.service.jooq.models.MTRecordRequestDTO;
import org.rabbit.service.jooq.models.TableRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JooqService implements AbstractJooqService {

    @Autowired
    private DefaultDSLContext dslContext;

    public DefaultDSLContext getDslContext() {
        return dslContext;
    }

    // To do create table (检查用户名是否存在空格，替换为下划线)
    public String generateTableName(String name) {
        // To do create table (检查用户名是否存在空格，替换为下划线)
        String tableName = TABLE_NAME_PREFIX + CommonUtils.replaceSpecialChar(name).toLowerCase() + "_";
        List<Table<?>> tables = dslContext.meta().getTables();
        // Table name format like "mt_{name}_{index}"
        List<String> nameIndexes = tables.stream().filter(item -> item.getName().startsWith(tableName))
                .map(item -> item.getName().replace(tableName, "")).collect(Collectors.toList());
        int[] indexes = new int[nameIndexes.size()];
        for (int i = 0; i < nameIndexes.size(); i++) {
            String index = nameIndexes.get(i);
            String[] arr = index.split("_");
            if (CommonUtils.isNumeric(arr[0]) && StringUtils.isNotBlank(index)) {
                indexes[i] = Integer.parseInt(arr[0]);
            }
        }
        int lastIndex = CommonUtils.largestNumber(indexes);
        return tableName + (lastIndex + 1);
    }

    public boolean createDataTable(TableRequestDTO request) {
        boolean isSuccess = false;
        try {
            String tableCreateSQL = "";
            String tableCreateIndexSQL = "";
            String tableName = request.getTable();
            List<MTFieldInfo> columns = request.getFields();
            Table<?> table = DSL.table(tableName);
            CreateTableColumnStep createTable = dslContext.createTable(table);
            for (MTFieldInfo fieldInfo : columns) {
                String fieldName = fieldInfo.getFieldName();
                boolean required = fieldInfo.getRequired();
                boolean unique = fieldInfo.getUnique();
                Boolean isPrimaryKey = fieldInfo.getPrimaryKey();
                // 根据数据类型创建列
                DataType<?> columnDataType = transferFieldDataType(fieldInfo);
                // 设置列的可空性 积分
                if (required) {
                    columnDataType = columnDataType.nullable(false);
                }
                Field<?> column = DSL.field(DSL.quotedName(fieldName), columnDataType);
                createTable.column(column);
                if (unique) {
                    createTable.constraint(DSL.unique(fieldName));
                }
                if (Boolean.TRUE.equals(isPrimaryKey)) {
                    createTable.constraint(DSL.primaryKey(fieldName));
                }
                // 不设置主键自增
//                columnDataType = columnDataType.identity(true);
                // 不加索引
//                boolean isIndex = false;
//                if (isIndex) {
//                    String indexFormat = "idx_" + tableName + "_" + fieldName;
//                    CreateIndexIncludeStep indexStep = dslContext.createIndex(indexFormat).on(table, column);
//                    tableCreateIndexSQL = indexStep.getSQL(ParamType.INLINED);
//                }
            }
            tableCreateSQL = createTable.getSQL(ParamType.INLINED);
            if (log.isDebugEnabled()) {
                log.debug("Table create sql:: {}", tableCreateSQL);
            }
            // Run table
            dslContext.execute(tableCreateSQL);
            // Run index
            if (StringUtils.isNotBlank(tableCreateIndexSQL)) {
                dslContext.execute(tableCreateIndexSQL);
            }
            isSuccess = true;
        } catch (Exception ex) {
            log.error("Failed to CREATE TABLE {} {} ", ex.getCause().getMessage(), ex.getMessage());
            isSuccess = false;
        }
        return isSuccess;
    }

    // 构建数据结构
    private DataType<?> transferFieldDataType(MTFieldInfo fieldInfo) {
        String dataType = fieldInfo.getDataType().toLowerCase();
        // 根据数据类型创建列
        DataType<?> columnDataType;
        if (dataType.equalsIgnoreCase("bigint")) {
            columnDataType = SQLDataType.BIGINT;
        } else if (dataType.equalsIgnoreCase("integer")) {
            columnDataType = SQLDataType.INTEGER;
        } else if (dataType.equalsIgnoreCase("timestamp")) {
            columnDataType = MySQLDataType.DATETIME;
        } else if (dataType.startsWith("varchar")) {
            int length = Integer.parseInt(dataType.substring(dataType.indexOf(":") + 1));
            columnDataType = SQLDataType.VARCHAR(length);
        } else if (dataType.equalsIgnoreCase("boolean")) {
            columnDataType = SQLDataType.BOOLEAN;
        } else if (dataType.equalsIgnoreCase("clob")) {
            columnDataType = SQLDataType.LONGVARCHAR;
        } else if (dataType.equalsIgnoreCase("decimal")) {
            columnDataType = SQLDataType.DECIMAL(19, 4);
        } else if (dataType.equalsIgnoreCase("json")) {
            columnDataType = SQLDataType.JSON;
        } else if (dataType.equalsIgnoreCase("relation")) {
            List<MTColumnInfo> columnInfos = getTableStructure("tableName");
            List<MTColumnInfo> matchs = columnInfos.stream().filter(field ->
                    field.getColumnName().equals(fieldInfo.getRelationField())).collect(Collectors.toList());
            MTColumnInfo columnInfo = matchs.get(0);
            String fieldDataType = columnInfo.getDataType();
            int len = columnInfo.getLength();
            if (len > 0) {
                fieldDataType = fieldDataType + ":" + len;
            }
            fieldInfo.setDataType(fieldDataType);
            return transferFieldDataType(fieldInfo);
        } else {
            columnDataType = SQLDataType.VARCHAR(255);
        }
        return columnDataType;
    }

    public boolean addFields(String tableName, List<MTFieldInfo> newFields) {
        Table<?> table = DSL.table(tableName);
        AlterTableStep alterTable = dslContext.alterTable(table);
        if (CollectionUtils.isNotEmpty(newFields)) {
            for (MTFieldInfo fieldInfo : newFields) {
                // New field into existing table
                String fieldName = fieldInfo.getFieldName();
                boolean required = fieldInfo.getRequired();
                // 根据数据类型创建列
                DataType<?> columnDataType = transferFieldDataType(fieldInfo);
                // 设置列的可空性 积分
                if (required) {
                    columnDataType = columnDataType.nullable(false);
                }
                Field<?> column = DSL.field(DSL.quotedName(fieldName), columnDataType);
                alterTable.add(column).execute();
            }
            return true;
        }
        return false;
    }

    public boolean addField(String tableName, MTFieldInfo fieldInfo) {
        if (null != fieldInfo && StringUtils.isNotBlank(fieldInfo.getFieldName())) {
            Table<?> table = DSL.table(tableName);
            AlterTableStep alterTable = dslContext.alterTable(table);
            // New field into existing table
            String fieldName = fieldInfo.getFieldName();
            boolean required = fieldInfo.getRequired();
            // 根据数据类型创建列
            DataType<?> columnDataType = transferFieldDataType(fieldInfo);
            // 设置列的可空性
            if (required) {
                columnDataType = columnDataType.nullable(false);
            }
            Field<?> column = DSL.field(DSL.quotedName(fieldName), columnDataType);
            alterTable.add(column).execute();
            return true;
        }
        return false;
    }

    public boolean dropTable(String tableName, boolean enforceable) {
        // check if table exists
        boolean exists = checkTableNameIfExists(tableName);
        if (!exists) {
            return true;
        }
        if (enforceable) {
            Table<?> table = DSL.table(tableName);
            dslContext.dropTableIfExists(table).execute();
            log.info("Perform drop of database table [{}]", tableName);
            return true;
        } else {
            // Query record
            List<LinkedHashMap<String, Object>> list = queryData(tableName, new HashMap<>());
            if (CollectionUtils.isEmpty(list)) {
                Table<?> table = DSL.table(tableName);
                dslContext.dropTableIfExists(table).execute();
                log.info("Perform drop of database table [{}]", tableName);
                return true;
            }
        }
        return false;
    }

    public boolean dropField(String tableName, String columnName) {
        if (log.isDebugEnabled()) {
            log.debug("Drop column is {} from table {}", columnName, tableName);
        }
        Table<?> table = DSL.table(tableName);
        AlterTableStep alterTable = dslContext.alterTable(table);
        if (StringUtils.isNotBlank(columnName)) {
            List<MTColumnInfo> columns = getTableStructure(tableName);
            if (columns.stream().anyMatch(item -> item.getColumnName().equals(columnName))) {
                alterTable.dropColumn(columnName).execute();
                return true;
            }
        }
        return false;
    }

    public List<MTColumnInfo> getTableStructure1(String tableName) {
        try {
            // 获取表结构信息
            Meta meta = dslContext.meta();
            List<Table<?>> tables = meta.getTables(tableName);
            Table<?> table = tables.get(0);
            List<MTColumnInfo> columnInfoList = new ArrayList<>();
            List<String> uniqueIds = queryUniqueField(tableName);
            // 获取表的列信息
            Field<?>[] fields = table.fields();
            for (Field<?> field : fields) {
                String columnName = field.getName();
                DataType<?> dataType = field.getDataType();
                Integer length = field.getDataType().length();
                boolean isRequired = field.getDataType().nullable();
                // 判断字段是否为主键
                boolean isPrimaryKey = Objects.requireNonNull(table.getPrimaryKey()).getFields().get(0).getName().equals(field.getName());
                MTColumnInfo columnInfo = new MTColumnInfo(columnName, dataType.getTypeName(), length, isPrimaryKey, !isRequired, uniqueIds.contains(columnName));
                columnInfoList.add(columnInfo);
            }
            return columnInfoList;
        } catch (Exception e) {
            log.error("查询表结构失败：" + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<MTColumnInfo> getTableStructure(String tableName) {
        try {
            List<MTColumnInfo> columnInfoList = new ArrayList<>();
            // 查询表的列信息
            Result<?> result = dslContext.fetch("SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_KEY FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?", tableName);
            for (Record record : result) {
                String columnName = record.getValue("COLUMN_NAME", String.class);
                String columnType = record.getValue("DATA_TYPE", String.class);
                String dataType = convertDataType(columnType);
                Integer length = record.getValue("CHARACTER_MAXIMUM_LENGTH", Integer.class);
                boolean isRequired = "NO".equals(record.getValue("IS_NULLABLE", String.class));
                boolean isPrimaryKey = "PRI".equals(record.getValue("COLUMN_KEY", String.class));
                boolean isUnique = "UNI".equals(record.getValue("COLUMN_KEY", String.class));
                MTColumnInfo columnInfo = new MTColumnInfo(columnName, dataType, length, isPrimaryKey, isRequired, (isUnique || isPrimaryKey));
                columnInfoList.add(columnInfo);
            }
            return columnInfoList;
        } catch (Exception e) {
            log.error("查询表结构失败：" + e.getMessage());
        }
        return new ArrayList<>();
    }

    public PaginationDTO<Map<String, Object>> page(int pageIndex, int pageSize, String tableName, Map<String, Object> whereArray) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        // Dynamically create field objects
        List<Field<?>> fields = new ArrayList<>();
        if (whereArray != null && !whereArray.isEmpty()) {
            for (Map.Entry<String, Object> entry : whereArray.entrySet()) {
                String fieldName = entry.getKey();
                Field<?> field = DSL.field(DSL.quotedName(fieldName));
                fields.add(field);
            }
        }
        // Build Query Sql
        SelectJoinStep<Record> selectQuery = dslContext.select(fields).from(table);
        // Add where conditions
        SelectConditionStep<Record> scs = selectQuery.where(DSL.noCondition());
        if (whereArray != null && !whereArray.isEmpty()) {
            for (Map.Entry<String, Object> entry : whereArray.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = (String) entry.getValue();
                Field<?> field = DSL.field(DSL.quotedName(fieldName));
                scs.or(field.like(PERCENT_SIGN + fieldValue + PERCENT_SIGN));
            }
        }

        SelectJoinStep countQuery = dslContext.selectCount().from(selectQuery);
        int totalRecords = (int) countQuery.fetchOne(0, int.class);
        if (pageSize > 0 && pageIndex >= 0) {
            selectQuery.limit(pageSize).offset(pageIndex * pageSize);
        }
        Result<Record> result = scs.orderBy(DSL.field(CREATED_DATE).asc()).fetch();
        List<Map<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            Map<String, Object> dataMap = new HashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return new PaginationDTO<>(resultList, totalRecords);
    }

    public Map<String, Object> findById(String tableName, String recordId) {
        Assert.notNull(tableName, "Table name can not null.");
        Assert.notNull(recordId, "Record ID can not null.");

        Table<Record> table = DSL.table(DSL.name(tableName));
        SelectJoinStep<Record> selectQuery = dslContext.select().from(table);
        selectQuery.where(DSL.field(DSL.name(ID)).eq(recordId));
        Record result = selectQuery.fetchOne();
        if (result == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        for (Field<?> field : result.fields()) {
            dataMap.put(field.getName(), result.get(field));
        }
        return dataMap;
    }

    public List<LinkedHashMap<String, Object>> findAllRecords(String tableName) {
        Assert.notNull(tableName, "table name can not null.");
        int pageNum = 0;
        int pageSize = 1000;
        PaginationDTO<LinkedHashMap<String, Object>> page = findAllRecords(pageNum, pageSize, tableName);
        int totalSize = page.getTotalSize();
        List<LinkedHashMap<String, Object>> resultList = page.getEntryList();
        while (totalSize > ((pageNum + 1) * pageSize)) {
            PaginationDTO<LinkedHashMap<String, Object>> pageDTO = findAllRecords(pageNum, pageSize, tableName);
            totalSize = pageDTO.getTotalSize();
            pageNum = pageNum + 1;
            resultList.addAll(pageDTO.getEntryList());
        }
        return resultList;
    }

    public PaginationDTO<LinkedHashMap<String, Object>> findAllRecords(int pageIndex, int pageSize, String tableName) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        List<MTColumnInfo> columns = getTableStructure(tableName);
        List<Field<?>> fields = new ArrayList<>();
        for (MTColumnInfo column : columns) {
            Field<?> field = DSL.field(DSL.name(column.getColumnName()));
            fields.add(field);
        }
        // Build Query Sql
        SelectJoinStep<Record> selectQuery = dslContext.select(fields).from(table);
        SelectJoinStep<? extends Record> countQuery = dslContext.selectCount().from(selectQuery);
        int totalRecords = countQuery.fetchOne(0, int.class);
        if (pageSize > 0 && pageIndex >= 0) {
            selectQuery.limit(pageSize).offset(pageIndex * pageSize);
        }
        Result<Record> result = selectQuery.orderBy(DSL.field(CREATED_DATE).asc()).fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return new PaginationDTO<>(resultList, totalRecords);
    }

    public List<LinkedHashMap<String, Object>> queryAllRecord(String tableName) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        List<MTColumnInfo> columns = getTableStructure(tableName);
        List<Field<?>> fields = new ArrayList<>();
        for (MTColumnInfo column : columns) {
            Field<?> field = DSL.field(DSL.name(column.getColumnName()));
            fields.add(field);
        }
        // Build Query Sql
        SelectJoinStep<Record> selectQuery = dslContext.select(fields).from(table);
        Result<Record> result = selectQuery.orderBy(DSL.field(CREATED_DATE).asc()).fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return resultList;
    }

    public List<LinkedHashMap<String, Object>> queryRecord(String tableName, String filterField, String displayField) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        List<Field<?>> fields = new ArrayList<>();
        fields.add(DSL.field(DSL.name(filterField)));
        fields.add(DSL.field(DSL.name(displayField)));
        // Build Query Sql
        SelectJoinStep<Record> selectQuery = dslContext.select(fields).from(table);
        Result<Record> result = selectQuery.orderBy(DSL.field(CREATED_DATE).asc()).fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return resultList;
    }

    /**
     * Query all unique fields of this table
     *
     * @param tableName the name of the master table
     * @return List<String> the unique field name
     */
    public List<String> queryUniqueField(String tableName) {
        Result<Record> result = dslContext.resultQuery("SHOW COLUMNS FROM " + tableName).fetch();
        List<String> uniqueFields = new ArrayList<>();
        for (Record r : result) {
            if (r.get("Key").toString().equalsIgnoreCase("UNI") || r.get("Key").toString().equalsIgnoreCase("PRI")) {
                uniqueFields.add((String) r.get("Field"));
            }
        }
        return uniqueFields;
    }

    public List<String> queryFields(String tableName) {
        Result<Record> result = dslContext.resultQuery("SHOW COLUMNS FROM " + tableName).fetch();
        List<String> fields = new ArrayList<>();
        for (Record r : result) {
            fields.add((String) r.get("Field"));
        }
        return fields;
    }

    public List<String> queryRequiredField(String tableName) {
        Result<Record> result = dslContext.resultQuery("SHOW COLUMNS FROM " + tableName).fetch();
        List<String> fields = new ArrayList<>();
        for (Record r : result) {
            if (r.get("Null").toString().equalsIgnoreCase("NO")) {
                fields.add((String) r.get("Field"));
            }
        }
        fields.removeIf(FIXED_FIELDS::contains);
        return fields;
    }

    public boolean checkTableNameIfExists(String tableName) {
        Result<Record> result = dslContext.resultQuery("SHOW TABLES").fetch();
        List<String> resultList = new ArrayList<>();
        for (Record data : result) {
            for (Field<?> field : data.fields()) {
                resultList.add(data.get(field).toString());
            }
        }
        return resultList.contains(tableName);
    }

    private void checkRequired(Map<String, Object> dataMap, List<String> requiredFields) {
        for (String requiredField : requiredFields) {
            String errorMessage = String.format("Field[%s] must have a value.", requiredField);
            if (dataMap.get(requiredField) == null || Objects.isNull(dataMap.get(requiredField))) {
                throw new IllegalArgumentException(errorMessage);
            }
            String fieldValue = String.valueOf(dataMap.get(requiredField));
            if (StringUtils.isBlank(fieldValue)) {
                throw new IllegalArgumentException(errorMessage);
            }
            if (fieldValue.equals("null")) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    private boolean checkValidation(String tableName, String fieldName, String fieldValue, List<String> uniqueFields) {
        List<MTColumnInfo> fields = getTableStructure(tableName);
        List<String> tableFields = fields.stream().map(MTColumnInfo::getColumnName).collect(Collectors.toList());
        // 1.1 filter data if non-table column
        if (!tableFields.contains(fieldName)) {
            log.warn("Does not exist field::[{}]", fieldName);
            return false;
        }
        // 1.2 Check data for uniqueness?
        if (CollectionUtils.isNotEmpty(uniqueFields) && uniqueFields.contains(fieldName) && !checkUnique(tableName, fieldName, fieldValue).isEmpty()) {
            String errorMessage = String.format("Field[%s] must be unique value, You need to update this value.", fieldName);
            throw new IllegalArgumentException(errorMessage);
        }
        // 1.3 类型校验
        for (MTColumnInfo columnInfo : fields) {
            if (!columnInfo.getColumnName().equals(fieldName)) {
                continue;
            }
            if (null != fieldValue && columnInfo.getDataType().equals("bigint")) {
                if (!CommonUtils.isNumeric(fieldValue)) {
                    throw new IllegalArgumentException(String.format("Field [%s] must be numeric value.", fieldName));
                }
                if (fieldValue.length() >= 20 || new BigDecimal(fieldValue).compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                    throw new IllegalArgumentException(String.format("Field [%s] value range is -9223372036854775808 to 9223372036854775807.", fieldName));
                }
            }
            if (fieldValue != null && columnInfo.getDataType().equals("boolean") || columnInfo.getDataType().equals("bit")) {
                if (!(("true").equalsIgnoreCase(fieldValue) || ("false").equalsIgnoreCase(fieldValue))) {
                    throw new IllegalArgumentException(String.format("Field [%s] must be boolean value.", fieldName));
                }
            }
            if (columnInfo.getDataType().equals("timestamp")) {
                if (null == fieldValue || StringUtils.isBlank(fieldValue)) {
                    fieldValue = null;
                } else {
                    Date data = DateUtil.formatDate(fieldValue);
                    if (data == null) {
                        System.out.println(String.format("Field [" + fieldName + "] must be date type. value::" + fieldValue));
                        throw new IllegalArgumentException(String.format("Field [%s] must be date type. value::[%s]", fieldName, fieldValue));
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(fieldValue) && fieldName.equals(ID) && CollectionUtils.isNotEmpty(queryById(tableName, fieldValue))) {
            log.warn("This record have bean exists. Field::[{}] value::[{}]", fieldName, fieldValue);
            return false;
        }
        return true;
    }

    public MTDataValidateDTO insertOneRecord(String tableName, Map<String, Object> dataMap, String creator, Map<String, MTColumnInfo> columnInfoMap) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        InsertQuery<Record> insertQuery = dslContext.insertQuery(table);
        // Check value is unique
        List<String> allFields = queryFields(tableName);
        List<String> uniqueFields = queryUniqueField(tableName);
        List<String> requiredFields = queryRequiredField(tableName);
        String errorMessage = validateRequired(dataMap, requiredFields);
        if (StringUtils.isNotBlank(errorMessage)) {
            return MTDataValidateDTO.builder().validate(false).validateResult(List.of(errorMessage)).build();
        }
        insertQuery.addValue(DSL.field(DSL.name(ID)), UUID.randomUUID().toString());
        insertQuery.addValue(DSL.field(DSL.name(CREATED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
        insertQuery.addValue(DSL.field(DSL.name(MODIFIED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
        insertQuery.addValue(DSL.field(DSL.name(MODIFIED_BY)), creator);
        insertQuery.addValue(DSL.field(DSL.name(STATUS)), true);
        if (allFields.contains(CREATED_BY)) {
            insertQuery.addValue(DSL.field(DSL.name(CREATED_BY)), creator);
        }
        List<String> validateResults = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            // Pre-check data format for insert data
            MTDataValidateDTO validateDTO = validationData(tableName, columnInfoMap, fieldName, String.valueOf(fieldValue), uniqueFields);
            if (!validateDTO.isValidate()) {
                validateResults.add(validateDTO.getErrorMessage());
                continue;
            }
            // Prepare handle data
            MTColumnInfo columnInfo = columnInfoMap.get(fieldName);
            if ("bit".equals(columnInfo.getDataType())) {
                // Convert data type using mapping
                if (fieldValue instanceof Boolean) {
                    fieldValue = (boolean) fieldValue;
                }
                if (null == fieldValue || StringUtils.isBlank(String.valueOf(fieldValue))) {
                    fieldValue = false;
                }
                if (("true").equalsIgnoreCase(String.valueOf(fieldValue))) {
                    fieldValue = true;
                }
                if (("false").equalsIgnoreCase(String.valueOf(fieldValue))) {
                    fieldValue = false;
                }
            }
            insertQuery.addValue(DSL.field(DSL.quotedName(fieldName)), fieldValue);
        }
        try {
            if (validateResults.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("Insert row data into master table [{}] , SQL::[ {} ] ", tableName, insertQuery.getSQL(ParamType.INLINED));
                }
                int insertRow = insertQuery.execute();
                return MTDataValidateDTO.builder().validate(insertRow > 0).validateResult(validateResults).build();
            } else {
                log.warn("Failed to insert row data, because data was check failed. {}, ", validateResults);
            }
        } catch (DataIntegrityViolationException dve) {
            log.error("Insert Data into table was failure. [{}]", dve.getMessage());
            validateResults.add("Not correct field value format");
        } catch (Exception ex) {
            log.error("Insert Data into table was failure. [{}]", ex.getMessage());
            validateResults.add("Execute insert data was failure " + ex.getMessage());
        }
        return MTDataValidateDTO.builder().validate(false).validateResult(validateResults).build();
    }

    /**
     * Insert multiple data
     *
     * @param tableName    table name, must be non-empty
     * @param dataList     data to insert
     * @param tableColumns the columns of this table
     * @param userId       the login-ed user
     * @return boolean
     */
    public boolean insertRecord(String tableName, List<Map<String, Object>> dataList, List<MTColumnInfo> tableColumns, String userId) {
        try {
            List<String> allFields = queryFields(tableName);
            List<String> uniqueFields = queryUniqueField(tableName);
            List<String> requiredFields = queryRequiredField(tableName);
            // 动态创建表对象
            Table<Record> table = DSL.table(DSL.name(tableName));
            // Prepare handle data
            int successCount = 0;
            Map<String, MTColumnInfo> columnInfoMap = tableColumns.stream().collect(Collectors.toMap(MTColumnInfo::getColumnName, a -> a, (k1, k2) -> k1));
            for (Map<String, Object> dataMap : dataList) {
                checkRequired(dataMap, requiredFields);
                boolean found = true;
                InsertQuery<Record> insertQuery = dslContext.insertQuery(table);
                insertQuery.addValue(DSL.field(DSL.name(ID)), UUID.randomUUID().toString());
                insertQuery.addValue(DSL.field(DSL.name(CREATED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
                insertQuery.addValue(DSL.field(DSL.name(MODIFIED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
                insertQuery.addValue(DSL.field(DSL.name(MODIFIED_BY)), userId);
                insertQuery.addValue(DSL.field(DSL.name(STATUS)), true);
                if (allFields.contains(CREATED_BY)) {
                    insertQuery.addValue(DSL.field(DSL.name(CREATED_BY)), userId);
                }
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    if (null == fieldValue || StringUtils.isBlank(String.valueOf(fieldValue))) {
                        fieldValue = null;
                        continue;
                    }
                    // Prepare handle data
                    if (!checkValidation(tableName, fieldName, String.valueOf(fieldValue), uniqueFields)) {
                        found = false;
                        continue;
                    }
                    // Convert data type using mapping
                    if (fieldValue instanceof Boolean) {
                        fieldValue = (boolean) fieldValue;
                    }
                    if (("true").equals(fieldValue)) {
                        fieldValue = true;
                    }
                    if (("false").equals(fieldValue)) {
                        fieldValue = false;
                    }
                    MTColumnInfo columnInfo = columnInfoMap.get(fieldName);
                    if ("timestamp".equals(columnInfo.getDataType())) {
                        Date date = DateUtil.formatDate((String) fieldValue);
                        insertQuery.addValue(DSL.field(DSL.quotedName(fieldName)), DateFormatUtils.format(date, DATE_FORMAT));
                    } else {
                        insertQuery.addValue(DSL.field(DSL.quotedName(fieldName)), fieldValue);
                    }
                }
                if (found) {
                    successCount = successCount + insertQuery.execute();
                }
            }
            return successCount > 0;
        } catch (DataAccessException ex) {
            log.error("Insert Data into table was failure ：" + ex.getMessage());
            throw new IllegalArgumentException(ex.getMessage());
        } catch (DataIntegrityViolationException dve) {
            log.error("Failed to insert data [{}]", dve.getMessage());
            throw new IllegalArgumentException("Data format not correct");
        }
    }

    public boolean insertRecord(String tableName, List<Map<String, Object>> dataList) {
        try {
            Table<Record> table = DSL.table(DSL.name(tableName));
            int successCount = 0;
            for (Map<String, Object> dataMap : dataList) {
                InsertQuery<Record> insertQuery = dslContext.insertQuery(table);
                if (!dataMap.keySet().contains(ID)) {
                    insertQuery.addValue(DSL.field(DSL.name(ID)), UUID.randomUUID().toString());
                }
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String columnName = entry.getKey();
                    Object columnValue = entry.getValue();
                    if (null == columnValue || StringUtils.isBlank(String.valueOf(columnValue))) {
                        columnValue = null;
                        continue;
                    }
                    insertQuery.addValue(DSL.field(DSL.quotedName(columnName)), columnValue);
                }
                successCount = successCount + insertQuery.execute();
            }
            return successCount > 0;
        } catch (DataAccessException ex) {
            log.error("Insert Data into table was failure ：" + ex.getMessage());
            throw new IllegalArgumentException(ex.getMessage());
        } catch (DataIntegrityViolationException dve) {
            log.error("Failed to insert data [{}]", dve.getMessage());
            throw new IllegalArgumentException("Data format not correct");
        }
    }

    public List<LinkedHashMap<String, Object>> queryData(String tableName, Map<String, Object> where) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        // Build Query Sql
        SelectJoinStep<Record> selectQuery = dslContext.select().from(table);
        // Add WHERE condition
        for (Map.Entry<String, Object> entry : where.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            selectQuery.where(DSL.field(DSL.quotedName(fieldName)).eq(fieldValue));
        }
        if (!where.isEmpty()) {
            selectQuery.orderBy(DSL.field(CREATED_DATE).asc());
        }
        Result<Record> result = selectQuery.fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return resultList;
    }

    @Override
    public List<LinkedHashMap<String, Object>> queryById(String tableName, String id) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        // Build Query Sql
        SelectJoinStep<Record> selectQuery = dslContext.select().from(table);
        selectQuery.where(DSL.field(DSL.name(ID)).eq(id));
        Result<Record> result = selectQuery.fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return resultList;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getRowData(String name, String columnName, Object columnValue) {
        return null;
    }

    /**
     * 检查字段的值 在数据库中是否唯一？
     *
     * @param tableName  the table name
     * @param fieldName  the field name on this table
     * @param fieldValue the field value
     * @return Map<String, Object> return data map，return empty map if this value does not exist
     */
    @Override
    public Map<String, Object> checkUnique(String tableName, String fieldName, String fieldValue) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        SelectJoinStep<Record> selectQuery = dslContext.select().from(table);
        // Add WHERE condition
        selectQuery.where(DSL.field(DSL.quotedName(fieldName)).eq(fieldValue));
        Record data = selectQuery.fetchOne();
        if (data != null) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            return dataMap;
        }
        return Collections.emptyMap();
    }

    /**
     * Update data based on Record ID
     */
    public boolean updateRecord(String tableName, String recordId, Map<String, Object> dataMap, String userId, Map<String, MTColumnInfo> columnInfoMap) {
        try {
            // Build an update statement
            Table<Record> table = DSL.table(DSL.name(tableName));
            UpdateSetMoreStep<Record> updateQuery = (UpdateSetMoreStep<Record>) dslContext.update(table);
            // Prepare handle data
            List<String> tableFields = getTableStructure(tableName).stream().map(MTColumnInfo::getColumnName).collect(Collectors.toList());
            List<String> requiredFields = queryRequiredField(tableName);
            // Add changed data
            checkRequired(dataMap, requiredFields);
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                MTColumnInfo columnInfo = columnInfoMap.get(fieldName);
                if ("bit".equals(columnInfo.getDataType())) {
                    if (null == fieldValue || StringUtils.isBlank(String.valueOf(fieldValue))) {
                        fieldValue = false;
                    } else {
                        if (("true").equalsIgnoreCase(String.valueOf(fieldValue))) {
                            fieldValue = true;
                        }
                        if (("false").equalsIgnoreCase(String.valueOf(fieldValue))) {
                            fieldValue = false;
                        }
                    }
                }
                // 1.1 filter data if non-table column
                if (!tableFields.contains(fieldName) || null == fieldValue) {
                    continue;
                }
                if ("timestamp".equals(columnInfo.getDataType())) {
                    Date date = DateUtil.formatDate((String) fieldValue);
                    updateQuery.set(DSL.field(DSL.quotedName(fieldName)), DateFormatUtils.format(date, DATE_FORMAT));
                } else {
                    updateQuery.set(DSL.field(DSL.quotedName(fieldName)), fieldValue);
                }
            }
            updateQuery.set(DSL.field(DSL.name(MODIFIED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
            updateQuery.set(DSL.field(DSL.name(MODIFIED_BY)), userId);
            // whereby record id
            updateQuery.where(DSL.field(DSL.quotedName(ID)).eq(recordId));
            if (log.isDebugEnabled()) {
                log.debug("Update record sql : {}", updateQuery.getSQL(ParamType.INLINED));
            }
            // 执行更新操作
            int updated = updateQuery.execute();
            return updated > 0;
        } catch (DataIntegrityViolationException dve) {
            log.error("Failed to insert data [{}]", dve.getMessage());
            throw new IllegalArgumentException("Data format not correct");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(MTInsertRecord request) {
        try {
            String tableName = request.getTable();
            // 查询出筛选的数据
            List<LinkedHashMap<String, Object>> updatedData = queryData(tableName, request.getWhere());
            // 根据筛选的数据去检查是否缺失必填项

            // 根据筛选的数据去检查是否唯一值 并且排除自身（问题是怎么知道自身？）
            List<Map<String, Object>> dataList = request.getData();
            Map<String, Object> whereArray = request.getWhere();
            // Build an update statement
            Table<Record> table = DSL.table(DSL.name(tableName));
            UpdateSetMoreStep<Record> update = (UpdateSetMoreStep<Record>) dslContext.update(table);
            // Prepare handle data
            List<String> tableFields = getTableStructure(tableName).stream().map(MTColumnInfo::getColumnName).collect(Collectors.toList());
            List<String> uniqueFields = queryUniqueField(tableName);
            List<String> requiredFields = queryRequiredField(tableName);
            // Add changed data
            for (Map<String, Object> dataMap : dataList) {
                checkRequired(dataMap, requiredFields);
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    // 1.1 filter data if non-table column
                    if (!tableFields.contains(fieldName) || null == fieldValue) {
                        continue;
                    }
                    // 1.2 Check data for uniqueness?
                    if (CollectionUtils.isNotEmpty(uniqueFields) && uniqueFields.contains(fieldName)) {
                        Map<String, Object> uniqueMap = checkUnique(tableName, fieldName, String.valueOf(fieldValue));
                        if (!uniqueMap.isEmpty() && uniqueMap.get(fieldName).equals(fieldValue)) {
                            String errorMessage = String.format("Field[%s] must be unique value, You need to update this value.", fieldName);
                            throw new IllegalArgumentException(errorMessage);
                        }
                    }
                    update.set(DSL.field(DSL.quotedName(fieldName)), fieldValue);
                }
                update.set(DSL.field(DSL.quotedName(MODIFIED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
                update.set(DSL.field(DSL.quotedName(MODIFIED_BY)), request.getUserId());
            }
            // Add WHERE condition
            for (Map.Entry<String, Object> entry : whereArray.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                update.where(DSL.field(DSL.quotedName(fieldName)).eq(fieldValue));
            }
            // 执行更新操作
            int updated = update.execute();
            return updated > 0;
        } catch (Exception ex) {
            log.error("Failed to CREATE TABLE {} {} ", ex.getCause().getMessage(), ex.getMessage());
            return false;
        }
    }

    public boolean removeData(MTInsertRecord request) {
        try {
            String tableName = request.getTable();
            List<Map<String, Object>> dataList = request.getData();
            int successCount = 0;
            int result = 0;
            for (Map<String, Object> dataMap : dataList) {
                // Build delete sql condition
                Condition condition = null;
                // Create delete operation
                DeleteQuery<Record> deleteQuery = dslContext.deleteQuery(DSL.table(tableName));
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    // 创建等于条件
                    Condition eqCondition = DSL.field(DSL.quotedName(fieldName)).eq(fieldValue);
                    // 使用OR关系连接条件
                    if (condition == null) {
                        condition = eqCondition;
                    } else {
                        condition = condition.or(eqCondition);
                    }
                }
                // 设置条件到删除操作中
                deleteQuery.addConditions(condition);
                result = deleteQuery.execute();
                successCount = successCount + result;
            }
            return successCount == dataList.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Physical Delete record from database table
     *
     * @param tableName the table name of database
     * @param recordId  the record id name of database
     * @return boolean if true has success
     */
    public boolean removeData(String tableName, String recordId) {
        try {
            // Create delete operation
            DeleteQuery<Record> deleteQuery = dslContext.deleteQuery(DSL.table(tableName));
            Condition eqCondition = DSL.field(ID).eq(recordId);
            // 设置条件到删除操作中
            deleteQuery.addConditions(eqCondition);
            return deleteQuery.execute() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Update record status
     *
     * @param tableName   the name of master table
     * @param recordId    record id
     * @param statusValue value
     * @param userId      login-ed user
     * @return boolean true if successful and false otherwise
     */
    public boolean updateRecordStatus(String tableName, String recordId, boolean statusValue, String userId) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        UpdateSetMoreStep<Record> updateQuery = (UpdateSetMoreStep<Record>) dslContext.update(table);
        updateQuery.set(DSL.field(DSL.quotedName(STATUS)), statusValue);
        updateQuery.set(DSL.field(DSL.name(MODIFIED_DATE)), DateFormatUtils.format(new Date(), DATE_FORMAT));
        updateQuery.set(DSL.field(DSL.name(MODIFIED_BY)), userId);
        updateQuery.where(DSL.field(DSL.quotedName(ID)).eq(recordId));
        // 执行更新操作
        int updated = updateQuery.execute();
        return updated > 0;
    }

    /**
     * Query records of this master table through filter parameters
     *
     * @param request         the parameters
     * @param masterTableName the master table
     * @param relations       the list of relation master table
     * @return PaginationDTO
     */
    public PaginationDTO<Map<String, Object>> findRecordPage(MTRecordRequestDTO request, String masterTableName, List<MasterTableRelationDTO> relations) {
        SelectJoinStep<Record> selectQuery = this.buildRecordSelectQuery(request, masterTableName, relations);
        SelectJoinStep countQuery = dslContext.selectCount().from(selectQuery);
        int totalRecords = (int) countQuery.fetchOne(0, int.class);
        selectQuery.limit(request.getPageSize()).offset(request.getPageNum() * request.getPageSize());
        Result<Record> result = selectQuery.fetch();
        List<Map<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            Field<?>[] displayFields = data.fields();
            Map<String, Object> dataMap = new HashMap<>();
            for (Field<?> field : displayFields) {
                Object fieldValue = data.get(field);
                dataMap.put(field.getName(), fieldValue);
            }
            resultList.add(dataMap);
        }
        return new PaginationDTO<>(resultList, totalRecords);
    }

    public List<Map<String, Object>> findRecords(MTRecordRequestDTO request, String masterTableName, List<MasterTableRelationDTO> relations) {
        SelectJoinStep<Record> selectQuery = this.buildRecordSelectQuery(request, masterTableName, relations);
        Result<Record> result = selectQuery.fetch();
        LinkedList<Map<String, Object>> resultList = new LinkedList<>();
        for (Record data : result) {
            Field<?>[] displayFields = data.fields();
            Map<String, Object> dataMap = new HashMap<>();
            for (Field<?> field : displayFields) {
                Object fieldValue = data.get(field);
                dataMap.put(field.getName(), fieldValue);
            }
            resultList.add(dataMap);
        }
        return resultList;
    }

    private SelectJoinStep<Record> buildRecordSelectQuery(MTRecordRequestDTO request, String masterTableName, List<MasterTableRelationDTO> relations) {
        // Obtain list of relation table for building query sql
        for (int i = 0; i < relations.size(); i++) {
            MasterTableRelationDTO relationTable = relations.get(i);
            String tableAlais = String.format("%s_%s", relationTable.getRealJoinTableName(), i);
            relationTable.setJoinTableAlais(tableAlais);
        }
        List<MTColumnInfo> columns = getTableStructure(masterTableName);
        // 1. 构建查询字段列表
        Name masterTableAlais = DSL.name(masterTableName);
        // 1.1 创建主表的查询字段
        List<Field<?>> displayFields = columns.stream().map(field -> {
            if (field.getDataType().equals("timestamp")) {
                Name fieldName = masterTableAlais.append(field.getColumnName());
                Field<Date> dateField = DSL.field(fieldName, Date.class);
                return dateFormat(dateField, "%Y-%m-%dT%H:%i:%s.000Z").as(field.getColumnName());
            } else {
                return DSL.field(masterTableAlais.append(field.getColumnName()));
            }
        }).collect(Collectors.toList());
        // 1.2 创建关联表的查询字段
        String relationPrefix = "Relation";
        for (int i = 0; i < relations.size(); i++) {
//            MasterTableRelationDTO re = relations.get(i);
//            // column alais name will likely be : Relation_title
//            Field<?> displayField = DSL.field(DSL.name(re.getJoinTableAlais(), re.getDisplayFieldNames()))
//                    .as(String.format("%s_%s", relationPrefix, re.getSourceFieldName()));
//            displayFields.add(displayField);
        }

        // 2. 构建查询语句
        SelectJoinStep<Record> selectQuery = dslContext.select(displayFields).from(masterTableName);
        // LEFT JOIN relation table
        for (int i = 0; i < relations.size(); i++) {
//            MasterTableRelationDTO relationTable = relations.get(i);
//            Table<?> joinTable = DSL.table(DSL.name(relationTable.getRealJoinTableName())).as(relationTable.getJoinTableAlais());
//            Field<?> sourceField = DSL.field(DSL.name(masterTableName, relationTable.getSourceFieldName()));
//            Field joinField = DSL.field(DSL.name(relationTable.getJoinTableAlais(), relationTable.getJoinFieldName()));
//            selectQuery.leftOuterJoin(joinTable).on(sourceField.eq(joinField));
        }

        // Build where conditions sql
        SelectConditionStep<Record> scs = selectQuery.where(DSL.noCondition());
        if (StringUtils.isNotBlank(request.getQ())) {
            for (MTColumnInfo column : columns) {
                String fieldName = column.getColumnName();
                String fieldValue = request.getQ();
                Field<?> field = DSL.field(DSL.name(masterTableName, fieldName));
                scs.or(field.like("%" + fieldValue + "%"));
            }
            for (int i = 0; i < relations.size(); i++) {
//                MasterTableRelationDTO relationTable = relations.get(i);
//                String fieldName = relationTable.getDisplayFieldNames();
//                String fieldValue = request.getQ();
//                Field<?> field = DSL.field(DSL.name(relationTable.getJoinTableAlais(), fieldName));
//                scs.or(field.like("%" + fieldValue + "%"));
            }
        }

        if (null != request.getWhere() && !request.getWhere().isEmpty()) {
            Map<String, MTColumnInfo> columnInfoMap = columns.stream().collect(Collectors.toMap(MTColumnInfo::getColumnName, a -> a, (k1, k2) -> k1));
            for (Map.Entry<String, Object> entry : request.getWhere().entrySet()) {
                String columnName = entry.getKey();
                Object fieldValue = entry.getValue();
                if (fieldValue == null || StringUtils.isBlank(String.valueOf(fieldValue))) {
                    continue;
                }
                if (columns.stream().anyMatch(column -> column.getColumnName().equals(columnName))) {
                    MTColumnInfo columnInfo = columnInfoMap.get(columnName);
                    String dataType = columnInfo.getDataType();
                    if (fieldValue instanceof Boolean) {
                        Field<Boolean> field = DSL.field(DSL.quotedName(masterTableName, columnName), Boolean.class);
                        scs.and(field.eq((boolean) fieldValue));
                    } else if (fieldValue instanceof Integer) {
                        Field<Integer> field = DSL.field(DSL.quotedName(masterTableName, columnName), Integer.class);
                        scs.and(field.eq((Integer) fieldValue));
                    } else if (fieldValue instanceof String) {
                        if ("timestamp".equals(dataType)) {
                            Date date = DateUtil.formatDate((String) fieldValue);
                            Field<Date> dateField = DSL.field(DSL.name(masterTableName, columnName), Date.class);
                            scs.and(dateField.eq(date));
                        } else {
                            Field<String> field = DSL.field(DSL.quotedName(masterTableName, columnName), String.class);
                            scs.and(field.eq((String) fieldValue));
                        }
                    } else if (fieldValue instanceof Double) {
                        Field<Double> field = DSL.field(DSL.quotedName(masterTableName, columnName), Double.class);
                        scs.and(field.eq((Double) fieldValue));
                    } else if (fieldValue instanceof Date) {
                        Date date = DateUtil.formatDate((String) fieldValue);
                        Field<Date> dateField = DSL.field(DSL.name(masterTableName, columnName), Date.class);
                        scs.and(dateField.eq(date));
                    } else {
                        Field<?> field = DSL.field(DSL.name(masterTableName, columnName));
                        scs.and(field.equalIgnoreCase(String.valueOf(fieldValue)));
                    }
                } else {
                    log.warn("It is not a column in the table [{}] ", columnName);
                }
            }
        }
        // 排序
        if (StringUtils.isBlank(request.getOrderBy())) {
            request.setOrderBy(JooqService.CREATED_DATE);
        }
        Field<?> orderField = DSL.field(DSL.name(masterTableName, request.getOrderBy()));
        SortOrder sortOrder = Boolean.TRUE.equals(request.getIsDesc()) ? SortOrder.DESC : SortOrder.ASC;
        SortField<?> sortField = orderField.sort(sortOrder);
        selectQuery.orderBy(sortField);
        return selectQuery;
    }

    public List<LinkedHashMap<String, Object>> caseQuery(String tableName, String groupColumnName, List<String> columnNames) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        List<Field<?>> fields = new ArrayList<>();
        Field<Object> groupField = DSL.field(DSL.quotedName(groupColumnName));
        fields.add(groupField);
        // build select field [max(case `column_name` when 'query column name' then `column_value` end) as `query column name`]
        for (String columnName : columnNames) {
            Field<Object> columnNameField = DSL.field(DSL.quotedName(COLUMN_NAME));
            Field<Object> valueField = DSL.field(DSL.quotedName(COLUMN_VALUE));
            fields.add(DSL.max(DSL.choose(columnNameField).when(columnName, valueField)).as(columnName));
        }
        SelectJoinStep<Record> selectQuery = dslContext.select(fields).from(table);
        GroupField groupByField = DSL.field(DSL.quotedName(groupColumnName));
        selectQuery.groupBy(groupByField);

        Result<Record> result = selectQuery.fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return resultList;
    }


    public PaginationDTO<List<LinkedHashMap<String, Object>>> caseQuery(
            String tableName, int pageNum, int pageSize, String groupColumnName, List<String> columnNames
    ) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        List<Field<?>> fields = new ArrayList<>();
        Field<Object> groupField = DSL.field(DSL.quotedName(groupColumnName));
        fields.add(groupField);
        // build select field [max(case `column_name` when 'query column name' then `column_value` end) as `query column name`]
        for (String columnName : columnNames) {
            Field<Object> columnNameField = DSL.field(DSL.quotedName(COLUMN_NAME));
            Field<Object> valueField = DSL.field(DSL.quotedName(COLUMN_VALUE));
            fields.add(DSL.max(DSL.choose(columnNameField).when(columnName, valueField)).as(columnName));
        }
        SelectJoinStep<Record> selectQuery = dslContext.select(fields).from(table);
        GroupField groupByField = DSL.field(DSL.quotedName(groupColumnName));
        selectQuery.groupBy(groupByField);
        SelectJoinStep countQuery = dslContext.selectCount().from(selectQuery);
        int totalRecords = (int) countQuery.fetchOne(0, int.class);

        selectQuery.limit(pageSize).offset(pageNum * pageSize);
        Result<Record> result = selectQuery.fetch();
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        PaginationDTO<List<LinkedHashMap<String, Object>>> paginationDTO = new PaginationDTO<>();
        paginationDTO.setEntryList(Collections.singletonList(resultList));
        paginationDTO.setTotalSize(totalRecords);
        return paginationDTO;
    }

    public List<LinkedHashMap<String, Object>> casePage(String tableName, String groupColumnName, List<String> columnNames, Map<String, String> whereMap) {
        Table<Record> table = DSL.table(DSL.name(tableName));
        List<Field<?>> fields = new ArrayList<>();
        Field<Object> groupField = DSL.field(DSL.quotedName(groupColumnName));
        fields.add(groupField);
        // build select field [max(case `column_name` when 'query column name' then `column_value` end) as `query column name`]
        for (String columnName : columnNames) {
            Field<Object> columnNameField = DSL.field(DSL.quotedName(COLUMN_NAME));
            Field<Object> valueField = DSL.field(DSL.quotedName(COLUMN_VALUE));
            fields.add(DSL.max(DSL.choose(columnNameField).when(columnName, valueField)).as(columnName));
        }
        SelectJoinStep<Record> subquery = dslContext.select(fields).from(table);
        GroupField groupByField = DSL.field(DSL.quotedName(groupColumnName));
        Table<?> nested = subquery.groupBy(groupByField).asTable("nested");

        SelectConditionStep<Record> scs = dslContext.select(nested.fields()).from(nested).where(DSL.noCondition());

        for (Map.Entry<String, String> entry : whereMap.entrySet()) {
            Field<Object> field = DSL.field(DSL.name(entry.getKey()));
            scs.and(field.eq(entry.getValue()));
        }
        Result<Record> result = scs.fetch();
        log.info("Execute SQL : {} ", scs.getSQL(ParamType.INLINED));

        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        for (Record data : result) {
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
            for (Field<?> field : data.fields()) {
                dataMap.put(field.getName(), data.get(field));
            }
            resultList.add(dataMap);
        }
        return resultList;
    }

}
