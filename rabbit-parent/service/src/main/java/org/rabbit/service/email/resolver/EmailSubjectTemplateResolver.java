package org.rabbit.service.email.resolver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.rabbit.entity.template.EmailTemplate;
import org.rabbit.service.email.models.ThymeleafResolvablePattern;
import org.rabbit.service.template.dao.EmailTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Collections;
import java.util.Map;

/**
 * @author nine rabbit
 */
@Component
@Slf4j
public class EmailSubjectTemplateResolver extends StringTemplateResolver {
    private static final Logger logger = LoggerFactory.getLogger(EmailSubjectTemplateResolver.class);

    private final EmailTemplateMapper emailTemplateMapper;

    @Autowired
    public EmailSubjectTemplateResolver(EmailTemplateMapper emailTemplateMapper) {
        this.emailTemplateMapper = emailTemplateMapper;
        this.setTemplateMode(TemplateMode.HTML);
        this.setResolvablePatterns(Collections.singleton(ThymeleafResolvablePattern.EMAIL_SUBJECT));
        this.setCacheable(false);
    }

    @Override
    protected ITemplateResource computeTemplateResource(
            IEngineConfiguration engineConfiguration, String ownerTemplate, String templateId,
            Map<String, Object> templateResolutionAttributes
    ) {
        String id = RegExUtils.replaceFirst(templateId, "_subject", "");
        EmailTemplate emailTemplate = emailTemplateMapper.selectById(id);
        if (emailTemplate == null) {
            throw new IllegalArgumentException(String.format("Email template '%s' not found!", templateId));
        }
        return super.computeTemplateResource(
                engineConfiguration, ownerTemplate, emailTemplate.getSubject(), templateResolutionAttributes
        );
    }
}
