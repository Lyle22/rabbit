package org.rabbit.service.form.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rabbit.entity.form.CaseTable;
import org.rabbit.service.jooq.MTColumnInfo;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseTableResponseDTO {

    String id;

    String caseTypeId;

    String label;

    @JsonIgnore
    String tableName;

    String status;

    String createdBy;

    String modifiedBy;

    Instant createdDate;

    Instant modifiedDate;

    List<MTColumnInfo> fields;

    public CaseTableResponseDTO assignValue(CaseTable table) {
        setId(table.getId());
        setCaseTypeId(table.getCaseTypeId());
        setLabel(table.getLabel());
        setTableName(table.getTableName());
        setStatus(table.getStatus());
        setCreatedBy(table.getCreatedBy());
        setModifiedBy(table.getModifiedBy());
        setCreatedDate(table.getCreatedDate());
        setModifiedDate(table.getModifiedDate());
        return this;
    }

}
