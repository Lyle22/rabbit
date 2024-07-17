package org.rabbit.service.form;

import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.MTFieldTypeMapping;
import org.rabbit.service.jooq.impl.MySQLHelper;

import java.util.List;

/**
 * @author nine rabbit
 **/
public interface AbstractCaseTableService {

    String getTableNamePrefix();

    List<MTFieldTypeMapping> findFieldMappings();

    List<MTFieldInfo> findFixedFields();

    void verifyFieldSyntax(List<MTFieldInfo> fields);

    default String transformColumnName(String label) {
        String columnName = label.replace(" ", "_");
        if (MySQLHelper.isReservedWord(columnName)) {
            columnName = columnName + "_";
        }
        return columnName;
    }

    default String transformLabel(String columnName) {
        if (MySQLHelper.isReservedWord(columnName)) {
            return columnName.substring(0, columnName.length() - 1);
        }
        return columnName;
    }
}
