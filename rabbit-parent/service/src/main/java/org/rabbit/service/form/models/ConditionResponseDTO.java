package org.rabbit.service.form.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionResponseDTO {

    private String key;

    private String label;

    private String type;

    private List<Map<String, Object>> options;

    /**
     * belong values [metadataMap]
     */
    private String belong;

    private Boolean isMultiple;

}
