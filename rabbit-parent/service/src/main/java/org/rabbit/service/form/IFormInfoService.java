package org.rabbit.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.entity.form.FormFieldMapping;
import org.rabbit.entity.form.FormInfo;
import org.rabbit.service.form.models.FormInfoDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nine rabbit
 **/
public interface IFormInfoService extends IService<FormInfo> {
    String DEFAULT_TABLE_NAME_PREFIX = "cust_";
    String BUSINESS_NO = "biz_no";
    String BUSINESS_TYPE = "biz_type";
    String COLUMN_NAME = "column_name";
    String COLUMN_VALUE = "column_value";


    /**
     * Create a new database table
     *
     * @param tableLabel      the table label, equivalent to table alias
     * @param tableNamePrefix the prefix of database table name
     * @param fieldMapping    the field mapping list
     * @return form Information
     */
    FormInfo create(String tableLabel, String tableNamePrefix, String bizId, List<FormFieldMapping> fieldMapping);

    FormInfo findByLabel(String tableLabel, String tableNamePrefix);

    /**
     * Retrieve name of the database table
     *
     * @param tableLabel      the table label, equivalent to table alias
     * @param tableNamePrefix the prefix of database table name
     * @return String name of the database table
     */
    String getTableName(String tableLabel, String tableNamePrefix);

    /**
     * Retrieve form through business id
     *
     * @param bizId the business id
     * @return form information
     */
    FormInfo getByBizId(String bizId);

    /**
     * Retrieve form for release version
     *
     * @param bizId the business id
     * @return form information if exist
     */
    FormInfoDTO getReleaseByBizId(String bizId);

    /**
     * Retrieve a case table structure
     * <p>Execute SQL: show columns from form_values </p>
     *
     * @param tableLabel      the table label, equivalent to table alias
     * @param tableNamePrefix the prefix of database table name
     * @return List<MTColumnInfo> the list of column information
     */
    List<FormFieldMapping> getTableStructure(String tableLabel, String tableNamePrefix);

    /**
     * Update form information and field mappings
     *
     * @param formInfo     the form info
     * @param fieldMapping the field mapping
     * @return
     */
    FormInfo update(FormInfo formInfo, List<FormFieldMapping> fieldMapping);

    boolean deleteByBizId(String bizId);

    /**
     * Insert data into the database table
     * <p>Execute SQL: show columns from form_values </p>
     *
     * @param formInfo the specify table
     * @param bizNo    the business number
     * @param bizType  the business type
     * @return List<MTColumnInfo> the list of column information
     */
    boolean insertRecord(FormInfoDTO formInfo, String bizNo, String bizType, Map<String, Object> dataMap);

    boolean updateRecord(FormInfo formInfo, String recordId, Map<String, Object> dataMap);

    PaginationDTO<List<LinkedHashMap<String, Object>>> pageQueryRecord(FormInfo formInfo, int pageNum, int pageSize, Map<String, Object> whereMap);

    List<LinkedHashMap<String, Object>> findRecords(String tableLabel, Map<String, Object> where);
}
