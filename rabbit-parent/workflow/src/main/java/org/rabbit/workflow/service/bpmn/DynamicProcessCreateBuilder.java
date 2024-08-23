package org.rabbit.workflow.service.bpmn;

import com.google.common.collect.Lists;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.rabbit.common.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * create bpmn model handler
 *
 * @author nine rabbit
 **/
public class DynamicProcessCreateBuilder {

    private BpmnModel model;
    private Process process;
    private RepositoryService repositoryService;

    public DynamicProcessCreateBuilder(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
        this.process = new Process();
        this.model = new BpmnModel();
    }

    public void createProcess(String processKey, String processName) {
        this.model.addProcess(process);
        this.process.setId(processKey);
        this.process.setName(processName);
    }

    public StartEvent createStartEvent(
            String id, String name,
            String initiator, String formKey, boolean sameDeployment, boolean isInterrupting, String validateFormFields, ArrayList<FormProperty> formProperties) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        startEvent.setName(name);
        startEvent.setInitiator(initiator);
        startEvent.setFormKey(formKey);
        startEvent.setSameDeployment(sameDeployment);
        startEvent.setInterrupting(isInterrupting);
        startEvent.setValidateFormFields(validateFormFields);
        startEvent.setFormProperties(formProperties);
        this.process.addFlowElement(startEvent);
        return startEvent;
    }

    public EndEvent createEndEvent(String id, String name) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        endEvent.setName(name);
        this.process.addFlowElement(endEvent);
        return endEvent;
    }

    public ServiceTask createServiceTask(String implementationType, String implementationValue, String name, String id) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setImplementationType(implementationType);
        serviceTask.setImplementation("${" + implementationValue + "}");
        serviceTask.setName(name);
        serviceTask.setId(id);
        serviceTask.setFieldExtensions(Lists.newArrayList());
        this.process.addFlowElement(serviceTask);
        return serviceTask;
    }

    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef, String id, String name) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(targetRef);
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        this.process.addFlowElement(sequenceFlow);
        return sequenceFlow;
    }

    public UserTask createUserTask(String id, String name, String assignee, String formKey, String formDefinitionKey, String formDefinitionId) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setAssignee(assignee);
        userTask.setFormKey(formKey);
        this.process.addFlowElement(userTask);
        return userTask;
    }

    public ExclusiveGateway createExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        this.process.addFlowElement(exclusiveGateway);
        return exclusiveGateway;
    }

    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(targetRef);
        this.process.addFlowElement(sequenceFlow);
        return sequenceFlow;
    }

    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef, String conditionExpression) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(targetRef);
        sequenceFlow.setConditionExpression(conditionExpression);
        this.process.addFlowElement(sequenceFlow);
        return sequenceFlow;
    }

    public void autoLayout() {
        // Set up auto layout for auto generate diagram if xml doesn't exist flowable:diagram
        new BpmnAutoLayout(model).execute();
    }

    public ProcessDefinition deployProcess() {
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel(process.getId() + ".bpmn20.xml", model)
                .name(process.getName())
                .category("BusinessProcesses")
                .deploy();
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
    }

    public void exportProcessDefinitionXml(String processDefinitionId) throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(
                processDefinitionId
        ).singleResult();
        InputStream stream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        FileUtil.copyInputStreamToFile(stream, new File(processDefinition.getKey() + "process.bpmn20.xml"));
    }


    public void exportProcessDefinitionDiagram(String processDefinitionId) throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(
                processDefinitionId
        ).singleResult();
        InputStream stream = repositoryService.getProcessDiagram(processDefinition.getId());
        FileUtil.copyInputStreamToFile(stream, new File(processDefinition.getKey() + ".png"));
    }


    public static void addFieldExtension(List<FieldExtension> list, String fieldName, String expression) {
        FieldExtension fieldExtension = new FieldExtension();
        fieldExtension.setFieldName(fieldName);
        fieldExtension.setExpression(expression);
        list.add(fieldExtension);
    }

}
