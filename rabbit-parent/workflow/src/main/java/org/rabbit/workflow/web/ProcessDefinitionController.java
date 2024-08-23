package org.rabbit.workflow.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.common.contains.Result;
import org.rabbit.workflow.models.*;
import org.rabbit.workflow.service.ResponseUtils;
import org.rabbit.workflow.service.bpmn.CreateProcessDefinitionService;
import org.rabbit.workflow.service.bpmn.FlowableAppService;
import org.rabbit.workflow.service.bpmn.IProcessDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Process Definition APIs
 *
 * @author nine rabbit
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("${API_VERSION}/processes")
@Tag(name = "Process Definition", description = "Process Definition Controller")
public class ProcessDefinitionController {

    private final ResponseUtils responseUtils;
    private final FlowableAppService flowableAppService;
    private final IProcessDefinitionService processDefinitionService;
    private final CreateProcessDefinitionService createProcessDefinitionService;

    /**
     * Validate whether exists process definition.
     */
    @PostMapping(value = "/validation")
    public Result<Object> checkBpmnXml(MultipartFile file) throws IOException {
        return Result.ok(processDefinitionService.preParseBpmnXml(file));
    }

    /**
     * Create process definition - created by uploading BPMN.xml
     */
    @PostMapping(value = "/upload")
    public Result<ProcessDefinitionDTO> create(MultipartFile file) throws IOException {
        return Result.ok(processDefinitionService.create(file));
    }

    /**
     * Create blank process definition - created by uploading BPMN.xml
     */
    @PostMapping(value = "/create/blank")
    public Result<ProcessDefinitionDTO> createBlank(@RequestBody CreateProcessDefinitionDTO requestDTO) {
        return Result.ok(createProcessDefinitionService.createBlankProcessDefinition(requestDTO));
    }

    /**
     * Download process definition PNG diagram
     *
     * @param processDefinitionKey the key of process definition
     * @return byte[]
     */
    @GetMapping(value = "/{processDefinitionKey}/download/diagram")
    public ResponseEntity<byte[]> downloadDiagram(
            @PathVariable String processDefinitionKey, @RequestParam(required = false) String processDefinitionId
    ) throws IOException {
        return responseUtils.fileResponseEntity(processDefinitionService.downloadDiagram(processDefinitionKey, processDefinitionId));
    }

    /**
     * Download process definition bpmn.xml file
     *
     * @param processDefinitionKey the key of process definition
     * @return byte[]
     */
    @GetMapping(value = "/{processDefinitionKey}/download/xml")
    public ResponseEntity<byte[]> downloadXml(
            @PathVariable String processDefinitionKey, @RequestParam(required = false) String processDefinitionId
    ) throws IOException {
        return responseUtils.fileResponseEntity(processDefinitionService.downloadBpmnXml(processDefinitionKey, processDefinitionId));
    }

    /**
     * Check whether exists process definition.
     */
    @GetMapping(value = "/{processDefinitionKey}/exists")
    public Result<Object> isExist(@PathVariable String processDefinitionKey) {
        return Result.ok(processDefinitionService.isExists(processDefinitionKey));
    }

    @PostMapping(value = "/deploy")
    public Result<Boolean> deployProcessDefinition(@RequestBody ProcessDefinitionRequestDTO requestDTO) {
        return Result.ok(flowableAppService.deployProcessDefinition(requestDTO.getKey()));
    }

    /**
     * Get process definitions
     *
     * @param processDefinitionKey the key of process definition
     * @return List<ProcessDefinitionDTO> the list of process definition
     */
    @GetMapping(value = "/{processDefinitionKey}")
    public Result<ProcessDefinitionDTO> getByProcessDefinitionKey(@PathVariable String processDefinitionKey) {
        return Result.ok(processDefinitionService.findByKey(processDefinitionKey));
    }

    /**
     * Pagination search for process definition
     */
    @PostMapping(value = "/page")
    public Result<PaginationDTO<ProcessDefinitionDTO>> page(@RequestBody ProcessDefinitionRequestDTO requestDTO) {
        return Result.ok(processDefinitionService.page(requestDTO));
    }

    @Operation(summary = "delete a process definition")
    @DeleteMapping(value = "{processDefinitionId}")
    public Result<Boolean> delete(@PathVariable String processDefinitionId) {
        return Result.ok(processDefinitionService.delete(processDefinitionId));
    }

    @Operation(summary = "delete a process definition")
    @DeleteMapping(value = "/definition")
    public Result<Boolean> deleteByKey(@RequestParam String processDefinitionKey) {
        return Result.ok(processDefinitionService.delete(processDefinitionKey));
    }

    /**
     * Suspends the <strong>all</strong> process definitions with the given key (= id in the bpmn20.xml file).
     * If a process definition is in state suspended, it will not be possible to start new process instances based on the process definition.
     * <strong>Note: all the process instances of the process definition will still be active (ie. not suspended)!</strong>
     */
    @Operation(summary = "Suspend a process definition")
    @DeleteMapping(value = "/suspend/{processDefinitionKey}")
    public Result<Boolean> suspend(@PathVariable String processDefinitionKey) {
        return Result.ok(processDefinitionService.suspend(processDefinitionKey));
    }

    @Operation(summary = "active a process definition")
    @PostMapping(value = "/active/{processDefinitionKey}")
    public Result<Boolean> active(@PathVariable String processDefinitionKey) {
        return Result.ok(processDefinitionService.active(processDefinitionKey));
    }

    @Operation(summary = "Get the list of form properties associated with the process definition")
    @GetMapping(value = "/forms")
    public Result<List<BpmnDynamicFormDTO>> findForms(
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(required = false) String processDefinitionId) {
        return Result.ok(processDefinitionService.findForms(processDefinitionId, processDefinitionKey));
    }

    @Operation(summary = "Get form properties of single element associated with the process definition")
    @GetMapping(value = "/forms/{elementKey}")
    public Result<List<FormPropertyDTO>> getForm(
            @PathVariable String elementKey,
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(required = false) String processDefinitionId) {
        return Result.ok(processDefinitionService.getByParseBpmnXml(processDefinitionId, processDefinitionKey, elementKey));
    }

}
