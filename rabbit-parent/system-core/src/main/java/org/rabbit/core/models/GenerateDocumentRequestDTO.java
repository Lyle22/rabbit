package org.rabbit.core.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type of generate document request dto.
 * @author nine rabbit
 */
@Data
@Schema(description = "Generate Document (RequestDTO)")
@Builder
public class GenerateDocumentRequestDTO {

    @Schema(description = "Document Template ID")
    private String templateId;

    @Schema(description = "FolderCabinet ID")
    private String folderCabinetId;

    @Schema(description = "Parent Document Path")
    private String parentPath;

    @Schema(description = "Document Name")
    private String name;

    @Schema(description = "Document Creator")
    private String creator;

    @Schema(description = "Document Type")
    private String type;

    @Schema(description = "Document Properties")
    private Map<String, Object> properties = new HashMap<>();

    @Schema(description = "Document Language")
    private List<String> languages = new ArrayList<>();

    @Schema(description = "Variables for generate document file")
    private Map<String, Object> variables = new HashMap<>();

}
