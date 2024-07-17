package org.rabbit.service.form;

import org.jetbrains.annotations.NotNull;
import org.rabbit.entity.form.FormFieldMapping;
import org.rabbit.entity.form.FormInfo;

import java.util.List;
import java.util.Map;

/**
 * Field mapping relationship of case table
 * <p/> 类的职责: 只定义CaseTable功能的共用的行为方法
 */
public interface IFieldMappingService {

    boolean delete(FormInfo formInfo);

    /**
     * Create a case table with columns
     *
     * @param formInfo      the form information
     * @param fieldMappings the field mappings
     * @return CaseTable
     */
    boolean create(FormInfo formInfo, List<FormFieldMapping> fieldMappings);

    List<FormFieldMapping> findAllByFormInfoId(@NotNull String formInfoId);

    List<FormFieldMapping> findActive(@NotNull String formInfoId);

    /**
     * Change field mapping of form information
     *
     * @param formInfo      the form information
     * @param fieldMappings the field mappings
     * @return boolean
     */
    boolean changeFields(FormInfo formInfo, List<FormFieldMapping> fieldMappings);

    /**
     * insert row data into a case table
     *
     * @param tableLabel the label of database table
     * @param dataMap    the row data
     * @return boolean
     */
    boolean insertFormData(String tableLabel, Map<String, Object> dataMap);

}
