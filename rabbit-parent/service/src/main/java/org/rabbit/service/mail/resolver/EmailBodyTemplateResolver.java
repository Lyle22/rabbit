package org.rabbit.service.mail.resolver;

import org.rabbit.entity.system.EmailTemplate;
import org.rabbit.service.mail.models.ThymeleafResolvablePattern;
import org.rabbit.service.system.dao.EmailTemplateMapper;
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
 * @author Lyle
 */
@Component
public class EmailBodyTemplateResolver extends StringTemplateResolver {
    private static final Logger logger = LoggerFactory.getLogger(EmailBodyTemplateResolver.class);

    private final EmailTemplateMapper emailTemplateMapper;

    @Autowired
    public EmailBodyTemplateResolver(EmailTemplateMapper emailTemplateMapper) {
        this.emailTemplateMapper = emailTemplateMapper;
        this.setTemplateMode(TemplateMode.HTML);
        this.setResolvablePatterns(Collections.singleton(ThymeleafResolvablePattern.EMAIL_BODY));
        this.setCacheable(false);
    }

    @Override
    protected ITemplateResource computeTemplateResource(
            IEngineConfiguration engineConfiguration, String ownerTemplate, String template,
            Map<String, Object> templateResolutionAttributes
    ) {
        EmailTemplate docPalEmailTemplate = emailTemplateMapper.selectById(template);
        if (docPalEmailTemplate == null) {
            throw new IllegalArgumentException(String.format("Email template '%s' not found!", template));
        }
        return super.computeTemplateResource(
                engineConfiguration, ownerTemplate, docPalEmailTemplate.getBody(), templateResolutionAttributes
        );
    }
}
