package org.rabbit.workflow.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.common.contains.Result;
import org.rabbit.workflow.models.ProcessInstanceDTO;
import org.rabbit.workflow.models.WorkflowRequestDTO;
import org.rabbit.workflow.service.ResponseUtils;
import org.rabbit.workflow.service.bpmn.FlowableAppService;
import org.rabbit.workflow.service.bpmn.IProcessDefinitionService;
import org.rabbit.workflow.service.bpmn.IProcessInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Process Instance APIs
 *
 * @author nine rabbit
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("${API_VERSION}/process/instances")
@Tag(name = "Process Instance", description = "Process Instance Controller")
public class ProcessInstanceController {

    private final ResponseUtils responseUtils;
    private final FlowableAppService flowableAppService;
    private final IProcessDefinitionService processDefinitionService;
    private final IProcessInstanceService processInstanceService;

    @PostMapping({"/start", "/process/start/"})
    public Result<ProcessInstanceDTO> startProcess(@RequestBody WorkflowRequestDTO workflow) {
        return Result.ok(processInstanceService.start(workflow.getProcessKey(), workflow.getBusinessKey(), workflow.getVariables()));
    }
}
