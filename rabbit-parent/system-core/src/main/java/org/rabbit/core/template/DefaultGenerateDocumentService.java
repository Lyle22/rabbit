package org.rabbit.core.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.rabbit.common.exception.ClientCustomException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.HttpPath;
import org.rabbit.core.core.NuxeoUtils;
import org.rabbit.core.document.DocumentService;
import org.rabbit.core.models.DocumentDTO;
import org.rabbit.core.models.DocumentRequestDTO;
import org.rabbit.core.models.GenerateDocumentRequestDTO;
import org.rabbit.entity.template.DocumentTemplate;
import org.rabbit.service.template.impl.DocumentTemplateService;
import org.rabbit.service.template.impl.ExtractOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class of default implement service for generate document
 * <p>Use templates to generate new documents according to different creation modes</p>
 *
 * @author nine rabbit
 */
@Slf4j
@Service
public class DefaultGenerateDocumentService extends AbstractGenerateDocument implements IGenerateDocumentService {

    @Autowired
    private NuxeoUtils nuxeoUtils;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ExtractOperation extractOperation;
    @Autowired
    private DocumentTemplateService documentTemplateService;

    public DefaultGenerateDocumentService(GenerateDocumentMode generateMode) {
        super(generateMode);
    }

    @Override
    protected String source() {
        return "TEMPLATE";
    }

    @Override
    public DocumentDTO generate(NuxeoClient client, GenerateDocumentRequestDTO requestDTO) {

        return null;
    }

    @Override
    protected Map<String, Object> getTemplateVariables(DocumentTemplate template, Map<String, Object> parameters) {
        log.debug("Does not need to convert template variables");
        return parameters;
    }

    @Override
    protected File loadTemplateDocument(NuxeoClient client, String templateDocumentId, String tempDirectoryPath, String tempFileName) {
        try {
            Document document = nuxeoUtils.getSimpleDocument(client, templateDocumentId);
//            Blob blob = nuxeoUtils.documentGetBlob(client, document.getPath());
            String extension = FilenameUtils.getExtension("nuxeo");
            Files.createDirectories(Paths.get(tempDirectoryPath));
            String tmpDocFilePath = tempDirectoryPath + HttpPath.PATH_DELIMITER + "origin_file." + extension;
//            InputStream is = "blob.getStream()".getBytes(StandardCharsets.UTF_8);
            byte[] content = "blob.getStream()".getBytes(StandardCharsets.UTF_8);
            Path tempPath = Paths.get(tmpDocFilePath);
            Files.write(tempPath, content, StandardOpenOption.CREATE);
            return new File(tempPath.toUri());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new ClientCustomException(ErrorCode.GLOBAL, "template document does not exist");
    }

    @Override
    protected MultipartFile replaceVariable(File templateFile, String fileName, Map<String, Object> templateVariables, String tempDirectoryPath, String fileExtension) {
        try {
//            byte[] bytes = extractOperation.replace(templateFile, fileExtension, templateVariables);
            byte[] bytes =String.valueOf("ddddddddddddddd").getBytes();
            String filePath = tempDirectoryPath + HttpPath.PATH_DELIMITER + fileName + "." + fileExtension;
            Files.write(Paths.get(filePath), bytes, StandardOpenOption.CREATE);
            File documentFile = new File(filePath);
            String mimeType = new MimetypesFileTypeMap().getContentType(documentFile);
//            return CommonUtils.fileToMultipartFile(documentFile, mimeType);
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new ClientCustomException(ErrorCode.GLOBAL, "Failed to replace template variable");
    }

    @Override
    protected DocumentDTO uploadDocument(MultipartFile multipartFile, String documentPath, Map<String, Object> properties, String documentType, NuxeoClient client) {
//        List<String> metadata = documentTypeService.find(documentType).getMetadata().stream().map(documentTypeMetadata::getMetadata).collect(Collectors.toList());
//        properties.keySet().removeIf(key -> !metadata.contains(key));
        try {
            // 检查文档是否存在, 如果存在则覆盖
            String documentName = documentPath.substring(documentPath.lastIndexOf(HttpPath.PATH_DELIMITER) + 1);
            if (null == nuxeoUtils.fetchDocument(client, documentPath)) {
                List<MultipartFile> files = new ArrayList<>();
                files.add(multipartFile);
//                return documentService.createDocument(documentPath, documentName, documentType, properties, new ArrayList<>(), files, client, null);
            } else {
                // Override file content
                DocumentRequestDTO updateRequest = new DocumentRequestDTO();
                updateRequest.setIdOrPath(documentPath);
                updateRequest.setName(documentName);
                updateRequest.setProperties(properties);
//                documentService.replaceFileContent(JsonHelper.write(updateRequest), multipartFile);
//                documentService.updateDocument(updateRequest);
//                return serviceUtils.transform(nuxeoUtils.getSimpleDocument(client, documentPath, SchemaName.UID, SchemaName.DUBLINCORE));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Upload document was not successful, documentPath={}", documentPath);
        }
        throw new ClientCustomException(ErrorCode.GLOBAL, "Upload document was not successful");
    }

}