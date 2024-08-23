package org.rabbit.workflow.service.delegate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.jetbrains.annotations.NotNull;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.workflow.constants.DocumentConstants;
import org.rabbit.workflow.service.facade.UploadFileService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Upload File Delegate
 *
 * @author nine rabbit
 */
@Service
@Slf4j
public class UploadFileDelegate extends AbstractJavaDelegate {

    private Expression parentPath;
    private Expression documentName;
    private Expression documentType;
    private Expression properties;
    private Expression contentId;

    private final UploadFileService uploadFileService;

    public UploadFileDelegate(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @Override
    public void execute(@NotNull DelegateExecution execution) {
        // 1. Get request parameters
        String parentDocPath = extractValue(execution, DocumentConstants.PARENT_PATH);
        String docType = extractValue(execution, DocumentConstants.DOCUMENT_TYPE);
        if (StringUtils.isBlank(docType)) {
            docType = "File";
        }
        String docName = extractValue(execution, DocumentConstants.DOCUMENT_NAME);
        String fileContentIds = extractValue(execution, DocumentConstants.CONTENT_ID);
        String propertiesValue = extractValue(execution, DocumentConstants.PROPERTIES);
        Map<String, Object> docProperties = new HashMap<>();
        if (StringUtils.isNotBlank(propertiesValue)) {
            docProperties = JsonHelper.read(propertiesValue, Map.class);
        }
        // 2. Invoke service method for upload document
        uploadFileService.generate(docName, parentDocPath, docType, fileContentIds, docProperties);
    }

}
