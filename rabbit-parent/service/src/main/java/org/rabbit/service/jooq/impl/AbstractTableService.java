package org.rabbit.service.jooq.impl;

import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.MTFieldTypeMapping;

import java.util.List;

/**
 * The class of abstract table service
 *
 * <p>类的职责</p>
 * <pre>
 * 1. 负责制定一些约定的配置信息
 * 2. 添加一些共用的业务逻辑和业务方法
 * </pre>
 */
public abstract class AbstractTableService {

    protected abstract String getTableNamePrefix();

    protected abstract List<MTFieldTypeMapping> findFieldMappings();

    protected abstract List<MTFieldInfo> findFixedFields();

    protected abstract void verifyFieldSyntax(List<MTFieldInfo> fields);

    protected String transformColumnName(String label) {
        String columnName = label.replace(" ", "_");
        if (MySQLHelper.isReservedWord(columnName)) {
            columnName = columnName + "_";
        }
        return columnName;
    }

    protected String transformLabel(String columnName) {
        if (MySQLHelper.isReservedWord(columnName)) {
            return columnName.substring(0, columnName.length() - 1);
        }
        return columnName;
    }
}
