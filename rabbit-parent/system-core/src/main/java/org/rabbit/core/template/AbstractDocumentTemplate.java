package org.rabbit.core.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.rabbit.core.models.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AbstractDocumentTemplate
 *
 * @author nine rabbit
 */
@Slf4j
public abstract class AbstractDocumentTemplate {

    protected static final Set<String> FILE_TYPES = Stream.of("Word", "Excel", "PPT").collect(Collectors.toSet());

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
//        Assert.notNull(documentTemplateId, "Document Template ID must be not null");
//        Assert.notNull(parentDocumentPath, "Parent Document Path must be not null");
//        Assert.notNull(docPalType, "document type cannot be null");
//        if (MapUtil.isEmpty(templateVariables)) {
//            log.error("template variables is empty. {} = {}", documentTemplateId, parentDocumentPath);
//            throw new ClientCustomException(ErrorCode.GLOBAL, "Missing parameters");
//        }
//        DocumentTemplate template = getById(documentTemplateId);
//        if (StringUtils.isBlank(template.getDocumentId())) {
//            throw new ClientCustomException(ErrorCode.GLOBAL, "Please upload template file first");
//        }
//
//        NuxeoClient client = NuxeoUtils.getNuxeoClient();
//        final String tempDirectoryPath = System.getProperty("java.io.tmpdir") + HttpPath.PATH_DELIMITER + UUID.randomUUID();
//        // 1. Download template file into local file system
//        File templateFile = loadTemplateDocument(client, template.getDocumentId(), tempDirectoryPath, "origin_file");
//        try {
//            // 2. Start replace value in file content
//            String extension = FilenameUtils.getExtension(templateFile.getName());
//            String name = StringUtils.isBlank(documentName) ? template.getName() : documentName;
//            String documentPath = CommonUtils.appendCharacters(parentDocumentPath, HttpPath.PATH_DELIMITER) + name + "." + extension;
//            Map<String, Object> originVariables = templateVariables;
//            List<String> languages = new ArrayList<>();
//            // 3. generate file content for document content
//            MultipartFile multipartFile = replaceVariable(templateFile, name, templateVariables, tempDirectoryPath, extension);
//            List<String> metadata = docPalTypeService.find(docPalType).getMetadata().stream().map(DocPalTypeMetadata::getMetadata).collect(Collectors.toList());
//            originVariables.keySet().removeIf(key -> !metadata.contains(key));
//            if (null == nuxeoUtils.fetchDocument(client, documentPath)) {
//                // 4. Do generate document (Check if the document exists, and if it exists then overwrite?)
//                List<MultipartFile> files = List.of(multipartFile);
//                return documentService.createDocument(documentPath, name, docPalType, originVariables, languages, files, client, null);
//            } else {
//                // 4. Override file content
//                DocumentRequestDTO updateRequest = new DocumentRequestDTO();
//                updateRequest.setIdOrPath(documentPath);
//                updateRequest.setName(name);
//                updateRequest.setProperties(originVariables);
//                documentService.replaceFileContent(JsonHelper.write(updateRequest), multipartFile);
//                documentService.updateDocument(updateRequest);
//                return serviceUtils.transform(nuxeoUtils.getSimpleDocument(client, documentPath));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Failed to generate file using document template, DocumentId:[{}] Variables:[{}]", template.getDocumentId(), templateVariables);
//        } finally {
//            FileUtils.deleteDirectory(new File(tempDirectoryPath));
//        }
//        log.error("Failed to generate file using document template, DocumentId:[{}] Variables:[{}]", template.getDocumentId(), templateVariables);
        return null;
    }

    private DocumentDTO doGenerateDocument(
            String documentName, String documentPath, String docpalType, Map<String, Object> properties, List<String> languages, MultipartFile multipartFile
    ) throws Exception {
//        // 检查文档是否存在, 如果存在则覆盖
//        if (null == nuxeoUtils.fetchDocument(client, documentPath)) {
//            List<MultipartFile> files = List.of(multipartFile);
//            return documentService.createDocument(documentPath, documentName, docpalType, properties, languages, files, client, null);
//        } else {
//            // Override file content
//            DocumentRequestDTO updateRequest = new DocumentRequestDTO();
//            updateRequest.setIdOrPath(documentPath);
//            updateRequest.setName(documentName);
//            updateRequest.setProperties(properties);
//            documentService.replaceFileContent(JsonHelper.write(updateRequest), multipartFile);
//            documentService.updateDocument(updateRequest);
//            return serviceUtils.transform(nuxeoUtils.getSimpleDocument(client, documentPath));
//        }
        return null;
    }

    /**
     * Download document to generated local file
     *
     * @param templateDocumentId template document id
     * @param tempDirectoryPath  temporary directory path
     * @param tempFileName       temporary file name
     * @return File the local file
     */
    protected File loadTemplateDocument(String templateDocumentId, String tempDirectoryPath, String tempFileName) {
        try {
//            Document document = nuxeoUtils.getSimpleDocument(client, templateDocumentId, SchemaName.DUBLINCORE, SchemaName.UID);
//            Blob blob = nuxeoUtils.documentGetBlob(client, document.getPath());
//            String extension = FilenameUtils.getExtension(blob.getFilename());
//            Files.createDirectories(Paths.get(tempDirectoryPath));
            String tmpDocFilePath = tempDirectoryPath +  "/origin_file.docx";
            InputStream is = new FileInputStream("");
            byte[] content = IOUtils.toByteArray(is);
            Path tempPath = Paths.get(tmpDocFilePath);
            Files.write(tempPath, content, StandardOpenOption.CREATE);
            return new File(tempPath.toUri());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
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
//        try {
//            byte[] bytes = extractOperation.replace(templateFile, extension, variables);
//            String filePath = tempDirectoryPath + HttpPath.PATH_DELIMITER + name + "." + extension;
//            Files.write(Paths.get(filePath), bytes, StandardOpenOption.CREATE);
//            File documentFile = new File(filePath);
//            String mimeType = new MimetypesFileTypeMap().getContentType(documentFile);
//            return CommonUtils.fileToMultipartFile(documentFile, mimeType);
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//        throw new ClientCustomException(ErrorCode.GLOBAL, "Failed to replace template variable");
        return null;
    }

}