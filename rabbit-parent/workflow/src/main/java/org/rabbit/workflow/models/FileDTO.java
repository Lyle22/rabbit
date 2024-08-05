package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type File dto.
 */
@Data
@Schema(description = "Document File")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    @Schema(description = "File Size")
    private long size;

    @Schema(description = "File Content")
    private byte[] content;

    @Schema(description = "Filename")
    private String name;

    @Schema(description = "File Mime-type")
    private String mimeType;

    @Schema(description = "Content ID")
    private String contentId;
}
