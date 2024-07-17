package org.rabbit.service.jooq.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.common.utils.DateUtil;
import org.rabbit.service.jooq.MTColumnInfo;
import org.rabbit.service.jooq.MTDataValidateDTO;
import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.MTFieldTypeMapping;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The class of abstract Jooq service
 * <pre>
 * 1. 只负责添加一些约定的配置信息和必须的业务行为
 * 2. 添加一些共用的业务算法和业务方法
 * </pre>
 */
public interface AbstractJooqService {

    public static final String TABLE_NAME_PREFIX = "mt_";

    public static final String ID = "id";
    public static final String MODIFIED_DATE = "modified_date";
    public static final String CREATED_DATE = "created_date";
    public static final String MODIFIED_BY = "modified_by";
    public static final String CREATED_BY = "created_by";
    public static final String STATUS = "status";
    public static final String BUSINESS_NO = "biz_no";
    public static final String BUSINESS_TYPE = "biz_type";
    public static final String COLUMN_NAME = "column_name";
    public static final String COLUMN_VALUE = "column_value";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String PERCENT_SIGN = "%";
    public static final String FIELD_TYPE_RELATION = "relation";
    public static final List<String> FIXED_FIELDS = List.of(ID, MODIFIED_DATE, CREATED_DATE, CREATED_BY, MODIFIED_BY, STATUS);

    // 唯一索引的列的数据类型通常是数值、文本或日期, CREATE UNIQUE INDEX index_name ON table_name(column_name);
    public static final List<MTFieldTypeMapping> FIELD_TYPE_MAPPINGS = Arrays.asList(
        new MTFieldTypeMapping("bigint", "Number", true, "number"),
        new MTFieldTypeMapping("varchar:255", "ShortText", true, "short_text"),
        new MTFieldTypeMapping("varchar:4000", "String", false, "string"),
        new MTFieldTypeMapping("clob", "LongText", false, "longText"),
        new MTFieldTypeMapping("timestamp", "Date", false, "date"),
        new MTFieldTypeMapping("boolean", "Boolean", false, "boolean"),
        new MTFieldTypeMapping("decimal", "Float", false, "float"),
        new MTFieldTypeMapping("json", "Json", false, "json")
    );

    default Table<Record> table(String tableName) { return DSL.table(DSL.name(tableName));}

    default Field<Object> field(String fieldName) {
        return DSL.field(DSL.name(fieldName));
    }

    default Field<Object> quotedField(String fieldName) {
        return DSL.field(DSL.quotedName(fieldName));
    }

    public default String convertDataType(String columnType) {
        if (columnType.equals("datetime")) {
            return "timestamp";
        }

        if (columnType.equals("tinyint")) {
            return "bit";
        }
        return columnType;
    }

    public default Field<String> dateDefaultFormat(Field<Date> field) {
        return dateFormat(field, "%Y-%m-%d %H:%i:%s");
    }

    public static List<MTFieldTypeMapping> getFieldTypeMapping() {
        List<MTFieldTypeMapping> mappings = FIELD_TYPE_MAPPINGS;
        return mappings.stream().sorted(Comparator.comparing(MTFieldTypeMapping::getLabel).reversed()).collect(Collectors.toList());
    }

    public default Field<String> dateFormat(Field<Date> field, String format) {
        return DSL.field("date_format({0}, {1})", SQLDataType.VARCHAR, field, DSL.inline(format));
    }

    public abstract List<MTColumnInfo> getTableStructure(String tableName);

    public abstract List<LinkedHashMap<String, Object>> queryById(String tableName, String id);

    public abstract List<LinkedHashMap<String, Object>> getRowData(String name, String columnName, Object columnValue);

    public abstract Map<String, Object> checkUnique(String tableName, String fieldName, String fieldValue);

    public default MTDataValidateDTO validationData(
            String tableName, Map<String, MTColumnInfo> columnInfoMap, String fieldName, String fieldValue, List<String> uniqueFields
    ) {
        MTDataValidateDTO validateDTO = MTDataValidateDTO.builder().validate(true).build();
        // 1.1 filter data if non-table column
        if (!columnInfoMap.keySet().contains(fieldName)) {
            validateDTO.setErrorMessage(String.format("Does not exist field [%s]", fieldName));
            return validateDTO;
        }
        // 1.2 Check data for uniqueness?
        if (CollectionUtils.isNotEmpty(uniqueFields) && uniqueFields.contains(fieldName) && !checkUnique(tableName, fieldName, fieldValue).isEmpty()) {
            String errorMessage = String.format("Field[%s] must be unique value, You need to update this value.", fieldName);
            validateDTO.setErrorMessage(errorMessage);
        }
        // 1.3 类型校验
        MTColumnInfo columnInfo = columnInfoMap.get(fieldName);
        if (null != columnInfo) {
            String dataType = columnInfo.getDataType();
            switch (dataType) {
                case "bigint":
                    if (CommonUtils.isNumeric(fieldValue)) {
                        if (fieldValue.length() >= 20 || new BigDecimal(fieldValue).compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                            String errorMessage = String.format("Field [%s] value range is -9223372036854775808 to 9223372036854775807.", fieldName);
                            validateDTO.setErrorMessage(errorMessage);
                        }
                    } else {
                        validateDTO.setErrorMessage(String.format("Field [%s] must be numeric value.", fieldName));
                    }
                    break;
                case "boolean":
                case "bit":
                    if (!(fieldValue.equalsIgnoreCase("true") || fieldValue.equalsIgnoreCase("false"))) {
                        validateDTO.setErrorMessage(String.format("Field [%s] must be boolean value.", fieldName));
                    }
                    break;
                case "timestamp":
                    Date date = DateUtil.formatDate(fieldValue);
                    if (date == null) {
                        validateDTO.setErrorMessage(String.format("Field [%s] must be date type. value::[%s]", fieldName, fieldValue));
                    }
                    break;
                case "decimal":
                    if (!CommonUtils.isNumeric(fieldValue)) {
                        validateDTO.setErrorMessage(String.format("Field [%s] must be numeric value.", fieldName));
                    }
                    break;
                case FIELD_TYPE_RELATION:
                    if (columnInfo.isNullRelation()) {
                        validateDTO.setErrorMessage(String.format("Relation Field [%s] was expired.", fieldName));
                        break;
                    }
                    // check data whether exists?
                    List<LinkedHashMap<String, Object>> rowData = getRowData(columnInfo.getRelationTable(), columnInfo.getRelationField(), fieldValue);
                    if (null == rowData || !rowData.isEmpty()) {
                        // This data does not exist in the related table | 关联表中不存在此条数据
                        validateDTO.setErrorMessage(String.format("Value doesn't exist in the related table [%s], Field = [%s].", columnInfo.getRelationTable(), fieldName));
                    }
                    break;
                default:
                    break;
            }
            if (StringUtils.isNotBlank(columnInfo.getRelationTable())) {
                if (columnInfo.isNullRelation()) {
                    validateDTO.setErrorMessage(String.format("Relation Field [%s] was expired.", fieldName));
                } else {
                    // check data whether exists?
                    List<LinkedHashMap<String, Object>> rowData = getRowData(columnInfo.getRelationTable(), columnInfo.getRelationField(), fieldValue);
                    if (null == rowData || rowData.isEmpty()) {
                        // This data does not exist in the related table | 关联表中不存在此条数据
                        validateDTO.setErrorMessage(String.format("Value doesn't exist in the related table [%s], Field = [%s].",
                                columnInfo.getRelationTable(), fieldName));
                    }
                }
            }
        } else {
            validateDTO.setErrorMessage(String.format("This master table doesn't exists this field [%s] ", fieldName));
        }
        if (fieldName.equals(ID) && CollectionUtils.isNotEmpty(queryById(tableName, fieldValue))) {
            validateDTO.setErrorMessage("This record have bean exists.");
        }
        return validateDTO;
    }

    public default String validateRequired(Map<String, Object> dataMap, List<String> requiredFields) {
        for (String requiredField : requiredFields) {
            String errorMessage = String.format("Field[%s] must have a value.", requiredField);
            if (dataMap.get(requiredField) == null || Objects.isNull(dataMap.get(requiredField))) {
                return errorMessage;
            }
            String fieldValue = String.valueOf(dataMap.get(requiredField));
            if (StringUtils.isBlank(fieldValue)) {
                return errorMessage;
            }
            if (fieldValue.equals("null")) {
                return errorMessage;
            }
        }
        return null;
    }

    public static List<MTFieldInfo> getFixedFields() {
        List<MTFieldInfo> fieldInfo = Lists.newArrayList();
        fieldInfo.add(MTFieldInfo.builder().fieldName(ID).dataType("varchar:64").required(true).unique(true).primaryKey(true).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(CREATED_BY).dataType("varchar:64").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(CREATED_DATE).dataType("timestamp").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(MODIFIED_BY).dataType("varchar:64").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(MODIFIED_DATE).dataType("timestamp").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(STATUS).dataType("boolean").required(false).unique(false).primaryKey(false).build());
        return fieldInfo;
    }

    public static List<MTFieldInfo> getValueTableFixedFields() {
        List<MTFieldInfo> fieldInfo = Lists.newArrayList();
        fieldInfo.add(MTFieldInfo.builder().fieldName(ID).dataType("varchar:64").required(true).unique(true).primaryKey(true).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(BUSINESS_NO).dataType("varchar:256").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(BUSINESS_TYPE).dataType("varchar:64").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(COLUMN_NAME).dataType("varchar:256").required(false).unique(false).primaryKey(false).build());
        fieldInfo.add(MTFieldInfo.builder().fieldName(COLUMN_VALUE).dataType("varchar:4000").required(false).unique(false).primaryKey(false).build());
        return fieldInfo;
    }

    public default String transformColumnName(String label) {
        String columnName = label.replace(" ", "_");
        if (MySQLHelper.isReservedWord(columnName)) {
            columnName = columnName + "_";
        }
        return columnName;
    }

    public default String transformLabel(String columnName) {
        if (MySQLHelper.isReservedWord(columnName)) {
            return columnName.substring(0, columnName.length() - 1);
        }
        return columnName;
    }

    public default boolean validationUnique(List<MTFieldInfo> fields) {
        fields.forEach(field -> {
            if (Boolean.TRUE.equals(field.getUnique())) {
                if (Boolean.FALSE.equals(field.getRequired())) {
                    throw new IllegalArgumentException(field.getFieldName() + " can not be null value for unique field");
                }
                String dataType = field.getDataType();
                // If type is string that should be checked length
                if (dataType.contains("varchar")) {
                    String[] dataTypeArr = dataType.split(":");
                    if (Integer.parseInt(dataTypeArr[1]) > 1024) {
                        throw new IllegalArgumentException(field.getFieldName() + " was too long as a unique field, max length is 1024");
                    }
                }
            }
        });
        return true;
    }
}
