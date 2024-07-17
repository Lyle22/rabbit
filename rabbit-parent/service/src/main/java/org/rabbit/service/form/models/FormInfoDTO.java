package org.rabbit.service.form.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rabbit.entity.form.FormFieldMapping;
import org.rabbit.entity.form.FormInfo;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormInfoDTO {

    String id;

    String bizId;

    String label;

    String tableName;

    String tableNamePrefix;

    String status;

    String createdBy;

    String modifiedBy;

    Instant createdDate;

    Instant modifiedDate;

    List<FormFieldMapping> fieldMappings;

    public static FormInfoDTO transform(FormInfo formInfo) {
        return FormInfoDTO.builder()
                .id(formInfo.getId())
                .bizId(formInfo.getBizId())
                .label(formInfo.getLabel())
                .tableName(formInfo.getTableName())
                .tableNamePrefix(formInfo.getTableNamePrefix())
                .status(formInfo.getStatus())
                .createdBy(formInfo.getCreatedBy())
                .modifiedBy(formInfo.getModifiedBy())
                .createdDate(formInfo.getCreatedDate())
                .modifiedDate(formInfo.getModifiedDate())
                .build();
    }

}
