package org.rabbit.workflow.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Task dto.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task")
public class TaskDTO {
    @Schema(description = "Task ID")
    private String id;

    @Schema(description = "Task Name")
    private String name;

    @Schema(description = "Task Description")
    private String description;

    @Schema(description = "Task Definition ID")
    private String taskDefinitionId;

    @Schema(description = "Task Definition Key")
    private String taskDefinitionKey;

    @Schema(description = "Task Assignee")
    private String assignee;

    @Schema(description = "Task Form Key")
    private String formKey;

    @Schema(description = "Task Instance ID")
    private String instanceId;

    @Schema(description = "Task Parent ID")
    private String parentId;

    @Schema(description = "Task Creation Date")
    private Instant createDate;

    @Schema(description = "Task Due Date")
    private Instant dueDate;

    @Schema(description = "Task Claim Date")
    private Instant claimDate;

    @Schema(description = "Task Instance")
    private InstanceDTO taskInstance;

    private Map<String, Object> variables = new HashMap<String, Object>();
}
