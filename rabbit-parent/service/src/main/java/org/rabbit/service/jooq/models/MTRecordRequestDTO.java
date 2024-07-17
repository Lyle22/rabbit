package org.rabbit.service.jooq.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rabbit.common.base.BasePageRequest;

import java.util.List;
import java.util.Map;

/**
 * The type of master table record
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MTRecordRequestDTO extends BasePageRequest {

    String id;

    Boolean status;

    String q;

    List<Map<String, Object>> data;

    Map<String, Object> where;

}
