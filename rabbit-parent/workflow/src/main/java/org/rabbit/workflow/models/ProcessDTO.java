package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Process dto.
 */
@Data
@Schema(description = "Process Definition")
public class ProcessDTO {
    @Schema(description = "Process ID")
    private String id;

    @Schema(description = "Process Key")
    private String key;

    @Schema(description = "Process Name")
    private String name;

    @Schema(description = "Process Category")
    private String category;

    @Schema(description = "Process Resource Name")
    private String resourceName;

    @Schema(description = "Process Diagram Resource Name")
    private String diagramName;

    @Schema(description = "Process Version")
    private Integer version;
}
