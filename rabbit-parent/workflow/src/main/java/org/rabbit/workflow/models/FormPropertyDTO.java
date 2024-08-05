package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.FormType;
import org.flowable.engine.impl.persistence.entity.HistoricFormPropertyEntityImpl;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * The type Form property dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Form Property")
public class FormPropertyDTO {
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

    @Schema(description = "Enum Options")
    private Map<String, String> options;

    @Schema(description = "time")
    private Date time;

    public FormPropertyDTO(HistoricFormPropertyEntityImpl his) {
        this.id = his.getPropertyId();
        this.value = his.getPropertyValue();
        this.name = his.getDetailType();
        this.time = his.getTime();
    }

    public FormPropertyDTO(@NotNull FormProperty formProperty) {
        FormType type = formProperty.getType();
        setId(formProperty.getId());
        setName(formProperty.getName());
        setType(formProperty.getType().getName());
        setValue(formProperty.getValue());
        setReadable(formProperty.isReadable());
        setRequired(formProperty.isRequired());
        setWritable(formProperty.isWritable());
        Object options = type.getInformation("values");
        if (options != null) {
            setOptions((Map<String, String>) options);
        }
    }

}
