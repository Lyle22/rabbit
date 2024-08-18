package org.rabbit.workflow.models;

import lombok.*;

/**
 * @author nine rabbit
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class CreateProcessDefinitionDTO {

    private String name;

    private String key;

    private String initiator;

    private String formKey;

    private String category;

    private String description;

}
