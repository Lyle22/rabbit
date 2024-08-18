package org.rabbit.core.core;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Repository;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.spi.NuxeoClientRemoteException;
import org.rabbit.common.utils.CommonUtils;
import org.rabbit.common.utils.HttpPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Nuxeo Utilities
 */
@Slf4j
@Component
public class NuxeoUtils {

    private static final String FILE_CONTENT = "file:content";
    public static String nuxeoServerURL;
    public static String nuxeoUserName;
    public static String nuxeoPassword;
    public static Integer writeTimeout;

    @Value("${nuxeo.display.document.type}")
    private String displayDocType;

    @Value("${nuxeo.upload.writeTimeout}")
    private void setWriteTimeout(Integer writeTimeout) {
        NuxeoUtils.writeTimeout = writeTimeout;
    }

    @Value("${nuxeo.app.url:http://localhost:8080}")
    private void setNuxeoServerURL(String nuxeoServerURL) {
        NuxeoUtils.nuxeoServerURL = nuxeoServerURL;
    }

    @Value("${nuxeo.public.username:Administrators}")
    private void setNuxeoUserName(String nuxeoUserName) {
        NuxeoUtils.nuxeoUserName = nuxeoUserName;
    }

    @Value("${nuxeo.public.password:password}")
    private void setNuxeoPassword(String nuxeoPassword) {
        NuxeoUtils.nuxeoPassword = nuxeoPassword;
    }

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private NuxeoProperties nuxeoProperties;

    /**
     * Gets nuxeo client. Used for public endpoints or scheduler jobs.
     *
     * @return the nuxeo client
     */
    public static NuxeoClient getNuxeoClient() {
        return new NuxeoClient.Builder()
                .url(nuxeoServerURL)
                .authentication(nuxeoUserName, nuxeoPassword)
                .schemas("*")
                .timeout(writeTimeout)
                .transactionTimeout(writeTimeout)
                .connect();
    }

    public String getSupportUser() {
        return nuxeoProperties.getSupportUser();
    }

    public List<String> getDisplayDocType() {
        if (StringUtils.isBlank(displayDocType)) {
            return new ArrayList<>();
        }
        return Arrays.asList(displayDocType.split(","));
    }

    /**
     * Create keyword name
     * <quot>
     * prefix:fieldName
     * </quot>
     *
     * @param prefix    the prefix
     * @param fieldName the field name
     * @return the keyword naem
     */
    public @NonNull String keywordName(String prefix, @NonNull String fieldName) {
        return keywordName(prefix, fieldName, null);
    }

    /**
     * Create keyword name
     * <quot>
     * prefix:fieldName/complexFiledName
     * </quot>
     *
     * @param prefix           the prefix
     * @param fieldName        the field name
     * @param complexFieldName the complex field name
     * @return the keyword name
     */
    public @NonNull String keywordName(
            String prefix, @NonNull String fieldName, String complexFieldName
    ) {
        if (StringUtils.isBlank(prefix)) {
            if (StringUtils.isBlank(complexFieldName)) {
                return String.format("%s", fieldName);
            } else {
                return String.format("%s/%s", fieldName, complexFieldName);
            }
        } else if (StringUtils.isBlank(complexFieldName)) {
            return String.format("%s:%s", prefix, fieldName);
        } else {
            return String.format("%s:%s/%s", prefix, fieldName, complexFieldName);
        }
    }

    /**
     * Get Nuxeo repository with all enrichers
     *
     * @param client the Nuxeo client
     * @return the repository
     */
    public Repository repository(@NonNull NuxeoClient client) {
        return client.repository().schemas("*");
    }

    public void setTimeOutAndChunkSize(NuxeoClient client, BatchUpload upload, Long fileSize) {
        client.timeout(writeTimeout);
        if (null != upload && 0 != nuxeoProperties.getUploadChunkSize()) {
            Integer chunksize = 1024 * 1024 * nuxeoProperties.getUploadChunkSize();
            if (chunksize > Integer.MAX_VALUE) {
                chunksize = Integer.MAX_VALUE;
            }
            // 如果fileSize=0 upload操作会报错 ,如果fileSize比chunkSize小 也没必要切片上传了
            if (fileSize > chunksize) {
                upload.chunkSize(chunksize);
            }
        }
    }

    /**
     * get the uuid of idOrPath
     *
     * <p>If idOrPath then endow uuid of ducument root </p>
     *
     * @param idOrPath path or uuid of ducument
     * @param client   nuxeo client
     * @return UUID
     */
    public String getUUID(String idOrPath, NuxeoClient client) {
        // Prepare parameters and get UUID
        if (StringUtils.isBlank(idOrPath)) {
            Document root = client.repository().fetchDocumentRoot();
            idOrPath = root.getUid();
        }
        String _idOrPath = CommonUtils.removeConsecutive(idOrPath, HttpPath.PATH_DELIMITER_CHAR).toString();
        if (!CommonUtils.isUUID(_idOrPath)) {
            Document document = client.repository()
                    .fetchDocumentByPath(StringUtils.startsWith(_idOrPath, HttpPath.PATH_DELIMITER) ? _idOrPath :
                            HttpPath.PATH_DELIMITER + _idOrPath);
            _idOrPath = document.getUid();
        }
        return _idOrPath;
    }

    public Document getSimpleDocument(NuxeoClient client, String idOrPath, String... properties) {
        Repository repository = client.repository();
        if (properties.length > 0) {
            for (String schemas : properties) {
                repository.schemas(true, schemas);
            }
        }
        return getSimpleDocument(repository, idOrPath);
    }

    public Document getSimpleDocument(Repository repository, String idOrPath) {
        repository.schemas(true, "global");
        String _idOrPath = CommonUtils.removeConsecutive(idOrPath, HttpPath.PATH_DELIMITER_CHAR).toString();
        if (CommonUtils.isUUID(_idOrPath)) {
            return repository.fetchDocumentById(_idOrPath);
        } else if (StringUtils.isBlank(idOrPath)) {
            return repository.fetchDocumentRoot();
        } else {
            return repository.fetchDocumentByPath(
                    StringUtils.startsWith(idOrPath, HttpPath.PATH_DELIMITER) ? idOrPath
                            : HttpPath.PATH_DELIMITER + idOrPath);
        }
    }

    public String getNuxeoIdByRequestUserId(String requestUserId, Set<String> nuxeoUserIds) {
        AtomicReference<String> nuxeoUserId = new AtomicReference<>(requestUserId);
        nuxeoUserIds.stream().forEach(item -> {
            if (requestUserId.equalsIgnoreCase(item)) {
                nuxeoUserId.set(item);
            }
        });
        return nuxeoUserId.get();
    }

    /**
     * Get document.
     *
     * @param client   the Nuxeo client
     * @param idOrPath the document id or path
     * @return the document
     */
    public Document getDocument( NuxeoClient client, String idOrPath) {
        String realIdOrPath = CommonUtils.removeConsecutive(idOrPath, HttpPath.PATH_DELIMITER_CHAR).toString();
        try {
            if (CommonUtils.isUUID(realIdOrPath)) {
                return repository(client).fetchDocumentById(realIdOrPath);
            } else if (StringUtils.isBlank(idOrPath)) {
                return repository(client).fetchDocumentRoot();
            } else {
                return repository(client).fetchDocumentByPath(
                        StringUtils.startsWith(idOrPath, HttpPath.PATH_DELIMITER) ? idOrPath : HttpPath.PATH_DELIMITER + idOrPath);
            }
        } catch (NuxeoClientRemoteException e) {
            if (404 == e.getStatus()) {
                throw new RuntimeException("601");
            }
            if (403 == e.getStatus()) {
                throw new RuntimeException("605");
            }
        } catch (Exception ae) {
            throw new RuntimeException("602");
        }
        throw new RuntimeException("602");
    }

    /**
     * Returns the document information if error return null value
     *
     * @param client   the client
     * @param idOrPath the id or path
     * @return Document
     */
    public Document fetchDocument( NuxeoClient client, String idOrPath) {
        String realIdOrPath = CommonUtils.removeConsecutive(idOrPath, HttpPath.PATH_DELIMITER_CHAR).toString();
        try {
            if (CommonUtils.isUUID(realIdOrPath)) {
                return repository(client).fetchDocumentById(realIdOrPath);
            } else if (StringUtils.isBlank(idOrPath)) {
                return repository(client).fetchDocumentRoot();
            } else {
                return repository(client).fetchDocumentByPath(
                        StringUtils.startsWith(idOrPath, HttpPath.PATH_DELIMITER) ? idOrPath : HttpPath.PATH_DELIMITER + idOrPath);
            }
        } catch (NuxeoClientRemoteException e) {
            if (404 == e.getStatus() || 403 == e.getStatus()) {
                return null;
            }
        } catch (Exception ae) {
            throw new RuntimeException("602");
        }
        return null;
    }

    /**
     * If exists content return StreamBlob，else null
     *
     * @param document the document
     * @return the stream blob, else null
     */
    public StreamBlob getFileContentBlob(Document document, NuxeoClient client) {
        Map<String, String> fileContent = document.getPropertyValue(FILE_CONTENT);
        if (null == fileContent) {
            return null;
        }
        return repository(client).streamBlobById(document.getId(), FILE_CONTENT);
    }

}