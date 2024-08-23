package org.rabbit.workflow.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.List;

/**
 * The type Process Instance dto.
 * @author nine rabbit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Process Instance Information")
public class ProcessInstanceDTO {

    @Schema(description = "process Instance ID")
    private String processInstanceId;

    @Schema(description = "Business Key")
    private String businessKey;

    @Schema(description = "Task ID")
    private String taskId;

    @Schema(description = "is complete state")
    private Boolean complete;

    @Schema(description = "process Instance state")
    private String state;

    @Schema(description = "Assigned user of current task")
    private String assignedUser;

    @Schema(description = "Error Message")
    private String errorMsg;

    InstanceDTO instance;

    List<TaskDTO> tasks;

    HistoricProcessInstance historicInstance;

}
