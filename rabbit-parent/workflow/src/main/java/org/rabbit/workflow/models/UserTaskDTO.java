package org.rabbit.workflow.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.FormProperty;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.rabbit.workflow.constants.GlobalProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * The class of user task
 *
 * @author Lyle
 */
@Data
@NoArgsConstructor
public class UserTaskDTO {

    private String id;

    private String name;

    private String flowElementType;

    private List<FormPropertiesDTO> formProperties;

    public UserTaskDTO(UserTask userTask){
        this.flowElementType = GlobalProperties.FLOWELEMENTTYPE_USERTASK;
        this.id = userTask.getId();
        this.name = userTask.getName();
        this.formProperties = transFormProperties(userTask.getFormProperties());
    }

    public UserTaskDTO(String flowElementType, String id, String name, List<FormProperty> formPropertys){
        this.setFlowElementType(flowElementType);
        this.id = id;
        this.name = name;
        this.formProperties = transFormProperties(formPropertys);
    }

    public UserTaskDTO(StartEvent startEvent){
        this.flowElementType = GlobalProperties.FLOWELEMENTTYPE_STARTEVENT;
        this.id = startEvent.getId();
        this.name = startEvent.getName();
        this.formProperties = transFormProperties(startEvent.getFormProperties());
    }

    private List<FormPropertiesDTO> transFormProperties(List<FormProperty> userTasks){
        List<FormPropertiesDTO> dtoList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userTasks)){
            userTasks.stream().forEach(n->{
                dtoList.add(new FormPropertiesDTO(n));
            });
        }
        return dtoList;
    }
}
