package org.rabbit.workflow.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Workflow Application RequestDTO")
public class AppRequestDTO extends BasePageRequest {

    @Schema(description = "App Name")
    private String name;

    @Schema(description = "App Key")
    private String key;

}
