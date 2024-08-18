package org.rabbit.workflow.service.bpmn;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.rabbit.workflow.models.BpmnDynamicFormDTO;
import org.rabbit.workflow.models.FileDTO;
import org.rabbit.workflow.models.FormPropertyDTO;
import org.rabbit.workflow.models.WorkflowRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The class of process definition form interface
 *
 * @author Lyle
 */
public interface IProcessFormService {

    /**
     * Get form property of process definition
     *
     * @param bpmnModel the bpmn model, not null
     * @param fromKey   the id of component
     * @return FormPropertyDTO
     */
    List<FormPropertyDTO> getByParseBpmnXml(BpmnModel bpmnModel, String fromKey);

    /**
     * Get the list of form properties associated with the process definition
     *
     * @param bpmnModel the bpmn model, not null
     * @return FormPropertyDTOs
     */
    List<BpmnDynamicFormDTO> find(BpmnModel bpmnModel);

    /**
     * Gets start properties.
     *
     * @param processDefinition the process definition, not null
     * @return the start properties
     */
    List<FormPropertyDTO> getStartProperties(ProcessDefinition processDefinition);

    /**
     * Gets form properties.
     *
     * @param taskId the task id
     * @return the form properties
     */
    List<FormPropertyDTO> getFormProperties(String taskId);

    void saveFormData(Task task, WorkflowRequestDTO request, List<MultipartFile> files, List<FileDTO> fileList) throws IOException;

    /**
     * Submit form.
     *
     * @param task     the task
     * @param properties the properties
     */
    void submitForm(Task task, Map<String, String> properties);
}
