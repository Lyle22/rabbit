package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * The type Instance dto.
 */
@Data
@Schema(description = "Process Instance")
public class InstanceDTO {
    @Schema(description = "Execution Id")
    private String id;

    @Schema(description = "Activity Id")
    private String activityId;

    @Schema(description = "Business Key")
    private String businessKey;

    @Schema(description = "Calllback Id")
    private String callbackId;

    @Schema(description = "Callback Type")
    private String callbackType;

    @Schema(description = "Deployment Id")
    private String deploymentId;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Localized Description")
    private String localizedDescription;

    @Schema(description = "Localized Name")
    private String localizedName;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Parent Id")
    private String parentId;

    @Schema(description = "Process Definition Id")
    private String processDefinitionId;

    @Schema(description = "Process Definition Key")
    private String processDefinitionKey;

    @Schema(description = "Process Definition Name")
    private String processDefinitionName;

    @Schema(description = "Process Definition Version")
    private Integer processDefinitionVersion;

    @Schema(description = "Process Instance Id")
    private String processInstanceId;

    @Schema(description = "Process Variables")
    private Map<String, Object> processVariables;

    @Schema(description = "Propagated Stage Instance Id")
    private String propagatedStageInstanceId;

    @Schema(description = "Reference Id")
    private String referenceId;

    @Schema(description = "Reference Type")
    private String referenceType;

    @Schema(description = "Root Process Instance Id")
    private String rootProcessInstanceId;

    @Schema(description = "Super Execution Id")
    private String superExecutionId;

    @Schema(description = "Start Time")
    private Instant startTime;

    @Schema(description = "Start User Id")
    private String startUserId;

    @Schema(description = "Tenant Id")
    private String tenantId;

    @Schema(description = "Is Ended")
    private Boolean isEnded;

    @Schema(description = "Is Suspended")
    private Boolean isSuspended;
}
