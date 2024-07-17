package org.rabbit.service.jooq.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rabbit.common.base.BasePageRequest;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MTAuditLogRequestDTO extends BasePageRequest {

    String id;

    String name;

    private String eventId;

    private List<String> categories;

    private List<String> creators;

}
