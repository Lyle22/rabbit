package org.rabbit.core.template;

import cn.hutool.core.map.MapUtil;
import com.wclsolution.docpal.api.constants.nuxeo.HttpPath;
import com.wclsolution.docpal.api.constants.nuxeo.SchemaName;
import com.wclsolution.docpal.api.dbmodel.docpal.DocPalTypeMetadata;
import com.wclsolution.docpal.api.dbmodel.docpal.DocumentTemplate;
import com.wclsolution.docpal.api.exception.DocPalCustomException;
import com.wclsolution.docpal.api.repository.docpal.DocumentTemplateRepository;
import com.wclsolution.docpal.api.security.ErrorCode;
import com.wclsolution.docpal.api.services.docpal.DocPalTypeService;
import com.wclsolution.docpal.api.services.docpal.ExtractOperation;
import com.wclsolution.docpal.api.services.nuxeo.DocumentService;
import com.wclsolution.docpal.api.utils.CommonUtils;
import com.wclsolution.docpal.api.utils.JsonHelper;
import com.wclsolution.docpal.api.utils.nuxeo.NuxeoUtils;
import com.wclsolution.docpal.api.utils.nuxeo.ServiceUtils;
import com.wclsolution.docpal.api.viewmodels.request.DocumentRequestDTO;
import com.wclsolution.docpal.api.viewmodels.response.DocumentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.blob.Blob;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractDocumentTemplate {

    protected static final Set<String> FILE_TYPES = Stream.of("Word", "Excel", "PPT").collect(Collectors.toSet());
    private final DocumentTemplateRepository documentTemplateRepository;
    private final DocumentService documentService;
    private final ExtractOperation extractOperation;
    private final NuxeoUtils nuxeoUtils;
    private final ServiceUtils serviceUtils;
    private final DocPalTypeService docPalTypeService;

    public AbstractDocumentTemplate(DocumentTemplateRepository documentTemplateRepository, DocumentService documentService,
                             ExtractOperation extractOperation, NuxeoUtils nuxeoUtils, ServiceUtils serviceUtils, DocPalTypeService docPalTypeService
    ) {
        this.documentTemplateRepository = documentTemplateRepository;
        this.documentService = documentService;
        this.extractOperation = extractOperation;
        this.nuxeoUtils = nuxeoUtils;
        this.serviceUtils = serviceUtils;
        this.docPalTypeService = docPalTypeService;
    }

    /**
     * Get template variables through request parameters
     *
     * @param variables the request variables
     * @param value     the operation object
     * @return Map<String, Object>  template variables
     */
    protected abstract Map<String, Object> convertToTemplateVariables(Map<String, Object> variables, Object value);

    // protected abstract Document findParentDocument(Map<String, Object> variables, Object value);

    protected DocumentDTO generate(String documentTemplateId, Map<String, Object> templateVariables, String parentDocumentPath, String docPalType, String documentName) throws IOException {
        Assert.notNull(documentTemplateId, "Document Template ID must be not null");
        Assert.notNull(parentDocumentPath, "Parent Document Path must be not null");
        Assert.notNull(docPalType, "document type cannot be null");
        if (MapUtil.isEmpty(templateVariables)) {
            log.error("template variables is empty. {} = {}", documentTemplateId, parentDocumentPath);
            throw new DocPalCustomException(ErrorCode.GLOBAL, "Missing parameters");
        }
        DocumentTemplate template = getById(documentTemplateId);
        if (StringUtils.isBlank(template.getDocumentId())) {
            throw new DocPalCustomException(ErrorCode.GLOBAL, "Please upload template file first");
        }

        NuxeoClient client = NuxeoUtils.getNuxeoClient();
        final String tempDirectoryPath = System.getProperty("java.io.tmpdir") + HttpPath.PATH_DELIMITER + UUID.randomUUID();
        // 1. Download template file into local file system
        File templateFile = loadTemplateDocument(client, template.getDocumentId(), tempDirectoryPath, "origin_file");
        try {
            // 2. Start replace value in file content
            String extension = FilenameUtils.getExtension(templateFile.getName());
            String name = StringUtils.isBlank(documentName) ? template.getName() : documentName;
            String documentPath = CommonUtils.appendCharacters(parentDocumentPath, HttpPath.PATH_DELIMITER) + name + "." + extension;
            Map<String, Object> originVariables = templateVariables;
            List<String> languages = new ArrayList<>();
            // 3. generate file content for document content
            MultipartFile multipartFile = replaceVariable(templateFile, name, templateVariables, tempDirectoryPath, extension);
            List<String> metadata = docPalTypeService.find(docPalType).getMetadata().stream().map(DocPalTypeMetadata::getMetadata).collect(Collectors.toList());
            originVariables.keySet().removeIf(key -> !metadata.contains(key));
            if (null == nuxeoUtils.fetchDocument(client, documentPath)) {
                // 4. Do generate document (Check if the document exists, and if it exists then overwrite?)
                List<MultipartFile> files = List.of(multipartFile);
                return documentService.createDocument(documentPath, name, docPalType, originVariables, languages, files, client, null);
            } else {
                // 4. Override file content
                DocumentRequestDTO updateRequest = new DocumentRequestDTO();
                updateRequest.setIdOrPath(documentPath);
                updateRequest.setName(name);
                updateRequest.setProperties(originVariables);
                documentService.replaceFileContent(JsonHelper.write(updateRequest), multipartFile);
                documentService.updateDocument(updateRequest);
                return serviceUtils.transform(nuxeoUtils.getSimpleDocument(client, documentPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to generate file using document template, DocumentId:[{}] Variables:[{}]", template.getDocumentId(), templateVariables);
        } finally {
            FileUtils.deleteDirectory(new File(tempDirectoryPath));
        }
        log.error("Failed to generate file using document template, DocumentId:[{}] Variables:[{}]", template.getDocumentId(), templateVariables);
        return null;
    }

    private DocumentDTO doGenerateDocument(
            NuxeoClient client, String documentName, String documentPath, String docpalType, Map<String, Object> properties, List<String> languages, MultipartFile multipartFile
    ) throws Exception {
        // 检查文档是否存在, 如果存在则覆盖
        if (null == nuxeoUtils.fetchDocument(client, documentPath)) {
            List<MultipartFile> files = List.of(multipartFile);
            return documentService.createDocument(documentPath, documentName, docpalType, properties, languages, files, client, null);
        } else {
            // Override file content
            DocumentRequestDTO updateRequest = new DocumentRequestDTO();
            updateRequest.setIdOrPath(documentPath);
            updateRequest.setName(documentName);
            updateRequest.setProperties(properties);
            documentService.replaceFileContent(JsonHelper.write(updateRequest), multipartFile);
            documentService.updateDocument(updateRequest);
            return serviceUtils.transform(nuxeoUtils.getSimpleDocument(client, documentPath));
        }
    }

    protected DocumentTemplate findById(String id) {
        return documentTemplateRepository.findById(id).orElse(null);
    }

    protected List<DocumentTemplate> findAll() {
        return documentTemplateRepository.findAll();
    }

    protected DocumentTemplate getById(String id) {
        return documentTemplateRepository.findById(id).orElseThrow(() -> new DocPalCustomException(ErrorCode.GLOBAL, "Document Template does not exists"));
    }

    protected Set<String> getSupportFileType() {
        return FILE_TYPES;
    }

    /**
     * Download document to generated local file
     *
     * @param client             client instance
     * @param templateDocumentId template document id
     * @param tempDirectoryPath  temporary directory path
     * @param tempFileName       temporary file name
     * @return File the local file
     */
    protected File loadTemplateDocument(NuxeoClient client, String templateDocumentId, String tempDirectoryPath, String tempFileName) {
        try {
            Document document = nuxeoUtils.getSimpleDocument(client, templateDocumentId, SchemaName.DUBLINCORE, SchemaName.UID);
            Blob blob = nuxeoUtils.documentGetBlob(client, document.getPath());
            String extension = FilenameUtils.getExtension(blob.getFilename());
            Files.createDirectories(Paths.get(tempDirectoryPath));
            String tmpDocFilePath = tempDirectoryPath + HttpPath.PATH_DELIMITER + "origin_file." + extension;
            InputStream is = blob.getStream();
            byte[] content = IOUtils.toByteArray(is);
            Path tempPath = Paths.get(tmpDocFilePath);
            Files.write(tempPath, content, StandardOpenOption.CREATE);
            return new File(tempPath.toUri());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new DocPalCustomException(ErrorCode.GLOBAL, "template document does not exist");
    }

    /**
     * Replace the file content variables of the template document file
     *
     * @param templateFile      the template document file
     * @param name              the file name
     * @param variables         the file variables
     * @param tempDirectoryPath the temp directory Path
     * @param extension         file name extension
     */
    protected MultipartFile replaceVariable(File templateFile, String name, Map<String, Object> variables, String tempDirectoryPath, String extension) {
        try {
            byte[] bytes = extractOperation.replace(templateFile, extension, variables);
            String filePath = tempDirectoryPath + HttpPath.PATH_DELIMITER + name + "." + extension;
            Files.write(Paths.get(filePath), bytes, StandardOpenOption.CREATE);
            File documentFile = new File(filePath);
            String mimeType = new MimetypesFileTypeMap().getContentType(documentFile);
            return CommonUtils.fileToMultipartFile(documentFile, mimeType);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new DocPalCustomException(ErrorCode.GLOBAL, "Failed to replace template variable");
    }

}