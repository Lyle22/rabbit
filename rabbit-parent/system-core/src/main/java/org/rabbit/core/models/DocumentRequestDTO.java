package org.rabbit.core.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Document request dto.
 * @author nine rabbit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Document (Request)")
public class DocumentRequestDTO {
    @Schema(description = "Document ID or Path")
    private String idOrPath;

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

    @Schema(description = "Page Number")
    private String pageNumber;

    @Schema(description = "Page Size")
    private String pageSize;

    @Schema(description = "File Type")
    private String fileType;//Image/Video

    private String emailId;

    @Schema(description = "FolderCabinet Template Id")
    private String templateId;

    private String oldDocPalType;

    public String getPageNumber() {
        if (pageNumber == null) {
            return "0";
        }
        return pageNumber;
    }

    public String getPageSize() {
        if (pageNumber == null) {
            return "10";
        }
        return pageSize;
    }
}
