package org.rabbit.core.template;

import com.wclsolution.docpal.api.dbmodel.docpal.DocumentTemplate;
import com.wclsolution.docpal.api.exception.DocPalCustomException;
import com.wclsolution.docpal.api.security.ErrorCode;
import com.wclsolution.docpal.api.services.docpal.DocumentTemplateService;
import com.wclsolution.docpal.api.utils.nuxeo.NuxeoUtils;
import com.wclsolution.docpal.api.viewmodels.request.GenerateDocumentRequestDTO;
import com.wclsolution.docpal.api.viewmodels.response.DocumentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.NuxeoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * The class of generate document implement service
 * <p>Use templates to generate new documents according to different creation modes</p>
 *
 * @author Lyle
 */
@Slf4j
@Service
public class GenerateDocumentService {

    private GenerateDocumentMode generateMode;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    public GenerateDocumentMode choose(GenerateDocumentRequestDTO requestDTO) {
        if (StringUtils.isNotBlank(requestDTO.getFolderCabinetId())) {
            return new FolderCabinetGenerateDocumentMode();
        }
        if (StringUtils.isNotBlank(requestDTO.getParentPath())) {
            return new DefaultGenerateDocumentMode();
        }
        log.error("folder cabinet id or specify parent path cannot be null at the same time");
        throw new DocPalCustomException(ErrorCode.GLOBAL, "Invalid generate document mode when use document template");
    }

    /**
     * Generate document using document template, Now supports two modes (folder cabinet and specify parent path)
     * <br/>
     * Note that does not check if template document's variables are required when create document using template?
     * <p>
     * For example:
     * If the template document has three variables (name, label, description) and the request parameter {@see GenerateDocumentRequestDTO#variables} is empty,
     * the document can be created successfully.
     * </p>
     *
     * @param requestDTO the request DTO
     * @return ResponseEntity
     */
    public DocumentDTO generateDocument(GenerateDocumentRequestDTO requestDTO, NuxeoClient client) {
        Assert.notNull(requestDTO.getTemplateId(), "Template ID must be not null");
        // 1. First, query document template and checking is exist?
        DocumentTemplate template = documentTemplateService.getById(requestDTO.getTemplateId());
        if (StringUtils.isBlank(template.getDocumentId())) {
            throw new DocPalCustomException(ErrorCode.GLOBAL, "Please upload template file first, Template Name=" + template.getName());
        }
        if (requestDTO.getVariables() == null || requestDTO.getVariables().isEmpty()) {
            log.warn("Those variables is null when generate document file. Document Template ID is {}", requestDTO.getTemplateId());
        }
        if (null == client) {
            client = NuxeoUtils.getNuxeoClient();
        }

        GenerateDocumentMode generateMode = choose(requestDTO);

        // Get replace value in file content
        Map<String, Object> variables = new HashMap<String, Object>();

        // Download template file into local file system

        // generate file content for document content

        // generate document and then upload it

        // set up document access control permissions

        generateMode.mode();

        return null;
    }
}