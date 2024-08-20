package org.rabbit.workflow.service.bpmn.validate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ProcessLevelValidator;
import org.rabbit.common.contains.Result;
import org.rabbit.workflow.service.facade.FunctionFacadeFactory;
import org.rabbit.workflow.service.facade.IFunctionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business flowable validator
 *
 * @author nine rabbit
 */
public class BizFlowableValidator extends ProcessLevelValidator {

    private final Logger log = LoggerFactory.getLogger(BizFlowableValidator.class);

    private final FunctionFacadeFactory functionFacadeFactory;

    public BizFlowableValidator(FunctionFacadeFactory functionFacadeFactory) {
        this.functionFacadeFactory = functionFacadeFactory;
    }

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        // 是否设置任务接收人
        List<UserTask> userTaskList = process.findFlowElementsOfType(UserTask.class);
        validateUserTask(userTaskList, errors);

        // 检查配置服务是否达到使用条件
        List<ServiceTask> serviceTaskList = process.findFlowElementsOfType(ServiceTask.class);
        validateServiceTask(serviceTaskList, errors);

        // 路线定义规则，如果该线路上一步是排他分支，则需要指定规则
        List<SequenceFlow> sequenceFlowList = process.findFlowElementsOfType(SequenceFlow.class);
        // validateSequenceFlow(process, sequenceFlowList, errors);

    }

    private ValidationError validationErrorFactory(String aId, String aName, String msg, boolean warning) {
        ValidationError validationError = new ValidationError();
        validationError.setWarning(warning);
        validationError.setDefaultDescription(msg);
        validationError.setActivityName(aName);
        validationError.setActivityId(aId);
        return validationError;
    }

    private void validateSequenceFlow(Process process, List<SequenceFlow> sequenceFlowList, List<ValidationError> errors) {
        //        sequenceFlowList.stream().filter(s -> StringUtils.isBlank(s.getName())).forEach(s -> {
//            errors.add(validationErrorFactory(s.getId(), s.getName(), ProblemsConstants.SEQUENCEFLOW + ProblemsConstants.MISSING_NAME, true));
//        });
        sequenceFlowList.forEach(s -> {
            FlowElement flow = process.getFlowElements().stream().filter(p -> p.getId().equals(s.getSourceRef())).collect(Collectors.toList()).get(0);
            if (flow instanceof ExclusiveGateway && StringUtils.isBlank(s.getConditionExpression())) {
                String sourceName = flow.getName();
                String targetName = process.getFlowElements().stream().filter(p -> p.getId().equals(s.getTargetRef())).collect(Collectors.toList()).get(0).getName();
                errors.add(validationErrorFactory(s.getId(), s.getName(), sourceName + " To " + targetName + ProblemsConstants.NO_RULES_FOUND, false));
            }
        });

    }

    private void validateUserTask(List<UserTask> userTaskList, List<ValidationError> errors) {
        if (CollectionUtils.isEmpty(userTaskList)) {
            // errors.add(validationErrorFactory(null, ProblemsConstants.NO_USERTASK_FOUND, ProblemsConstants.NO_USERTASK_FOUND, true));
            log.info(ProblemsConstants.NO_USERTASK_FOUND);
        } else {
            userTaskList.stream().filter(s -> StringUtils.isBlank(s.getName())).forEach(s -> {
                errors.add(validationErrorFactory(s.getId(), s.getName(), ProblemsConstants.USERTASK + ProblemsConstants.MISSING_NAME, true));
            });
            userTaskList.stream().filter(s -> CollectionUtils.isEmpty(s.getCandidateUsers()) && CollectionUtils.isEmpty(s.getCandidateGroups()))
                    .forEach(s -> {
                        List<FlowableListener> listeners = s.getTaskListeners();
                        List<FlowableListener> assigneeListener = listeners.stream().filter(item -> {
                            return item.getFieldExtensions().stream().anyMatch(ext -> ext.getFieldName().equalsIgnoreCase("assignee"));
                        }).collect(Collectors.toList());

                        if (CollectionUtils.isEmpty(assigneeListener)) {
                            String errorMessage = s.getClass().getSimpleName() + " " + ProblemsConstants.NO_TASK_RECIPIENT_FOUND;
                            errors.add(validationErrorFactory(s.getId(), s.getName(), errorMessage, false));
                        }
                    });
        }
    }

    private void validateServiceTask(List<ServiceTask> serviceTaskList, List<ValidationError> errors) {
        if (CollectionUtils.isEmpty(serviceTaskList)) {
            return;
        }
        for (ServiceTask serviceTask : serviceTaskList) {
            String implementationType = serviceTask.getImplementationType();
            if (StringUtils.isNotBlank(implementationType) && implementationType.equals(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION)) {
                String name = serviceTask.getName();
                String id = serviceTask.getId();
                String expression = serviceTask.getImplementation();
                String expressionDelegate = expression.replaceFirst("\\$\\{", "").replace("}", "");
                String functionServiceName = expressionDelegate.replace("Delegate", "Service");

                List<FieldExtension> fieldExtensions = serviceTask.getFieldExtensions();
                Map<String, String> fieldMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(fieldExtensions)) {
                    for (FieldExtension fieldExt : fieldExtensions) {
                        if (StringUtils.isNotBlank(fieldExt.getExpression())) {
                            fieldMap.put(fieldExt.getFieldName(), fieldExt.getExpression());
                        } else if (StringUtils.isNotBlank(fieldExt.getStringValue())) {
                            fieldMap.put(fieldExt.getFieldName(), fieldExt.getStringValue());
                        } else {
                            fieldMap.put(fieldExt.getFieldName(), null);
                        }
                    }
                }

                IFunctionFacade functionFacade = functionFacadeFactory.selectService(functionServiceName);
                if (null != functionFacade) {
                    Result<String> validateRes = functionFacade.validate(fieldMap, functionServiceName);
                    if (validateRes.getCode() == 200) {
                        // not error
                    } else {
                        errors.add(validationErrorFactory(id, name, serviceTask.getClass().getSimpleName() + " " + validateRes.getMessage(), false));
                    }
                }
            } else {
                log.info(serviceTask.getName());
            }
        }
    }
}
