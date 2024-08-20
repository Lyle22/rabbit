package org.rabbit.workflow.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.rabbit.common.base.BasePageRequest;


/**
 * The class of Workflow Process Definition RequestDTO
 *
 * @author nine rabbit
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Workflow Process Definition RequestDTO")
public class ProcessDefinitionRequestDTO extends BasePageRequest {

    @Schema(description = "Process Definition Name")
    private String name;

    @Schema(description = "Process Definition Key")
    private String key;

}
