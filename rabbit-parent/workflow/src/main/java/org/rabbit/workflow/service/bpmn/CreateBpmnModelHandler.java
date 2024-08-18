package org.rabbit.workflow.service.bpmn;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ProcessEngine;
import org.rabbit.workflow.models.SourceTargetDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * create bpmn model handler
 *
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
    public static BpmnModel createProcessDefinition(String processDefinitionKey) {
        List<SourceTargetDTO> sequenceFlows = Lists.newArrayList();
        BpmnModel model = new BpmnModel();
        Process process = new Process();
        StartEvent startEvent = createStartEvent("nine", null, false, true, null, null);
        process.addFlowElement(startEvent);

        ServiceTask serviceTask = createServiceTask(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION, "uploadFileDelegate");
        process.addFlowElement(serviceTask);

        EndEvent endEvent = createEndEvent("end", "End Event", null, false, true, null, null);
        process.addFlowElement(endEvent);

        sequenceFlows.add(SourceTargetDTO.add(startEvent.getId(), serviceTask.getId()));
        sequenceFlows.add(SourceTargetDTO.add(serviceTask.getId(), endEvent.getId()));
        setSequenceFlows(process, sequenceFlows);

        process.setId(processDefinitionKey);
        process.setName(processDefinitionKey);
        model.addProcess(process);
        return model;
    }

    public static StartEvent createStartEvent(String initiator, String formKey, boolean sameDeployment, boolean isInterrupting, String validateFormFields, ArrayList<FormProperty> formProperties) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        startEvent.setName("start event");
        startEvent.setInitiator(initiator);
        startEvent.setFormKey(formKey);
        startEvent.setSameDeployment(sameDeployment);
        startEvent.setInterrupting(isInterrupting);
        startEvent.setValidateFormFields(validateFormFields);
        startEvent.setFormProperties(formProperties);
        return startEvent;
    }

    public static EndEvent createEndEvent(String id, String name, String formKey, boolean sameDeployment, boolean isInterrupting, String validateFormFields, ArrayList<FormProperty> formProperties) {
        EndEvent event = new EndEvent();
        if (StringUtils.isBlank(id)) {
            event.setId("endEvent");
        } else {
            event.setId(id);
        }
        if (StringUtils.isBlank(name)) {
            event.setName("End Event");
        } else {
            event.setName(name);
        }
        return event;
    }

    public void add(List<SourceTargetDTO> sequenceFlows, String source, String target) {
        SourceTargetDTO element = SourceTargetDTO.add(source, target);
        sequenceFlows.add(element);
    }

    public static void setSequenceFlows(Process process, List<SourceTargetDTO> sequenceFlows) {
        // check list

        // setup SequenceFlow list
        for (SourceTargetDTO sequenceFlow : sequenceFlows) {
            SequenceFlow flow = new SequenceFlow();
            flow.setSourceRef(sequenceFlow.getSourceRef());
            flow.setTargetRef(sequenceFlow.getTargetRef());
//            flow.setConditionExpression("${isExists('testIdentifier')}");
            process.addFlowElement(flow);
        }
    }

    public static ServiceTask createServiceTask(String implementationType, String implementationValue) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setImplementationType(implementationType);
        serviceTask.setImplementation("${" + implementationValue + "}");
        serviceTask.setName("Service Task");
        serviceTask.setId("serviceTask1");
        serviceTask.setFieldExtensions(Lists.newArrayList());
        return serviceTask;
    }


}
