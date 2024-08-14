package org.rabbit.workflow.service.bpmn;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.rabbit.workflow.config.FlowableAppProperties;
import org.rabbit.workflow.models.AppRequestDTO;
import org.rabbit.workflow.models.CustomAppInfo;
import org.rabbit.workflow.models.DeploymentDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type of workflow app service
 */

@Slf4j
@Service
public class FlowableAppService {

    private final FlowableAppProperties flowableAppProperties;
    private final ProcessEngine processEngine;

    public FlowableAppService(FlowableAppProperties flowableAppProperties, ProcessEngine processEngine) {
        this.flowableAppProperties = flowableAppProperties;
        this.processEngine = processEngine;
    }

    public CustomAppInfo buildCustomApp() {
        return CustomAppInfo.builder().name(flowableAppProperties.getCustomName())
                .key(flowableAppProperties.getCustomName().toLowerCase().replace(" ", ""))
                .category(flowableAppProperties.getTargetNameSpace())
                .build();
    }

    public DeploymentDTO create(AppRequestDTO requestDTO) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String processKey = requestDTO.getKey();
        if (StringUtils.isBlank(requestDTO.getKey())) {
            processKey = requestDTO.getName().toLowerCase().replace(" ", "_");
        }
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentName(requestDTO.getName())
                .deploymentKey(processKey).singleResult();
        if (null != deployment) {
            return new DeploymentDTO(deployment);
        }
        // Create application
        //.category(FlowableConstants.DEFAULT_NAME_SPACE)
        return new DeploymentDTO(repositoryService.createDeployment().name(requestDTO.getName()).key(processKey).deploy());
    }

    public List<DeploymentDTO> getApps() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        return list.stream().map(DeploymentDTO::new).collect(Collectors.toList());
    }

    public DeploymentDTO getByName(String appName) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Deployment> list = repositoryService.createDeploymentQuery().deploymentName(appName)
                .orderByDeploymentTime().desc()
                .list();
        return list.stream().map(DeploymentDTO::new).findFirst().orElse(null);
    }

    /**
     * Deploy process definition to custom workflows application
     *
     * @param key the process definition key
     * @return boolean return true if success
     */
    public Boolean deployProcessDefinition(String key) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key).latestVersion().singleResult();
        // Get bpmn model
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        CustomAppInfo customApp = buildCustomApp();
        repositoryService.createDeployment()
                .name(customApp.getName())
                .key(customApp.getKey())
                .category(customApp.getCategory())
                .addBpmnModel(processDefinition.getName(), bpmnModel)
                .deploy();
        return true;
    }

    public DeploymentDTO getCustomApp() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentKey(buildCustomApp().getKey())
                .latest().singleResult();
        return new DeploymentDTO(deployment);
    }

}
