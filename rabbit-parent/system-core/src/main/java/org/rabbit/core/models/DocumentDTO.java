package org.rabbit.core.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * The type Document dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Document")
public class DocumentDTO {

    @Schema(description = "Document ID")
    private String id;

    @Schema(description = "Document Name")
    private String name;

    @Schema(description = "Document Description")
    private String description;

    @Schema(description = "Document Path")
    private String path;

    @Schema(description = "Document Type")
    private String type;

    @Schema(description = "Document Version")
    private String version;

    @Schema(description = "Document Creator")
    private String createdBy;

    @Schema(description = "Document Created Date")
    private Instant createdDate;

    @Schema(description = "Docuemnt Modification Date")
    private Instant modifiedDate;

    @Schema(description = "Is Document Folder")
    private Boolean isFolder;

    @Schema(description = "Is Document Checked Out")
    private Boolean isCheckedOut;

    @Schema(description = "Document Properties")
    private Map<String, Object> properties;

    @Schema(description = "parentRef")
    private String parentRef;

    @Schema(description = "logicalPath")
    private String logicalPath;

    private String auditComment;
    private String auditName;

    @Schema(description = "permissionName")
    private List<String> permissionName;

    /**
     * Get the metadata data of the document
     *
     * @param key the key of the metadata
     * @return the value of the metadata
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

}
