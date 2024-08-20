package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.bpmn.model.FormProperty;

import java.util.Map;

/**
 * The type Form property dto.
 * @author nine rabbit
 */
@Data
@Schema(description = "Form Property")
@AllArgsConstructor
@NoArgsConstructor
public class FormPropertiesDTO {
    @Schema(description = "Property Key")
    private String id;

    @Schema(description = "Property Name")
    private String name;

    @Schema(description = "Property Type")
    private String type;

    @Schema(description = "Property Value")
    private String value;

    @Schema(description = "Is Property Readable")
    private Boolean readable;

    @Schema(description = "Is Property Required")
    private Boolean required;

    @Schema(description = "Is Property Writable")
    private Boolean writable;

    @Schema(description = "Property expression")
    private String expression;

    @Schema(description = "Property variable")
    private String variable;

    @Schema(description = "Property defaultExpression")
    private String defaultExpression;

    @Schema(description = "Enum Options")
    private Map<String, String> options;

    public FormPropertiesDTO(FormProperty formProperty){
        this.id = formProperty.getId();
        this.name = formProperty.getName();
        this.type = formProperty.getType();
        this.readable = formProperty.isReadable();
        this.required = formProperty.isRequired();
        this.writable = formProperty.isWriteable();
        this.expression = formProperty.getExpression();
        this.variable= formProperty.getVariable();
        this.defaultExpression = formProperty.getDefaultExpression();
    }
}
