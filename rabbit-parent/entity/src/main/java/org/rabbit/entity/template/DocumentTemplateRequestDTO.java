package org.rabbit.entity.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.rabbit.common.base.BasePageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * The type of Document Template RequestDTO
 * @author nine rabbit
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Document Template RequestDTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentTemplateRequestDTO extends BasePageRequest {

    @Schema(description = "id")
    String id;

    @Schema(description = "Document Template Name")
    String name;

    @Schema(description = "Nuxeo Document Id")
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

    MultipartFile file;

    List<String> fileTypes;

    List<String> createdBys;

    Map<String, String> variables;

    public DocumentTemplate to() {
        DocumentTemplate template = new DocumentTemplate();
        template.setId(getId());
        template.setName(getName());
        template.setDocumentId(getDocumentId());
        template.setFileType(getFileType());
        template.setTemplateVariable(getTemplateVariable());
        template.setDescription(getDescription());
        template.setCreatedBy(getCreatedBy());
        template.setModifiedBy(getModifiedBy());
        template.setCreatedDate(getCreatedDate());
        template.setModifiedDate(getModifiedDate());
        return template;
    }

}
