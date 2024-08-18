package org.rabbit.core.template;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.NuxeoClient;
import org.rabbit.common.utils.HttpPath;
import org.rabbit.core.models.DocumentDTO;
import org.rabbit.entity.template.DocumentTemplate;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author Lyle
 */
public abstract class AbstractGenerateDocument {

    /**
     * the source of data
     */
    protected abstract String source();

    protected GenerateDocumentMode mode;

    AbstractGenerateDocument(GenerateDocumentMode mode) {
        this.mode = mode;
    }

    protected abstract Map<String, Object> getTemplateVariables(DocumentTemplate template, Map<String, Object> parameters);

    protected abstract File loadTemplateDocument(NuxeoClient client, String templateDocumentId, String tempDirectoryPath, String tempFileName);

    protected abstract MultipartFile replaceVariable(File templateFile, String fileName, Map<String, Object> templateVariables, String tempDirectoryPath, String fileExtension);

    protected abstract DocumentDTO uploadDocument(MultipartFile multipartFile, String documentPath, Map<String, Object> properties, String docPalType, NuxeoClient client);

    public DocumentDTO generate(NuxeoClient client, DocumentTemplate template, Map<String, Object> variables, String parentDocumentPath, String docPalType, String documentName) {
        Assert.state(null != template, "Document Template must be not null");
        Assert.notNull(parentDocumentPath, "Parent Document Path must be not null");
        Assert.notNull(docPalType, "document type cannot be null");
        final String tempDirectoryPath = System.getProperty("java.io.tmpdir") + HttpPath.PATH_DELIMITER + UUID.randomUUID();
        // Get replace value in file content
        Map<String, Object> templateVariables = getTemplateVariables(template, variables);
        try {
            // Download template file into local file system
            File templateFile = loadTemplateDocument(client, template.getDocumentId(), tempDirectoryPath, "origin_file");

            // generate file content for document content
            String extension = FilenameUtils.getExtension(templateFile.getName());
            String name = StringUtils.isBlank(documentName) ? template.getName() : documentName;
            MultipartFile multipartFile = replaceVariable(templateFile, name, templateVariables, tempDirectoryPath, extension);

            // generate document and then upload it
            Map<String, Object> originVariables = templateVariables;
            String documentPath = parentDocumentPath + "/" + name + "." + extension;
            DocumentDTO document = uploadDocument(multipartFile, documentPath, variables, docPalType, client);
            // set up document access control permissions

            return document;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.deleteDirectory(new File(tempDirectoryPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}