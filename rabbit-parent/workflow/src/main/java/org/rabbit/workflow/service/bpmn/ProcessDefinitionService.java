package org.rabbit.workflow.service.bpmn;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.workflow.constants.FlowableConstants;
import org.rabbit.workflow.models.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author nine rabbit
 **/
@Service
@Slf4j
public class ProcessDefinitionService implements IProcessDefinitionService {

    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final FlowableAppService appService;

    public ProcessDefinitionService(ProcessEngine processEngine, RepositoryService repositoryService, RuntimeService runtimeService, FlowableAppService appService) {
        this.processEngine = processEngine;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.appService = appService;
    }

    @Override
    public boolean active(String processDefinitionKey) {
        runtimeService.activateProcessInstanceById(processDefinitionKey);
        repositoryService.activateProcessDefinitionByKey(processDefinitionKey);
        return true;
    }

    @Override
    public List<FormPropertyDTO> getByParseBpmnXml(String processDefinitionId, String processDefinitionKey, String formKey) {

        return null;
    }

    @Override
    public List<BpmnDynamicFormDTO> findForms(String processDefinitionId, String processDefinitionKey) {
        return null;
    }

    @Override
    public boolean isExists(String processDefinitionKey) {
        return false;
    }

    @Override
    public ProcessDefinition create(MultipartFile multipartFile) throws IOException {
        BpmnModel bpmnModel = CreateBpmnModelHandler.createProcessDefinition("processDefinitionKey");
        List<Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            Process process = processes.get(0);
            bpmnModel.setTargetNamespace(FlowableConstants.DEFAULT_NAME_SPACE);
            String processName = (process.getName() + FlowableConstants.PD_BPMN_FILENAME_SUFFIX).replace(" ", "");
            // Set up auto layout for auto generate diagram if xml doesn't exist flowable:diagram
            new BpmnAutoLayout(bpmnModel).execute();

            ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
            ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
            // 验证失败信息的封装 ValidationError
            List<ValidationError> validate = defaultProcessValidator.validate(bpmnModel);

            // Save process definition
            RepositoryService repositoryService = processEngine.getRepositoryService();

            // Deploy to default application
            CustomAppInfo customApp = appService.buildCustomApp();

            Deployment deployment = repositoryService.createDeployment()
                    .name(customApp.getName())
                    .key(customApp.getKey())
                    .category(customApp.getCategory())
                    .addBpmnModel(processName, bpmnModel)
                    .deploy();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId()).singleResult();
            log.info("Deploy process definition result:: [{}] ", JsonHelper.write(deployment));
            return processDefinition;
        } else {
            log.error("Can not deploy process definition to application,  because file doesn't exists");
            return null;
        }

    }

    @Override
    public boolean preParseBpmnXml(MultipartFile multipartFile) throws IOException {
        return false;
    }

    @Override
    public ProcessDefinitionDTO findByKey(String processKey) {
        return null;
    }

    @Override
    public List<ProcessDefinitionDTO> findByKey(Set<String> processKeys) {
        return null;
    }

    @Override
    public BpmnModel getBpmnModel(String processKey, String processDefinitionId) {
        return null;
    }

    @Override
    public ProcessDefinition query(String processDefinitionKey, String processDefinitionId) {
        return null;
    }

    @Override
    public FileDTO downloadBpmnXml(String processKey, String processDefinitionId) throws IOException {
        return null;
    }

    @Override
    public FileDTO downloadDiagram(String processKey, String processDefinitionId) throws IOException {
        return null;
    }

    @Override
    public PaginationDTO<ProcessDefinitionDTO> page(ProcessDefinitionRequestDTO requestDTO) {
        return null;
    }

    @Override
    public boolean delete(String processDefinitionId) {

        return false;
    }

    @Override
    public boolean suspend(String processDefinitionKey) {
        return false;
    }
}
