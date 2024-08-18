package org.rabbit.workflow.service.bpmn;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.content.api.ContentItem;
import org.flowable.content.api.ContentService;
import org.flowable.content.engine.ContentEngines;
import org.flowable.engine.FormService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.FormType;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.jetbrains.annotations.NotNull;
import org.rabbit.workflow.constants.GlobalProperties;
import org.rabbit.workflow.models.BpmnDynamicFormDTO;
import org.rabbit.workflow.models.FileDTO;
import org.rabbit.workflow.models.FormPropertyDTO;
import org.rabbit.workflow.models.WorkflowRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessFormService implements IProcessFormService {

    private final ProcessEngine processEngine;
    private final FlowableAppService appService;

    public ProcessFormService(ProcessEngine processEngine, FlowableAppService appService) {
        this.processEngine = processEngine;
        this.appService = appService;
    }

    @Override
    public List<FormPropertyDTO> getByParseBpmnXml(BpmnModel bpmnModel, String fromKey) {
        List<FormPropertyDTO> fields = Lists.newArrayList();
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            Map<String, FlowElement> flowElementsMap = process.getFlowElementMap();
            for (Map.Entry<String, FlowElement> map : flowElementsMap.entrySet()) {
                FlowElement currElement = map.getValue();
                if (fromKey.equals(currElement.getId())) {
                    if (currElement instanceof UserTask) {
                        UserTask userTask = (UserTask) currElement;
                        List<org.flowable.bpmn.model.FormProperty> formProperties = userTask.getFormProperties();
                        fields = formProperties.stream().map(this::transform).collect(Collectors.toList());
                    }
                    if (currElement instanceof ServiceTask) {
                        ServiceTask currTask = (ServiceTask) currElement;
                        List<FieldExtension> fieldExtensions = currTask.getFieldExtensions();
                        fields = transform(fieldExtensions);
                    }
                    return fields;
                }
            }
        }
        return fields;
    }

    @Override
    public List<BpmnDynamicFormDTO> find(BpmnModel bpmnModel) {
        // 查询动态表单的信息
        List<BpmnDynamicFormDTO> dynamicForms = Lists.newArrayList();
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            Map<String, FlowElement> flowElementsMap = process.getFlowElementMap();
            for (Map.Entry<String, FlowElement> map : flowElementsMap.entrySet()) {
                FlowElement currElement = map.getValue();
                if (currElement instanceof UserTask) {
                    UserTask userTask = (UserTask) currElement;
                    List<org.flowable.bpmn.model.FormProperty> formProperties = userTask.getFormProperties();
                    List<FormPropertyDTO> formPropertyDTOS = formProperties.stream().map(this::transform).collect(Collectors.toList());
                    dynamicForms.add(new BpmnDynamicFormDTO(currElement.getId(), currElement.getName(), "UserTask", formPropertyDTOS));
                }
                if (currElement instanceof ServiceTask) {
                    ServiceTask currTask = (ServiceTask) currElement;
                    if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(currTask.getImplementationType())) {
                        List<FieldExtension> fields = currTask.getFieldExtensions();
                        List<FormPropertyDTO> formPropertyDTOS = transform(fields);
                        dynamicForms.add(new BpmnDynamicFormDTO(currElement.getId(), currElement.getName(), "ServiceTask", formPropertyDTOS));
                    }
                }
            }
        }
        return dynamicForms;
    }

    private List<FormPropertyDTO> transform(List<FieldExtension> fields) {
        if (fields.isEmpty()) {
            return new ArrayList<>();
        }
        return fields.stream().map(fieldExtension -> {
            FormPropertyDTO property = new FormPropertyDTO();
            property.setId(fieldExtension.getFieldName());
            property.setName(fieldExtension.getFieldName());
            property.setValue(fieldExtension.getStringValue());
            property.setType(fieldExtension.getExpression());
            return property;
        }).collect(Collectors.toList());
    }

    private FormPropertyDTO transform(org.flowable.bpmn.model.FormProperty formProperty) {
        FormPropertyDTO property = new FormPropertyDTO();
        property.setId(formProperty.getId());
        property.setName(formProperty.getName());
        property.setType(formProperty.getType());
        property.setValue(new Gson().toJson(formProperty.getFormValues()));
        property.setReadable(formProperty.isReadable());
        property.setRequired(formProperty.isRequired());
        property.setWritable(formProperty.isWriteable());
        // property.setOptions(formProperty.getVariable());
        return property;
    }

    private @NotNull FormPropertyDTO formProperty(@NotNull FormProperty formProperty) {
        FormType type = formProperty.getType();
        Object options = type.getInformation("values");
        FormPropertyDTO property = new FormPropertyDTO();
        property.setId(formProperty.getId());
        property.setName(formProperty.getName());
        property.setType(formProperty.getType().getName());
        property.setValue(formProperty.getValue());
        property.setReadable(formProperty.isReadable());
        property.setRequired(formProperty.isRequired());
        property.setWritable(formProperty.isWritable());
        if (options != null) {
            property.setOptions((Map<String, String>) options);
        }
        return property;
    }

    private @NotNull List<FormPropertyDTO> formProperties(@NotNull List<FormProperty> formProperties) {
        List<FormPropertyDTO> formData = new ArrayList<>();
        formProperties.forEach(property -> formData.add(formProperty(property)));
        return formData;
    }

    @Override
    public List<FormPropertyDTO> getFormProperties(String taskId) {
        return formProperties(getTaskFormProperties(taskId));
    }

    private List<FormProperty> getTaskFormProperties(String taskId) {
        FormService formService = processEngine.getFormService();
        TaskFormData formData = formService.getTaskFormData(taskId);
        if (formData == null) {
            return new ArrayList<>();
        }
        return formData.getFormProperties();
    }

    @Override
    public List<FormPropertyDTO> getStartProperties(@NotNull ProcessDefinition processDefinition) {
        FormService formService = processEngine.getFormService();
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        return formProperties(startFormData.getFormProperties());
    }

    /**
     * Save form data.
     *
     * @param task the workflow
     * @param files    the files
     * @throws IOException the io exception
     */
    @Override
    public void saveFormData(Task task, WorkflowRequestDTO request, List<MultipartFile> files, List<FileDTO> fileList) throws IOException {
        String contentId;
        FormProperty property;
        String taskId = request.getTaskId();
        List<FormProperty> formProperties = getTaskFormProperties(taskId);
        Map<String, String> attachments = request.getAttachments();
        if (attachments != null && !attachments.isEmpty() && files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                final String filename = file.getOriginalFilename();
                final String propertyId = attachments.get(filename);
                property = formProperties.stream()
                        .filter(p -> StringUtils.equals(p.getId(), propertyId))
                        .findAny()
                        .orElse(null);
                if (property != null) {
                    if (StringUtils.isNotBlank(propertyId)) {
                        contentId = property.getValue();
                        if (StringUtils.isBlank(contentId)) {
                            saveContent(task, file, request, propertyId);
                        } else {
                            FileDTO fileDTO = fileList.stream()
                                    .filter(f -> StringUtils.equals(f.getName(), filename))
                                    .findAny()
                                    .orElse(null);
                            if (fileDTO == null) {
                                saveContent(task, file, request, propertyId);
                            }
                        }
                    }
                }
            }
        }

        // Save properties into task variables table
        processEngine.getFormService().saveFormData(taskId, request.getProperties());
    }

    private Boolean isProcessKeyKey(String taskId, String processKey) {
        if (StringUtils.isBlank(taskId)) {
            return false;
        }
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().taskId(taskId).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            Task task = taskList.get(0);
            List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService()
                    .createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).list();
            if (CollectionUtils.isNotEmpty(processDefinitions)) {
                ProcessDefinition processDefinition = processDefinitions.get(0);
                if (null != processKey && processKey.equals(processDefinition.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Submit form.
     *
     * @param task     the task
     * @param properties the properties
     */
    @Override
    public void submitForm(Task task, Map<String, String> properties) {
        FormService formService = processEngine.getFormService();
        this.checkProperties(task.getId(), properties);
        formService.submitTaskFormData(task.getId(), properties);
    }

    private void checkProperties(String taskId, Map<String, String> properties) {
        if (Boolean.FALSE.equals(this.isProcessKeyKey(taskId, GlobalProperties.CONTRAC_TAPPROVAL))) {
            return;
        }
        TaskFormData taskFormData = processEngine.getFormService().getTaskFormData(taskId);
        List<FormProperty> propertyList = taskFormData.getFormProperties();
        propertyList.stream().forEach(item -> {
            if (item.isRequired() && StrUtil.isBlank(properties.get(item.getId()))) {
                throw new RuntimeException(item.getId() + " is required");
            }
            if ((!item.isWritable()) && StrUtil.isNotBlank(properties.get(item.getId()))) {
                throw new RuntimeException(item.getId() + " is not writable");
            }
        });
    }

    /**
     * Save from properties.
     *
     * @param taskId     the task id
     * @param properties the properties
     */
    public void saveFromProperties(String taskId, Map<String, String> properties) {
        FormService formService = processEngine.getFormService();
        formService.saveFormData(taskId, properties);
    }

    private void saveContent(Task task, MultipartFile file, WorkflowRequestDTO request, String propertyId) throws IOException {
        String value;
        String contentId = saveContent(task, file);
        if (request.getProperties() == null) {
            request.setProperties(new HashMap<>());
        }
        if (request.getProperties().containsKey(propertyId)) {
            value = request.getProperty(propertyId);
            request.putProperty(
                    propertyId, value == null ? contentId : value + "," + contentId
            );
        } else {
            request.putProperty(propertyId, contentId);
        }
    }

    private String saveContent(Task task, MultipartFile file) throws IOException {
        ContentService contentService = ContentEngines.getDefaultContentEngine().getContentService();
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setTaskId(task.getId());
        contentItem.setName(file.getOriginalFilename());
        contentItem.setContentStoreName("file");
        contentItem.setMimeType(file.getContentType());
        contentItem.setProcessInstanceId(task.getProcessInstanceId());
        contentService.saveContentItem(contentItem, file.getInputStream());
        return contentItem.getId();
    }

}
