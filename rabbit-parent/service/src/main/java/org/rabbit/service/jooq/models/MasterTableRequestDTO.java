package org.rabbit.service.jooq.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rabbit.common.base.BasePageRequest;
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
public class MasterTableRequestDTO extends BasePageRequest {

    String id;

    String name;

    String status;

    List<String> createdBys;

    List<MTFieldInfo> fields;

    List<Map<String, Object>> data;

    Map<String, Object> where;

    public TableRequestDTO toTableRequest() {
        TableRequestDTO request = new TableRequestDTO();
        request.setTable(getName());
        request.setFields(getFields());
        return request;
    }

}
