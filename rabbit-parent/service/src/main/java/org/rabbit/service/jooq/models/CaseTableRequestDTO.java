package org.rabbit.service.jooq.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rabbit.common.base.BasePageRequest;
import org.rabbit.entity.form.CaseTable;
import org.rabbit.service.jooq.MTFieldInfo;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseTableRequestDTO extends BasePageRequest {

    String id;

    String label;

    String caseTypeId;

    String tableName;

    String status;

    List<MTFieldInfo> fields;

    List<String> createdBys;

    List<Map<String, Object>> data;

    Map<String, Object> where;

    String q;

    public CaseTable to() {
        CaseTable table = new CaseTable();
        table.setId(getId());
        table.setCaseTypeId(getCaseTypeId());
        table.setLabel(getLabel());
        return table;
    }

    public TableRequestDTO toTableRequest() {
        TableRequestDTO request = new TableRequestDTO();
        request.setTable(getTableName());
        request.setFields(getFields());
        return request;
    }

}
