package org.rabbit.service.email.resolver;

import org.apache.commons.lang3.StringUtils;
import org.rabbit.service.email.models.ThymeleafResolvablePattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Collections;
import java.util.Map;

/**
 * TextTemplateResolver
 * @author nine rabbit
 */
@Component
public class TextTemplateResolver extends StringTemplateResolver {

    @Autowired
    public TextTemplateResolver() {
        this.setTemplateMode(TemplateMode.TEXT);
        this.setResolvablePatterns(Collections.singleton(ThymeleafResolvablePattern.TEXT));
        this.setCacheable(false);
    }

    @Override
    protected ITemplateResource computeTemplateResource(
            IEngineConfiguration engineConfiguration, String ownerTemplate, String templateBody,
            Map<String, Object> templateResolutionAttributes
    ) {
        if (StringUtils.isBlank(templateBody)) {
            throw new IllegalArgumentException("Text can not null!");
        }
        return super.computeTemplateResource(
                engineConfiguration, ownerTemplate, templateBody, templateResolutionAttributes
        );
    }
}
