package org.rabbit.service.mail.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.rabbit.common.contains.StatusValue;
import org.rabbit.entity.system.EmailTemplate;
import org.rabbit.service.mail.models.TemplateRequestDTO;
import org.rabbit.service.mail.models.ThymeleafResolvablePattern;
import org.rabbit.service.system.dao.EmailLogMapper;
import org.rabbit.service.system.dao.EmailTemplateMapper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The type of email template service
 *
 * @author Lyle
 */
@Slf4j
@Service
public class EmailTemplateService {

    public static final String EMAIL_TEMPLATE_ID_PREFIX = "notification.";
    private final EmailTemplateMapper emailTemplateMapper;
    private final EmailLogMapper emailLogMapper;
    private final TemplateEngine templateEngine;

    public EmailTemplateService(EmailTemplateMapper emailTemplateMapper, EmailLogMapper emailLogMapper, TemplateEngine templateEngine) {
        this.emailTemplateMapper = emailTemplateMapper;
        this.emailLogMapper = emailLogMapper;
        this.templateEngine = templateEngine;
    }

    public List<EmailTemplate> findAll() {
        return emailTemplateMapper.selectList(null);
    }

    /**
     * Find by id
     *
     * @param id the id of email template
     */
    public EmailTemplate findById(String id) {
        return emailTemplateMapper.selectById(id);
    }

    /**
     * find all activated email template
     *
     * @return List<EmailTemplate> the list of activated email template
     */
    public List<EmailTemplate> findActive() {
        EmailTemplate template = new EmailTemplate();
        template.setStatus(StatusValue.ACTIVE);
        QueryWrapper<EmailTemplate> queryWrapper = new QueryWrapper<>();
        return emailTemplateMapper.selectList(queryWrapper.setEntity(template));
    }

    public String parse(TemplateRequestDTO template) {
        Map<String, Object> variables = template.getVariables();
        Context context = formatVariables(variables);
        String id = template.getId();
        String[] parts = id.split("\\.");
        Boolean subject = template.getSubject();
        if (subject != null && subject) {
            parts[0] = parts[0] + "_subject";
            id = String.join(".", parts);
        }
        return templateEngine.process(id, context);
    }

    public String parseText(String text, Map<String, Object> variables) {
        Context context = formatVariables(variables);
        String parseStr = templateEngine.process(ThymeleafResolvablePattern.TEXT + text, context);
        return parseStr.replace(ThymeleafResolvablePattern.TEXT, "");
    }

    public String parseHTML(String htmlBody, Map<String, Object> variables) {
        Context context = formatVariables(variables);
        return templateEngine.process(htmlBody, context);
    }

    @NotNull
    private static Context formatVariables(Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach((k, v) -> {
            if (v instanceof String) {
                try {
                    Instant instant = Instant.parse((String) v);
                    variables.put(k, instant);
                } catch (DateTimeException ignored) {

                }
            } else if (v instanceof Long && StringUtils.endsWithIgnoreCase(k, "date")) {
                Instant instant = (new Date((long) v)).toInstant();
                variables.put(k, instant);
            } else if (v instanceof Double && StringUtils.endsWithIgnoreCase(k, "date")) {
                Double time = ((Double) v) * 1000;
                Instant instant = (new Date(time.longValue())).toInstant();
                variables.put(k, instant);
            }
        });
        context.setVariables(variables);
        return context;
    }

}