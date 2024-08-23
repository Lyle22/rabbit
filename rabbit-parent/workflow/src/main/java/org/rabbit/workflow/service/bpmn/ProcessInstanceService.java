package org.rabbit.workflow.service.bpmn;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.rabbit.workflow.constants.ProcessInstanceConstants;
import org.rabbit.workflow.models.ProcessDefinitionDTO;
import org.rabbit.workflow.models.ProcessInstanceDTO;
import org.rabbit.workflow.service.ResponseUtils;
import org.rabbit.workflow.service.facade.FunctionFacadeFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * The class of Process Instance Service
 *
 * @author nine rabbit
 **/
@Service
@Slf4j
public class ProcessInstanceService implements ProcessInstanceConstants, IProcessInstanceService {

    private final ResponseUtils responseUtils;
    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final FlowableAppService appService;
    private final IProcessFormService processFormService;
    private final FunctionFacadeFactory functionFacadeFactory;
    private final IProcessDefinitionService processDefinitionService;

    public ProcessInstanceService(ResponseUtils responseUtils, ProcessEngine processEngine, RepositoryService repositoryService, RuntimeService runtimeService, FlowableAppService appService, IProcessFormService processFormService, FunctionFacadeFactory functionFacadeFactory, IProcessDefinitionService processDefinitionService) {
        this.responseUtils = responseUtils;
        this.processEngine = processEngine;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.appService = appService;
        this.processFormService = processFormService;
        this.functionFacadeFactory = functionFacadeFactory;
        this.processDefinitionService = processDefinitionService;
    }

    @Override
    public ProcessInstanceDTO start(String processKey, String businessKey, Map<String, Object> variables) {
        ProcessInstance processInstance = null;
        if (MapUtil.isNotEmpty(variables)) {
            String userId = String.valueOf(variables.get(CREATOR));
            variables.putIfAbsent(INITIATOR, userId);
            if (StringUtils.isNotBlank(userId)) {
                Authentication.setAuthenticatedUserId(userId);
            }
        } else {
            processInstance = runtimeService.startProcessInstanceByKey(processKey, businessKey);
        }
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        processInstanceBuilder = processInstanceBuilder.processDefinitionKey(processKey);
        processInstanceBuilder = processInstanceBuilder.businessKey(businessKey);
        for (String key : variables.keySet()) {
            processInstanceBuilder = processInstanceBuilder.startFormVariable(key, variables.get(key));
        }
        processInstanceBuilder.variables(variables);
        processInstance = processInstanceBuilder.start();
        return transform(processInstance);
    }

    private ProcessInstance startWithProperties(String processKey, String businessKey, Map<String, String> properties) {
        FormService formService = processEngine.getFormService();
        ProcessDefinitionDTO processDefinition = processDefinitionService.findByKey(processKey);
        if (processDefinition == null) {
            throw new IllegalArgumentException(processKey);
        }
        return formService.submitStartFormData(processDefinition.getId(), businessKey, properties);
    }

    /**
     * Gets process instances.
     *
     * @param processKey the process definition key
     * @return the process instances
     */
    public List<ProcessInstance> queryById(String processKey) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        query.processDefinitionKey(processKey).orderByStartTime().desc().list();
        return query.list();
    }

    public ProcessInstanceDTO queryProcessInstance(String processInstanceId) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        ProcessInstance processInstance = query.processInstanceId(processInstanceId).singleResult();
        ProcessInstanceDTO processInstanceDTO = ProcessInstanceDTO.builder().processInstanceId(processInstanceId).build();
        processInstanceDTO.setComplete(false);
        if (processInstance != null) {
            TaskService taskService = processEngine.getTaskService();
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            HistoryService historyService = processEngine.getHistoryService();
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .finished()
                    .singleResult();
            if (CollectionUtils.isEmpty(tasks) && hpi != null) {
                processInstanceDTO.setState("complete");
            }
            if (CollectionUtils.isNotEmpty(tasks) && !processInstance.isEnded()) {
                processInstanceDTO.setState("active");
            } else {
                processInstanceDTO.setState("start");
            }
            if (CollectionUtils.isNotEmpty(tasks)) {
                processInstanceDTO.setTaskId(tasks.get(0).getId());
            }
        } else {
            // 可能已经删除了这个审批流程
            HistoryService historyService = processEngine.getHistoryService();
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .finished()
                    .singleResult();
            if (hpi != null) {
                processInstanceDTO.setComplete(true);
                processInstanceDTO.setState("complete");
            }
        }
        return processInstanceDTO;
    }

    public ProcessInstanceDTO transform(ProcessInstance processInstance) {
        ProcessInstanceDTO processInstanceDTO = ProcessInstanceDTO.builder().build();
        processInstanceDTO.setComplete(false);
        processInstanceDTO.setProcessInstanceId(processInstance.getProcessInstanceId());
        if (processInstance != null) {
            String processInstanceId = processInstance.getProcessInstanceId();
            TaskService taskService = processEngine.getTaskService();
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            HistoryService historyService = processEngine.getHistoryService();
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).finished().singleResult();
            if (CollectionUtils.isEmpty(tasks) && hpi != null) {
                processInstanceDTO.setState("complete");
            }
            if (CollectionUtils.isNotEmpty(tasks) && !processInstance.isEnded()) {
                processInstanceDTO.setState("active");
            } else {
                processInstanceDTO.setState("start");
            }
            if (CollectionUtils.isNotEmpty(tasks)) {
                processInstanceDTO.setTaskId(tasks.get(0).getId());
            }
        }
        return processInstanceDTO;
    }

}
