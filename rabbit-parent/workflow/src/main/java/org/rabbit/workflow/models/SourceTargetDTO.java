package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type File dto.
 *
 * @author Lyle
 */
@Data
@Schema(description = "Source Target")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceTargetDTO {

    @Schema(description = "Source")
    private String sourceRef;

    @Schema(description = "Target")
    private String targetRef;


    public static SourceTargetDTO add(String sourceRef, String targetRef) {
        SourceTargetDTO dto = new SourceTargetDTO();
        dto.setSourceRef(sourceRef);
        dto.setTargetRef(targetRef);
        return dto;
    }

}
