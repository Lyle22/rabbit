package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * BPMN Dynamic Form Information DTO
 *
 * @author Lyle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "BPMN Dynamic Form Information DTO")
public class BpmnDynamicFormDTO {

    String id;

    String name;

    String type;

    List<FormPropertyDTO> properties = new ArrayList<>();

}
