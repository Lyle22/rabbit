package org.rabbit.workflow.service;

import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.content.api.ContentItem;
import org.flowable.content.api.ContentItemQuery;
import org.flowable.content.api.ContentService;
import org.flowable.content.engine.ContentEngines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rabbit.workflow.models.FileDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class of response utils.
 *
 * @author nine rabbit
 */
@Service
public class ResponseUtils {

    /**
     * Attachment file dto.
     *
     * @param attachmentId the attachment id
     * @return the file dto
     * @throws IOException the io exception
     */
    public FileDTO attachment(String attachmentId) throws IOException {
        byte[] buffer;
        FileDTO attachment = null;
        ContentService contentService = ContentEngines.getDefaultContentEngine().getContentService();
        ContentItemQuery contentItemQuery = contentService.createContentItemQuery();
        contentItemQuery = contentItemQuery.id(attachmentId);
        ContentItem contentItem = contentItemQuery.singleResult();
        if (contentItem != null) {
            buffer = IOUtils.toByteArray(this.getContentItemData(contentItem.getId()));
            attachment = new FileDTO();
            attachment.setContentId(contentItem.getId());
            attachment.setName(contentItem.getName());
            attachment.setMimeType(contentItem.getMimeType());
            attachment.setSize(contentItem.getContentSize());
            attachment.setContent(buffer);
        }
        return attachment;
    }

    /**
     * Attachment big file dto.
     *
     * @param attachmentId the attachment id
     * @return the file dto
     * @throws IOException the io exception
     */
    public @Nullable FileDTO attachmentForBigFile(String attachmentId) {
        FileDTO attachment = null;
        ContentService contentService = ContentEngines.getDefaultContentEngine().getContentService();
        ContentItemQuery contentItemQuery = contentService.createContentItemQuery();
        contentItemQuery = contentItemQuery.id(attachmentId);
        ContentItem contentItem = contentItemQuery.singleResult();
        if (contentItem != null) {
            InputStream contentItemData = this.getContentItemData(contentItem.getId());
            attachment = new FileDTO();
            attachment.setContentId(contentItem.getId());
            attachment.setName(contentItem.getName());
            attachment.setMimeType(contentItem.getMimeType());
            attachment.setSize(contentItem.getContentSize());
            attachment.setInputStream(contentItemData);
        }
        return attachment;
    }

    /**
     * Attachments list.
     *
     * @param attachmentIds the attachment ids
     * @return the list
     * @throws IOException the io exception
     */
    public @NotNull List<FileDTO> attachments(@NotNull List<String> attachmentIds) throws IOException {
        List<FileDTO> files = new ArrayList<>();
        for (String attachmentId : attachmentIds) {
            if (StringUtils.isNotBlank(attachmentId)) {
                FileDTO fileDTO = attachment(attachmentId);
                if (null != fileDTO) {
                    files.add(fileDTO);
                }
            }
        }
        return files;
    }

    /**
     * File response entity response entity.
     *
     * @param file the file
     * @return the response entity
     */
    public @Nullable ResponseEntity<byte[]> fileResponseEntity(FileDTO file) {
        if (file == null) {
            return null;
        }
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(file.getName())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentLength(file.getSize());
        headers.setContentDisposition(disposition);
        return new ResponseEntity<>(file.getContent(), headers, HttpStatus.OK);
    }

    /**
     * File response entity response entity.
     *
     * @param contentItem the attachment
     * @return the response entity
     */
    public @Nullable ResponseEntity<byte[]> fileResponseEntity(ContentService contentService, ContentItem contentItem) throws IOException {
        if (contentItem == null) {
            return null;
        }
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(contentItem.getName())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentItem.getMimeType()));
        headers.setContentLength(contentItem.getContentSize());
        headers.setContentDisposition(disposition);
        InputStream inputStream = this.getContentItemData(contentItem.getId());
        return new ResponseEntity<>(
                IOUtils.toByteArray(inputStream), headers, HttpStatus.OK
        );
    }

    public InputStream getContentItemData(String contentItemId) {
        ContentService contentService = ContentEngines.getDefaultContentEngine().getContentService();
        InputStream inputStream = null;
        try {
            inputStream = contentService.getContentItemData(contentItemId);
        } catch (Exception e) {
            if (e instanceof FlowableObjectNotFoundException) {
                throw new FlowableObjectNotFoundException("718");
            }
        }
        return inputStream;
    }

    /**
     * File response entity response entity.
     *
     * @param buffer   the file buffer
     * @param fileName     the file name
     * @param mimeType the file media type
     * @return the response entity
     */
    public @Nullable ResponseEntity<byte[]> fileResponseEntity(byte[] buffer, String fileName, String mimeType) {
        if (buffer == null) {
            return null;
        }
        ContentDisposition disposition = ContentDisposition.inline().filename(fileName).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));
        headers.setContentLength(buffer.length);
        headers.setContentDisposition(disposition);
        return new ResponseEntity<>(buffer, headers, HttpStatus.OK);
    }

    /**
     * Convert response body to response entity for download
     *
     * @param responseBody the response body
     * @param fileName     the file name
     * @return the response entity
     * @throws IOException the io exception
     */
    public @Nullable ResponseEntity<byte[]> fileResponseEntity(
            ResponseBody responseBody, String fileName
    ) throws IOException {
        if (responseBody == null || StringUtils.isBlank(fileName)) {
            return null;
        }
        ContentDisposition disposition = ContentDisposition.inline().filename(fileName).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(Objects.requireNonNull(responseBody.contentType()).toString()));
        headers.setContentLength(responseBody.contentLength());
        headers.setContentDisposition(disposition);
        return new ResponseEntity<>(responseBody.byteStream().readAllBytes(), headers, HttpStatus.OK);
    }

    public @Nullable FileDTO file(String filename, InputStream is) throws IOException {
        if (StringUtils.isBlank(filename) || is == null) {
            return null;
        }
        byte[] buffer = IOUtils.toByteArray(is);
        FileDTO file = new FileDTO();
        file.setName(filename);
        file.setSize(buffer.length);
        file.setMimeType(mimeType(filename));
        file.setContent(buffer);
        return file;
    }

    public @NotNull String mimeType(String filename) {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(filename);
    }

}
