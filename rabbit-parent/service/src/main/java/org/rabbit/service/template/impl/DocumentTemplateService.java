package org.rabbit.service.template.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.exception.CustomException;
import org.rabbit.common.exception.ClientCustomException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.entity.template.DocumentTemplate;
import org.rabbit.entity.template.DocumentTemplateRequestDTO;
import org.rabbit.entity.template.DocumentTemplateResponseDTO;
import org.rabbit.entity.user.User;
import org.rabbit.service.template.dao.DocumentTemplateMapper;
import org.rabbit.service.user.ILoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Document Template Service
 *
 * @author nine rabbit
 **/
@Slf4j
@Service
public class DocumentTemplateService extends ServiceImpl<DocumentTemplateMapper, DocumentTemplate> {

    public static final Set<String> FILE_TYPES = Stream.of("Word", "Excel", "PPT").collect(Collectors.toSet());

    /**
     * Default setting root path of all template documents
     */
    public static final String DOC_TEMPLATE_ROOT_PATH = "/default-domain/templates";
    public static final String DOC_TEMPLATE_ROOT_NAME = "Templates";

    @Autowired
    private ILoginUserService userService;

    private void preCheckArgs(DocumentTemplate documentTemplate) {
        Assert.notNull(documentTemplate.getName(), "name must be not null");
        if (!FILE_TYPES.contains(documentTemplate.getFileType())) {
            throw new ClientCustomException(ErrorCode.GLOBAL, String.format("FileType include of %s", FILE_TYPES));
        }
//        if (this.isDuplicateName(documentTemplate.getName(), documentTemplate.getId())) {
//            throw new ClientCustomException(ErrorCode.GLOBAL, "The template name already exists");
//        }
    }

    public DocumentTemplateResponseDTO create(DocumentTemplateRequestDTO requestDTO) throws Exception {
        DocumentTemplate entity = requestDTO.to();
        // Pre-check parameters
        this.preCheckArgs(entity);
        if (StringUtils.isBlank(entity.getId())) {
            entity.setId(UUID.randomUUID().toString());
        }
        User loggedInUser = userService.getCurrentUser();
        entity.setCreatedBy(loggedInUser.getId());
        entity.setModifiedBy(loggedInUser.getId());
        entity.setCreatedDate(Instant.now());
        entity.setModifiedDate(Instant.now());
        baseMapper.insert(entity);
        // If file is exists
        if (null != requestDTO.getFile()) {
            requestDTO.setId(entity.getId());
            this.upload(requestDTO);
        }
        return DocumentTemplateResponseDTO.builder().build().assignValue(baseMapper.selectById(entity.getId()));
    }

    public DocumentTemplateResponseDTO upload(DocumentTemplateRequestDTO requestDTO) throws Exception {
        if (Objects.isNull(requestDTO.getFile())) {
            throw new CustomException("The uploaded file cannot be empty");
        }
        return null;
    }

}
