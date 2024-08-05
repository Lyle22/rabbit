package org.rabbit.workflow.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.repository.ProcessDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type of workflow process definition DTO
 * @author Lyle
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinitionDTO implements Serializable {
    /**
     * unique identifier
     */
    private String id;

    /**
     * category name which is derived from the targetNamespace attribute in the definitions element
     */
    private String category;

    /**
     * label used for display purposes
     */
    private String name;

    /**
     * unique name for all versions this process definitions
     */
    private String key;

    /**
     * version of this process definition
     */
    private int version;

    /**
     *
     */
    private String resourceName;

    /**
     * The deployment in which this process definition is contained.
     */
    private String deploymentId;

    /**
     * The resource name in the deployment of the diagram image (if any).
     */
    private String diagramResourceName;

    /**
     * description of this process
     **/
    private String description;

//    private boolean hasStartFormKey ;
//
//    /**
//     * Does this process definition has a graphical notation defined (such that a diagram can be generated)?
//     */
//    private boolean hasGraphicalNotation ;
//
//    /** Returns true if the process definition is in suspended state. */
//    private boolean isSuspended ;
//
//    /** The tenant identifier of this process definition */
//    private String getTenantId ;
//
//    /** The derived from process definition value when this is a dynamic process definition */
//    private String getDerivedFrom ;
//
//    /** The root derived from process definition value when this is a dynamic process definition */
//    private String getDerivedFromRoot ;
//
//    /** The derived version of the process definition */
//    private int getDerivedVersion ;
//
//    /** The engine version for this process definition (5 or 6) */
//    private String getEngineVersion ;

    List<UserTaskDTO> userTasks = new ArrayList<>();

    public ProcessDefinitionDTO(ProcessDefinition processDefinition) {
        this.id = processDefinition.getId();
        this.name = processDefinition.getName();
        this.version = processDefinition.getVersion();
        this.key = processDefinition.getKey();
        this.category = processDefinition.getCategory();
        this.resourceName = processDefinition.getResourceName();
        this.deploymentId = processDefinition.getDeploymentId();
    }

    public static List<ProcessDefinitionDTO> toDTO(List<ProcessDefinition> processDefinitions) {
        return processDefinitions.stream().map(ProcessDefinitionDTO::new).collect(Collectors.toList());
    }

}
