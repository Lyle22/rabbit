package org.rabbit.entity.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Document Template ResponseDTO")
public class DocumentTemplateResponseDTO {

    @Schema(description = "id")
    String id;

    @Schema(description = "Document Template Name")
    String name;

    @Schema(description = "Document Id")
    String documentId;

    @Schema(description = "Document Template File Type")
    String fileType;

    @Schema(description = "Document Template Variable")
    String templateVariable;

    @Schema(description = "Document Template Description")
    String description;

    @Schema(description = "Document Template CreatedBy")
    String createdBy;

    @Schema(description = "Document Template ModifiedBy")
    String modifiedBy;

    @Schema(description = "Document Template CreatedDate")
    Instant createdDate;

    @Schema(description = "Document Template ModifiedDate")
    Instant modifiedDate;


    public DocumentTemplateResponseDTO assignValue(DocumentTemplate template) {
        setId(template.getId());
        setName(template.getName());
        setDocumentId(template.getDocumentId());
        setFileType(template.getFileType());
        setTemplateVariable(template.getTemplateVariable());
        setDescription(template.getDescription());
        setCreatedBy(template.getCreatedBy());
        setModifiedBy(template.getModifiedBy());
        setCreatedDate(template.getCreatedDate());
        setModifiedDate(template.getModifiedDate());
        return this;
    }
}
