package org.rabbit.workflow.service.bpmn;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

/**
 * @author nine rabbit
 **/
@Slf4j
@Component
public class CreateBpmnModelHandler {

    private ProcessEngine processEngine;

    /**
     * Creates a simple process definition with a service task that checks for existence.
     *
     * @param processDefinitionKey The key of the process definition.
     * @return The created BpmnModel instance.
     */
    public BpmnModel createProcessDefinition(String processDefinitionKey) {
        BpmnModel model = new BpmnModel();
        Process process = new Process();
        process.addFlowElement(new StartEvent("startEvent"));
        ServiceTask serviceTask = new ServiceTask("serviceTask");
        serviceTask.setImplementationType("class");
        serviceTask.setImplementation(this.getClass().getName() + "#checkExistence");
        process.addFlowElement(serviceTask);

        ExclusiveGateway exclusiveGateway = new ExclusiveGateway("exclusiveGateway");
        exclusiveGateway.setDefaultFlow("defaultFlow");
        process.addFlowElement(exclusiveGateway);

        EndEvent endEvent = new EndEvent("endEvent");
        process.addFlowElement(endEvent);

        SequenceFlow sequenceFlowTrue = new SequenceFlow("sequenceFlowTrue", "serviceTask", "exclusiveGateway");
        sequenceFlowTrue.setConditionExpression("${isExists('testIdentifier')}");
        process.addFlowElement(sequenceFlowTrue);

        SequenceFlow defaultFlow = new SequenceFlow("defaultFlow", "exclusiveGateway", "endEvent");
        process.addFlowElement(defaultFlow);

        model.addProcess(process);
        return model;
    }


    /**
     * Deploys the created process definition and starts a process instance.
     */
    public void deployAndStartProcess() {
        processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        BpmnModel bpmnModel = createProcessDefinition("DynamicProcess");
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel("DynamicProcess.bpmn20.xml", bpmnModel)
                .deploy();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("DynamicProcess");
        // Check if the process instance exists using the isExists method

    }

}
