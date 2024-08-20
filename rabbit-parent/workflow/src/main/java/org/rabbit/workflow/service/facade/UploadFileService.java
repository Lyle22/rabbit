package org.rabbit.workflow.service.facade;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.contains.Result;
import org.rabbit.workflow.constants.FlowableConstants;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author nine rabbit
 */
@Slf4j
@Service
public class UploadFileService implements IFunctionFacade {

    private static final String DOCUMENT_NAME = "documentName";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String PARENT_PATH = "parentPath";
    private static final String CONTENT_ID = "contentId";

//    @Autowired
//    private NuxeoUtils nuxeoUtils;
//    @Autowired
//    private DocumentService documentService;

    public void generate(String documentName, String parentDocPath, String docType, String fileContentIds, Map<String, Object> docProperties) {
//        if (StringUtils.isBlank(docType)) {
//            docType = DocumentType.FILE;
//        }
//        String[] contentIds = fileContentIds.split(",");
//        NuxeoClient client = AuthenticationUtils.getNuxeoClient();
//        Document parentDoc = nuxeoUtils.fetchDocumentByPath(client, parentDocPath);
//        if (parentDoc == null) {
//            throw new ClientCustomException(ErrorCode.WORKFLOW, "Parent document does not exist.");
//        }
//        boolean replaceName = StringUtils.isBlank(documentName);
//        ContentService contentService = ContentEngines.getDefaultContentEngine().getContentService();
//        for (String contentItemId : contentIds) {
//            InputStream fis = contentService.getContentItemData(contentItemId);
//            ContentItem content = contentService.createContentItemQuery().id(contentItemId).singleResult();
//            if (replaceName) {
//                documentName = content.getName();
//            }
//            String uniqueDocName = checkDuplicateName(parentDocPath, documentName, 0);
//            // 1. Build multipart file of request parameters
//            List<MultipartBody.Part> parts = new ArrayList<>();
//            try {
//                MultipartBody.Part part = MultipartBody.Part.createFormData("files", uniqueDocName,
//                        RequestBody.create(okhttp3.MediaType.parse(content.getMimeType()), fis.readAllBytes())
//                );
//                parts.add(part);
//            } catch (IOException e) {
//                log.error("Unable to get file content for upload file to create document [ {} ]", parentDocPath + HttpPath.PATH_DELIMITER + uniqueDocName);
//                e.printStackTrace();
//            }
//            if (parts.isEmpty()) {
//                throw new ClientCustomException(ErrorCode.WORKFLOW, "Failed to read file content for create document");
//            }
//            // 2. Build document fields of request parameters
//            String idOrPath = String.format("%s/%s", parentDocPath, uniqueDocName);
//            docProperties.put(SchemaFieldName.DC_TITLE, uniqueDocName);
//            DocumentRequestDTO documentRequestDTO = DocumentRequestDTO.builder()
//                    .idOrPath(idOrPath)
//                    .name(uniqueDocName)
//                    .type(docType)
//                    .properties(docProperties)
//                    .creator(fileContentIds)
//                    .build();
//            try {
//                DocumentDTO documentDTO = documentService.createDocument(documentRequestDTO, parts);
//                if (log.isDebugEnabled()) {
//                    log.info("Create document was successful through upload multipart files. Name::[ {} ] , Path::[ {} ]",
//                            documentDTO.getName(), documentDTO.getPath());
//                }
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                throw new ClientCustomException(ErrorCode.WORKFLOW, "Failed to create document");
//            }
//        }
    }
//
//    public String checkDuplicateName(String parentPath, String docName, int index) {
//        if (nuxeoUtils.checkDuplicateName(parentPath, docName)) {
//            index++;
//            String documentTitle = docName + " " + index;
//            for (int i = 1; i < Integer.MAX_VALUE; i++) {
//                String[] nameAndType = docName.split("\\.");
//                String extend = nameAndType[0] + " (" + i + ")";
//                if (nameAndType.length > 1) {
//                    extend = extend + "." + nameAndType[1];
//                }
//                if (!nuxeoUtils.checkDuplicateName(parentPath, extend)) {
//                    documentTitle = extend;
//                    break;
//                }
//            }
//            return documentTitle;
//        }
//        return docName;
//    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取所有参数名
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            // 获取参数值
            String[] paramValues = request.getParameterValues(paramName);
            // 输出参数名称和值
            out.println("<p>Name: " + paramName + "</p>");
            for (String value : paramValues) {
                out.println("<p>Value: " + value + "</p>");
            }
            // 检查是否为文件上传
            if (request.getPart(paramName) != null) {
                out.println("<p>Type: File Upload</p>");
                // 处理文件上传
                Part filePart = request.getPart(paramName);
                out.println("<p>File Size: " + filePart.getSize() + "</p>");
            } else {
                out.println("<p>Type: Text Input</p>");
            }
        }
    }

    @Override
    public Result<String> validate(Map<String, String> reqParam, String funcPoint) {
        // 校验必填参数是否存在?
        if (StringUtils.isBlank(reqParam.get(PARENT_PATH))) {
            return Result.error("missing parent path");
        } else {
//            Document document = nuxeoUtils.fetchDocument(AuthenticationUtils.getNuxeoAdminClient(), reqParam.get(PARENT_PATH));
//            if (null == document) {
//                return Result.error("parent document does not exist");
//            }
        }
        if (StringUtils.isBlank(reqParam.get(DOCUMENT_NAME))) {
            return Result.error("missing document name");
        }
        if (StringUtils.isBlank(reqParam.get(DOCUMENT_TYPE))) {
            return Result.error("missing document type");
        }
        if (StringUtils.isBlank(reqParam.get(CONTENT_ID))) {
            return Result.error("missing setup contentId");
        }
        String properties = reqParam.get(FlowableConstants.PROPERTIES);
        if (StringUtils.isNotBlank(properties)) {
            Map<String, Object> propertiesMap = new Gson().fromJson(properties, Map.class);
//            String verifyResult = docPalTypeService.verifyMetadata(reqParam.get(DOCUMENT_TYPE), new ArrayList<>(propertiesMap.keySet()));
//            if (StringUtils.isNotBlank(verifyResult)) {
//                return Result.error(verifyResult);
//            }
        }
        return Result.ok(Boolean.TRUE.toString());
    }

}
