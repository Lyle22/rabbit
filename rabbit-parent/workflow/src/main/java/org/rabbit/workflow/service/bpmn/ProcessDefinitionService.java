package org.rabbit.workflow.service.bpmn;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.Task;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ValidatorSet;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.workflow.constants.FlowableConstants;
import org.rabbit.workflow.exception.WorkflowException;
import org.rabbit.workflow.models.*;
import org.rabbit.workflow.service.ResponseUtils;
import org.rabbit.workflow.service.bpmn.validate.BizFlowableValidator;
import org.rabbit.workflow.service.facade.FunctionFacadeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 此类是定义流程定义的服务方法和业务行为
 *
 * @author nine rabbit
 **/
@Service
@Slf4j
public class ProcessDefinitionService implements IProcessDefinitionService {

    private final ResponseUtils responseUtils;
    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final FlowableAppService appService;
    private final IProcessFormService processFormService;
    private final FunctionFacadeFactory functionFacadeFactory;


    public ProcessDefinitionService(ResponseUtils responseUtils, ProcessEngine processEngine, RepositoryService repositoryService, RuntimeService runtimeService, FlowableAppService appService, IProcessFormService processFormService, FunctionFacadeFactory functionFacadeFactory) {
        this.responseUtils = responseUtils;
        this.processEngine = processEngine;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.appService = appService;
        this.processFormService = processFormService;
        this.functionFacadeFactory = functionFacadeFactory;
    }

    @Override
    public boolean active(String processDefinitionKey) {
        runtimeService.activateProcessInstanceById(processDefinitionKey);
        repositoryService.activateProcessDefinitionByKey(processDefinitionKey);
        return true;
    }

    @Override
    public List<FormPropertyDTO> getByParseBpmnXml(String processDefinitionId, String processDefinitionKey, String elementId) {
        BpmnModel model = getBpmnModel(processDefinitionKey, processDefinitionId);
        return processFormService.getByParseBpmnXml(model, elementId);
    }

    @Override
    public List<BpmnDynamicFormDTO> findForms(String processDefinitionId, String processDefinitionKey) {
        return null;
    }

    @Override
    public boolean isExists(String processDefinitionKey) {
        return findByKey(processDefinitionKey) != null;
    }

    @Override
    public ProcessDefinitionDTO create(MultipartFile multipartFile) {
        BpmnModel bpmnModel = CreateBpmnModelHandler.createProcessDefinition("testApproval");
        List<Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            Process process = processes.get(0);
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
        RepositoryService repositoryService = processEngine.getRepositoryService();
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

    @Override
    public boolean preParseBpmnXml(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        assert fileName != null;
        if (!fileName.endsWith(FlowableConstants.PD_BPMN_FILE_SUFFIX)) {
            throw new WorkflowException("Incorrect file format, it should be bpmn.xml file");
        }
        File tmpFile = CommonUtils.toFile(multipartFile);
        // Parse bpmn.xml for check grammar is right ？
        List<Process> processes = preParseBpmnXml(tmpFile);
        return CollectionUtils.isNotEmpty(processes);
    }

    public List<Process> preParseBpmnXml(File file) {
        XMLStreamReader reader = null;
        Path path = Paths.get(file.getPath());
        try (InputStream inputStream = Files.newInputStream(path)) {
            BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            reader = factory.createXMLStreamReader(inputStream);
            BpmnModel model = bpmnXMLConverter.convertToBpmnModel(reader);
            List<Process> processes = model.getProcesses();
            if (CollectionUtils.isEmpty(processes)) {
                log.error("BPMN模型没有配置流程");
                return Collections.emptyList();
            }
            return processes;
        } catch (Exception e) {
            log.error("BPMN模型创建流程异常", e);
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                log.error("关闭异常", e);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public ProcessDefinitionDTO findByKey(String processKey) {
        Assert.notNull(processKey, "Process Definition Key must be not null");
        ProcessDefinition definition = this.query(processKey, null);
        return parse(definition);
    }

    @Override
    public List<ProcessDefinitionDTO> findByKey(Set<String> processKeys) {
        if (processKeys == null || processKeys.isEmpty()) {
            return Lists.newArrayList();
        }
        return processKeys.stream().map(this::findByKey).collect(Collectors.toList());
    }

    @Override
    public BpmnModel getBpmnModel(String processKey, String processDefinitionId) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = query(processKey, processDefinitionId);
        return processDefinition == null ? null : repositoryService.getBpmnModel(processDefinition.getId());
    }

    @Override
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

    @Override
    public FileDTO downloadBpmnXml(String processKey, String processDefinitionId) throws IOException {
        ProcessDefinition processDefinition = query(processKey, processDefinitionId);
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String fileName = processDefinition.getResourceName();
        InputStream is = repositoryService.getProcessModel(processDefinition.getId());
        return responseUtils.file(fileName, is);
    }

    @Override
    public FileDTO downloadDiagram(String processKey, String processDefinitionId) throws IOException {
        ProcessDefinition processDefinition = query(processKey, processDefinitionId);
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String fileName = processDefinition.getDiagramResourceName();
        InputStream is = repositoryService.getProcessDiagram(processDefinition.getId());
        return responseUtils.file(fileName, is);
    }

    @Override
    public PaginationDTO<ProcessDefinitionDTO> page(ProcessDefinitionRequestDTO requestDTO) {
        Assert.notNull(requestDTO.getPageNum(), "PageNum must be not null");
        Assert.notNull(requestDTO.getPageSize(), "PageSize must be not null");

        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .processDefinitionCategory(FlowableConstants.DEFAULT_NAME_SPACE)
                .latestVersion()
                .orderByProcessDefinitionKey().desc();
        if (StringUtils.isNotBlank(requestDTO.getName())) {
            query.processDefinitionNameLikeIgnoreCase("%" + requestDTO.getName() + "%");
        }
        int currPage = requestDTO.getPageNum() * requestDTO.getPageSize();
        long total = query.count();
        List<ProcessDefinition> list = query.listPage(currPage, requestDTO.getPageSize());

        List<ProcessDefinitionDTO> definitionDTOS = ProcessDefinitionDTO.toDTO(list);
        PaginationDTO<ProcessDefinitionDTO> page = new PaginationDTO<>();
        page.setEntryList(definitionDTOS);
        page.setPageCount((int) total);
        page.setTotalSize((int) total);
        return page;
    }

    @Override
    public boolean delete(String processDefinitionId) {
        ProcessDefinition processDefinition = query(null, processDefinitionId);
        // 检查是否存在正在运行的流程实例？
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processDefinitionKey(processDefinition.getKey()).list();
        if (CollectionUtils.isEmpty(list)) {
            // 不进行级联删除
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), false);
            return true;
        } else {
            // delete process instance
            List<String> processInstanceIds = list.stream().map(Task::getProcessInstanceId).collect(Collectors.toList());
            for (String processInstanceId : processInstanceIds) {
                processEngine.getRuntimeService().deleteProcessInstance(processInstanceId, null);
            }
            HistoricProcessInstanceQuery query = processEngine.getHistoryService().createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> histories = query.processDefinitionKey(processDefinition.getKey()).list();
            histories.forEach(processInstance -> processEngine.getHistoryService().deleteHistoricProcessInstance(processInstance.getId()));
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
            return true;
        }
    }

    @Override
    public boolean suspend(String processDefinitionKey) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition definition = this.query(processDefinitionKey, null);
        repositoryService.suspendProcessDefinitionById(definition.getId());
        return true;
    }

    public ProcessDefinitionDTO parse(ProcessDefinition processDefinition) {
        if (processDefinition == null) {
            return null;
        }
        ProcessDefinitionDTO processDefinitionDTO = new ProcessDefinitionDTO(processDefinition);
        // Parse permissions
        RepositoryService repositoryService = processEngine.getRepositoryService();
        BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());
        List<Map<String, String>> permissions = ProcessDefinitionParseHandler.extractPermissions(model);
        processDefinitionDTO.setPermissions(permissions);
        List<Map<String, String>> dataMappingMap = ProcessDefinitionParseHandler.extractFolderCabinetDataMapping(model);
        processDefinitionDTO.setFcDataMapping(dataMappingMap);
        return processDefinitionDTO;
    }
}
