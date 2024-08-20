package org.rabbit.service.email.resolver;

import org.apache.commons.lang3.StringUtils;
import org.rabbit.service.email.models.ThymeleafResolvablePattern;
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
public class HtmlTemplateResolver extends StringTemplateResolver {

    private static final Logger logger = LoggerFactory.getLogger(HtmlTemplateResolver.class);

    @Autowired
    public HtmlTemplateResolver() {
        this.setTemplateMode(TemplateMode.HTML);
        this.setResolvablePatterns(Collections.singleton(ThymeleafResolvablePattern.HTML));
        this.setCacheable(false);
    }

    @Override
    protected ITemplateResource computeTemplateResource(
            IEngineConfiguration engineConfiguration, String ownerTemplate, String templateBody,
            Map<String, Object> templateResolutionAttributes
    ) {
        if (StringUtils.isBlank(templateBody)) {
            throw new IllegalArgumentException("HTML body can not null!");
        }
        return super.computeTemplateResource(
                engineConfiguration, ownerTemplate, templateBody, templateResolutionAttributes
        );
    }
}
