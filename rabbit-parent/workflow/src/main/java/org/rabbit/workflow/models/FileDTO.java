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
@Schema(description = "Document File")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

    @Schema(description = "File Size")
    private long size;

    @Schema(description = "File Content")
    private byte[] content;

    @Schema(description = "zip file InputStream")
    private java.io.InputStream InputStream;

    @Schema(description = "Filename")
    private String name;

    @Schema(description = "File Mime-type")
    private String mimeType;

    @Schema(description = "Content ID")
    private String contentId;
}
