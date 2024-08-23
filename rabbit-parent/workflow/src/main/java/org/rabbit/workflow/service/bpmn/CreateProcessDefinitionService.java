package org.rabbit.workflow.service.bpmn;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ValidatorSet;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.workflow.constants.FlowableConstants;
import org.rabbit.workflow.exception.WorkflowException;
import org.rabbit.workflow.models.CreateProcessDefinitionDTO;
import org.rabbit.workflow.models.CustomAppInfo;
import org.rabbit.workflow.models.ProcessDefinitionDTO;
import org.rabbit.workflow.models.SourceTargetDTO;
import org.rabbit.workflow.service.bpmn.validate.BizFlowableValidator;
import org.rabbit.workflow.service.facade.FunctionFacadeFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.rabbit.workflow.service.bpmn.CreateBpmnModelHandler.createStartEvent;

/**
 * 此类设计为创建流程定义和解析流程定义的服务
 * the class of create process definition service
 *
 * @author nine rabbit
 **/
@Slf4j
@Service
public class CreateProcessDefinitionService {

    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final FlowableAppService appService;
    private final RepositoryService repositoryService;
    private final IProcessFormService processFormService;
    private final CreateBpmnModelHandler createBpmnModelHandler;
    private final FunctionFacadeFactory functionFacadeFactory;

    public CreateProcessDefinitionService(
            ProcessEngine processEngine, RepositoryService repositoryService, RuntimeService runtimeService,
            FlowableAppService appService, IProcessFormService processFormService,
            CreateBpmnModelHandler createBpmnModelHandler, FunctionFacadeFactory functionFacadeFactory) {
        this.processEngine = processEngine;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.appService = appService;
        this.processFormService = processFormService;
        this.createBpmnModelHandler = createBpmnModelHandler;
        this.functionFacadeFactory = functionFacadeFactory;
    }

    public ProcessDefinitionDTO build(CreateProcessDefinitionDTO requestDTO) {
        // 1. 获取流程定义

        return null;
    }

    /**
     * 创建最简单的流程定义
     * @param requestDTO the requestDTO
     * @return ProcessDefinitionDTO
     */
    public ProcessDefinitionDTO createBlankProcessDefinition(CreateProcessDefinitionDTO requestDTO) {
        if (StringUtils.isBlank(requestDTO.getName())) {
            requestDTO.setName("Simple Process " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH-mm-ss"));
        }
        if (StringUtils.isBlank(requestDTO.getKey())) {
            requestDTO.setKey(CommonUtils.removeSpecialChar(requestDTO.getName()));
        }
        // 1. 创建BpmnModel
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        StartEvent startEvent = createStartEvent("$INITATOR", null, false, true, null, null);
        process.addFlowElement(startEvent);

        ServiceTask serviceTask = CreateBpmnModelHandler.createServiceTask(
                ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION, "uploadFileDelegate"
        );
        List<FieldExtension> list = Lists.newArrayList();
        CreateBpmnModelHandler.addFieldExtension(list, "parentPath", "${variables.get('parentPath')}");
        CreateBpmnModelHandler.addFieldExtension(list, "documentName", "${variables.get('documentName')}");
        CreateBpmnModelHandler.addFieldExtension(list, "documentType", "${variables.get('documentType')}");
        CreateBpmnModelHandler.addFieldExtension(list, "properties", "${variables.get('properties')}");
        CreateBpmnModelHandler.addFieldExtension(list, "contentId", "${variables.get('contentId')}");
        serviceTask.setFieldExtensions(list);
        process.addFlowElement(serviceTask);

        EndEvent endEvent = CreateBpmnModelHandler.createEndEvent();
        process.addFlowElement(endEvent);

        List<SourceTargetDTO> sequenceFlows = Lists.newArrayList();
        sequenceFlows.add(SourceTargetDTO.add(startEvent.getId(), serviceTask.getId()));
        sequenceFlows.add(SourceTargetDTO.add(serviceTask.getId(), endEvent.getId()));
        CreateBpmnModelHandler.setSequenceFlows(process, sequenceFlows);

        process.setId(requestDTO.getKey());
        process.setName(requestDTO.getName());
        bpmnModel.addProcess(process);

        // 2. 创建流程定义
        List<Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            bpmnModel.setTargetNamespace(FlowableConstants.DEFAULT_NAME_SPACE);
            String processName = (process.getName() + FlowableConstants.PD_BPMN_FILENAME_SUFFIX).replace(" ", "");
            // Set up auto layout for auto generate diagram if xml doesn't exist flowable:diagram
            new BpmnAutoLayout(bpmnModel).execute();

            ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
            ProcessValidator validator = processValidatorFactory.createDefaultProcessValidator();
            ValidatorSet validatorSet = new ValidatorSet("Validate UserTask assignee");
            validatorSet.addValidator(new BizFlowableValidator(functionFacadeFactory));
            validator.getValidatorSets().add(validatorSet);
            // 获得验证的失败信息
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (errors.isEmpty()) {
                return deploy(bpmnModel, processName);
            } else {
                StringBuilder builder = new StringBuilder();
                for (ValidationError validation : errors) {
                    log.error(validation.getDefaultDescription());
                    builder.append(validation.getActivityName()).append(" ").append(validation.getDefaultDescription()).append(" \n");
                }
                throw new WorkflowException(builder.toString());
            }
        } else {
            log.error("Can not deploy process definition to application,  because file doesn't exists");
            return null;
        }
    }

    public ProcessDefinitionDTO deploy(BpmnModel bpmnModel, String processName) {
        // Deploy to default application
        CustomAppInfo customApp = appService.buildCustomApp();
        Deployment deployment = repositoryService.createDeployment()
                .name(customApp.getName())
                .key(customApp.getKey())
                .category(customApp.getCategory())
                .addBpmnModel(processName, bpmnModel)
                .deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessDefinitionDTO processDefinitionDTO = new ProcessDefinitionDTO(processDefinition);
        // Parse permission
        BpmnModel model = getBpmnModel(processDefinition.getKey(), processDefinition.getId());
        List<Map<String, String>> permissions = ProcessDefinitionParseHandler.extractPermissions(model);
        processDefinitionDTO.setPermissions(permissions);
        return processDefinitionDTO;
    }

    public BpmnModel getBpmnModel(String processKey, String processDefinitionId) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = query(processKey, processDefinitionId);
        return processDefinition == null ? null : repositoryService.getBpmnModel(processDefinition.getId());
    }

    public ProcessDefinition query(String processDefinitionKey, String processDefinitionId) {
        if (StringUtils.isAllBlank(processDefinitionKey, processDefinitionId)) {
            throw new WorkflowException("Process definition key or id must be not null");
        }
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            processDefinitionQuery = processDefinitionQuery.processDefinitionKey(processDefinitionKey).latestVersion();
        }
        if (StringUtils.isNotBlank(processDefinitionId)) {
            processDefinitionQuery = processDefinitionQuery.processDefinitionId(processDefinitionId);
        }
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
        if (processDefinitions.isEmpty()) {
            return null;
        }
        return processDefinitions.get(0);
    }

}
