package org.rabbit.service.mail.resolver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
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
            IEngineConfiguration engineConfiguration, String ownerTemplate, String template,
            Map<String, Object> templateResolutionAttributes
    ) {
        String _template = RegExUtils.replaceFirst(template, "_subject", "");
        EmailTemplate docPalEmailTemplate = emailTemplateMapper.selectById(_template);
        if (docPalEmailTemplate == null) {
            throw new IllegalArgumentException(String.format("Email template '%s' not found!", _template));
        }
        return super.computeTemplateResource(
                engineConfiguration, ownerTemplate, docPalEmailTemplate.getSubject(), templateResolutionAttributes
        );
    }
}
