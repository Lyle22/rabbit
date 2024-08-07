package org.rabbit.core.template;

import com.wclsolution.docpal.api.constants.nuxeo.HttpPath;
import com.wclsolution.docpal.api.constants.nuxeo.SchemaName;
import com.wclsolution.docpal.api.dbmodel.docpal.DocPalTypeMetadata;
import com.wclsolution.docpal.api.dbmodel.docpal.DocumentTemplate;
import com.wclsolution.docpal.api.exception.DocPalCustomException;
import com.wclsolution.docpal.api.security.ErrorCode;
import com.wclsolution.docpal.api.services.docpal.DocPalTypeService;
import com.wclsolution.docpal.api.services.docpal.DocumentTemplateService;
import com.wclsolution.docpal.api.services.docpal.ExtractOperation;
import com.wclsolution.docpal.api.services.nuxeo.DocumentService;
import com.wclsolution.docpal.api.utils.CommonUtils;
import com.wclsolution.docpal.api.utils.JsonHelper;
import com.wclsolution.docpal.api.utils.nuxeo.NuxeoUtils;
import com.wclsolution.docpal.api.utils.nuxeo.ServiceUtils;
import com.wclsolution.docpal.api.viewmodels.request.DocumentRequestDTO;
import com.wclsolution.docpal.api.viewmodels.request.GenerateDocumentRequestDTO;
import com.wclsolution.docpal.api.viewmodels.response.DocumentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.blob.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The class of default implement service for generate document
 * <p>Use templates to generate new documents according to different creation modes</p>
 *
 * @author Lyle
 */
@Slf4j
@Service
public class DefaultGenerateDocumentService extends AbstractGenerateDocument implements IGenerateDocumentService {

    @Autowired
    private NuxeoUtils nuxeoUtils;
    @Autowired
    private ServiceUtils serviceUtils;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ExtractOperation extractOperation;
    @Autowired
    private DocPalTypeService docPalTypeService;
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

    @Override
    protected MultipartFile replaceVariable(File templateFile, String fileName, Map<String, Object> templateVariables, String tempDirectoryPath, String fileExtension) {
        try {
            byte[] bytes = extractOperation.replace(templateFile, fileExtension, templateVariables);
            String filePath = tempDirectoryPath + HttpPath.PATH_DELIMITER + fileName + "." + fileExtension;
            Files.write(Paths.get(filePath), bytes, StandardOpenOption.CREATE);
            File documentFile = new File(filePath);
            String mimeType = new MimetypesFileTypeMap().getContentType(documentFile);
            return CommonUtils.fileToMultipartFile(documentFile, mimeType);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new DocPalCustomException(ErrorCode.GLOBAL, "Failed to replace template variable");
    }

    @Override
    protected DocumentDTO uploadDocument(MultipartFile multipartFile, String documentPath, Map<String, Object> properties, String docPalType, NuxeoClient client) {
        List<String> metadata = docPalTypeService.find(docPalType).getMetadata().stream().map(DocPalTypeMetadata::getMetadata).collect(Collectors.toList());
        properties.keySet().removeIf(key -> !metadata.contains(key));
        try {
            // 检查文档是否存在, 如果存在则覆盖
            String documentName = documentPath.substring(documentPath.lastIndexOf(HttpPath.PATH_DELIMITER) + 1);
            if (null == nuxeoUtils.fetchDocument(client, documentPath)) {
                List<MultipartFile> files = List.of(multipartFile);
                return documentService.createDocument(documentPath, documentName, docPalType, properties, new ArrayList<>(), files, client, null);
            } else {
                // Override file content
                DocumentRequestDTO updateRequest = new DocumentRequestDTO();
                updateRequest.setIdOrPath(documentPath);
                updateRequest.setName(documentName);
                updateRequest.setProperties(properties);
                documentService.replaceFileContent(JsonHelper.write(updateRequest), multipartFile);
                documentService.updateDocument(updateRequest);
                return serviceUtils.transform(nuxeoUtils.getSimpleDocument(client, documentPath, SchemaName.UID, SchemaName.DUBLINCORE));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Upload document was not successful, documentPath={}", documentPath);
        }
        throw new DocPalCustomException(ErrorCode.GLOBAL, "Upload document was not successful");
    }

}