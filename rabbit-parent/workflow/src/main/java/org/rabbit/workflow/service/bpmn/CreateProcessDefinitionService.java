package org.rabbit.workflow.service.bpmn;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.rabbit.workflow.models.CreateProcessDefinitionDTO;
import org.rabbit.workflow.models.ProcessDefinitionDTO;
import org.springframework.stereotype.Service;

/**
 * 此类设计为创建流程定义和解析流程定义的服务
 * the class of create process definition service
 *
 * @author Lyle
 **/
@Slf4j
@Service
public class CreateProcessDefinitionService {

    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final FlowableAppService appService;
    private final IProcessFormService processFormService;

    public CreateProcessDefinitionService(
            ProcessEngine processEngine, RepositoryService repositoryService, RuntimeService runtimeService,
            FlowableAppService appService, IProcessFormService processFormService
    ) {
        this.processEngine = processEngine;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.appService = appService;
        this.processFormService = processFormService;
    }

    public ProcessDefinitionDTO build(CreateProcessDefinitionDTO requestDTO) {


        return null;
    }

}
