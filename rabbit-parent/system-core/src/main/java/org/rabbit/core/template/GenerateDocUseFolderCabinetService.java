package org.rabbit.core.template;

import com.wclsolution.docpal.api.repository.docpal.DocumentTemplateRepository;
import com.wclsolution.docpal.api.services.docpal.DocPalTypeService;
import com.wclsolution.docpal.api.services.docpal.ExtractOperation;
import com.wclsolution.docpal.api.services.docpal.cabinet.DocumentFolderCabinetService;
import com.wclsolution.docpal.api.services.docpal.cabinet.FolderCabinetService;
import com.wclsolution.docpal.api.services.nuxeo.DocumentService;
import com.wclsolution.docpal.api.services.nuxeo.IdentityService;
import com.wclsolution.docpal.api.utils.nuxeo.NuxeoUtils;
import com.wclsolution.docpal.api.utils.nuxeo.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The class of generate document implement service
 *
 * <p>Defines a service method that uses the folder cabinet to create documents</p>
 */
@Slf4j
@Service
public class GenerateDocUseFolderCabinetService extends AbstractDocumentTemplate {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final DocumentService documentService;
    private final ExtractOperation extractOperation;
    private final NuxeoUtils nuxeoUtils;
    private final ServiceUtils serviceUtils;
    private final DocPalTypeService docPalTypeService;
    private final IdentityService identityService;
    private final FolderCabinetService folderCabinetService;
    private final DocumentFolderCabinetService documentFolderCabinetService;

    public GenerateDocUseFolderCabinetService(
            IdentityService identityService, FolderCabinetService folderCabinetService,
            DocumentTemplateRepository documentTemplateRepository, DocumentService documentService, ExtractOperation extractOperation,
            NuxeoUtils nuxeoUtils, ServiceUtils serviceUtils, DocPalTypeService docPalTypeService,
            DocumentFolderCabinetService documentFolderCabinetService
    ) {
        super(documentTemplateRepository, documentService, extractOperation, nuxeoUtils, serviceUtils, docPalTypeService);
        this.documentTemplateRepository = documentTemplateRepository;
        this.documentService = documentService;
        this.extractOperation = extractOperation;
        this.nuxeoUtils = nuxeoUtils;
        this.serviceUtils = serviceUtils;
        this.docPalTypeService = docPalTypeService;
        this.identityService = identityService;
        this.folderCabinetService = folderCabinetService;
        this.documentFolderCabinetService = documentFolderCabinetService;
    }

    @Override
    protected Map<String, Object> convertToTemplateVariables(Map<String, Object> variables, Object value) {

        return null;
    }

}