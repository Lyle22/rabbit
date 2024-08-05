package org.rabbit.workflow.service.bpmn;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.repository.ProcessDefinition;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.workflow.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 定制流程定義的接口
 *
 * @author lyle
 **/
public interface IProcessDefinitionService {

    /**
     * Checks if an process definition exists based on the key.
     *
     * @param processDefinitionKey The unique key of process definition.
     * @return true if the element exists, false otherwise.
     */
    boolean isExists(String processDefinitionKey);

    /**
     * Create a process definition from an uploaded XML file.
     * [通过上传的XML文件来创建流程定义]
     *
     * @param multipartFile the multipart-file
     */
    ProcessDefinition create(MultipartFile multipartFile) throws IOException;

    boolean preParseBpmnXml(MultipartFile multipartFile) throws IOException;

    /**
     * Obtain process definition detail.
     *
     * @param processKey the key of process definition
     * @return the process definitions
     */
    ProcessDefinitionDTO findByKey(String processKey);


    /**
     * Obtain process definition detail.
     *
     * @param processKeys the keys of process definition
     * @return the process definitions
     */
    List<ProcessDefinitionDTO> findByKey(Set<String> processKeys);

    /**
     * Gets bpmn model.
     *
     * @param processKey          the process key
     * @param processDefinitionId the process definition id
     * @return the bpmn model
     */
    BpmnModel getBpmnModel(String processKey, String processDefinitionId);

    /**
     * Gets process definition.
     *
     * @param processDefinitionKey the process key
     * @param processDefinitionId  the process definition id
     * @return the process definition
     */
    ProcessDefinition query(String processDefinitionKey, String processDefinitionId);

    /**
     * Gives access to a deployed process model, e.g., a BPMN 2.0 XML file, through a stream of bytes.
     *
     * @param processKey          the key of process definition
     * @param processDefinitionId the process definition id
     * @return FileDTO
     */
    FileDTO downloadBpmnXml(String processKey, String processDefinitionId) throws IOException;

    /**
     * Gives access to a deployed process diagram, e.g., a PNG image, through a stream of bytes.
     *
     * @param processKey          the key of process definition
     * @param processDefinitionId the process definition id
     * @return FileDTO
     */
    FileDTO downloadDiagram(String processKey, String processDefinitionId) throws IOException;

    /**
     * Pagination search of process definition.
     *
     * @param requestDTO the request DTO
     * @return the Page<ProcessDefinitionDTO>
     */
    PaginationDTO<ProcessDefinitionDTO> page(ProcessDefinitionRequestDTO requestDTO);

    boolean delete(String processDefinitionId);

    boolean suspend(String processDefinitionKey);

    boolean active(String processDefinitionKey);

    /**
     * Get form properties of single element associated with the process definition
     *
     * @param processDefinitionKey the process definition key
     * @param processDefinitionId  the process definition id
     * @return FormPropertyDTO
     */
    List<FormPropertyDTO> getByParseBpmnXml(String processDefinitionId, String processDefinitionKey, String formKey);

    /**
     * Get the list of form properties associated with the process definition
     *
     * @param processDefinitionKey the process definition key
     * @param processDefinitionId  the process definition id
     * @return FormPropertyDTO
     */
    List<BpmnDynamicFormDTO> findForms(String processDefinitionId, String processDefinitionKey);

}
