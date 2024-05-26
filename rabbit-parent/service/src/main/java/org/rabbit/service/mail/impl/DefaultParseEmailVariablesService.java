package org.rabbit.service.mail.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.entity.system.EmailTemplate;
import org.rabbit.service.mail.models.MailSendRequest;
import org.rabbit.service.mail.models.SendEmailParam;
import org.rabbit.service.mail.models.TemplateRequestDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lyle
 */
@Slf4j
@Service
public class DefaultParseEmailVariablesService {

    /**
     * Replace tag for body text in the email layout
     * [Agreed configuration]
     */
    protected static final String REPLACE_TAG = "[[emailContent]]";

    private final EmailTemplateService emailTemplateService;

    public DefaultParseEmailVariablesService(EmailTemplateService emailTemplateService) {
        this.emailTemplateService = emailTemplateService;
    }

    public SendEmailParam processBeforeSendEmail(MailSendRequest request) {
        SendEmailParam emailParam = new SendEmailParam();
        emailParam.setFromEmail(request.getFromEmail());
        emailParam.setTos(request.getTos());
        emailParam.setCcs(request.getCcs());
        emailParam.setBcc(request.getBcc());
        emailParam.setFiles(request.getFiles());
        // 判断邮件的内容类型
        String bodyText = request.getText();
        if (isHtml(bodyText)) {
            emailParam.setMainBodyText(replaceVariables(bodyText, request.getVariables()));
            emailParam.setMimeType(SendEmailParam.EmailMimeType.HTML);
        } else {
            emailParam.setMainBodyText(bodyText);
            emailParam.setMimeType(SendEmailParam.EmailMimeType.TEXT);
        }
        emailParam.setSubject(request.getSubject());
        return emailParam;
    }

    public SendEmailParam processBeforeSendEmail(MailSendRequest request, EmailTemplate emailTemplate) {
        SendEmailParam emailParam = new SendEmailParam();
        emailParam.setFromEmail(request.getFromEmail());
        emailParam.setTos(request.getTos());
        emailParam.setCcs(request.getCcs());
        emailParam.setBcc(request.getBcc());
        emailParam.setFiles(request.getFiles());

        String bodyText = emailTemplate.getBody();
        // Parse main body text
        if (isHtml(bodyText)) {
            bodyText = parseBody(emailTemplate, request.getVariables());
            emailParam.setMimeType(SendEmailParam.EmailMimeType.HTML);
        } else {
            emailParam.setMimeType(SendEmailParam.EmailMimeType.TEXT);
        }
        if (StringUtils.isBlank(bodyText)) {
            return null;
        }
        emailParam.setMainBodyText(bodyText);
        if (StringUtils.isNotBlank(request.getSubject())) {
            emailParam.setSubject(request.getSubject());
            return emailParam;
        }
        // Parse subject
        String subject = parseSubject(emailTemplate, request.getVariables());
        if (StringUtils.isBlank(subject)) {
            return null;
        }
        emailParam.setSubject(subject);
        return emailParam;
    }

    /**
     * 解析邮件的内容，并且替换变量成为对应的数值
     * Parse the content of the email and replace variables with corresponding values
     *
     * @param content   邮件内容
     * @param variables 变量Map
     * @return String
     */
    public String parse(String content, Map<String, Object> variables) {
        if (isHtml(content)) {
            return emailTemplateService.parseHTML(content, variables);
        } else {
            return emailTemplateService.parseText(content, variables);
        }
    }

    private String parseBody(EmailTemplate emailTemplate, Map<String, Object> variables) {
        TemplateRequestDTO templateRequestDTO = new TemplateRequestDTO();
        templateRequestDTO.setId(emailTemplate.getId());
        templateRequestDTO.setVariables(variables);
        try {
            return emailTemplateService.parse(templateRequestDTO);
        } catch (Exception ex) {
            log.error("Parse email template was failure，TemplateId::[{}]", emailTemplate.getId());
            return null;
        }
    }

    private String parseSubject(EmailTemplate emailTemplate, Map<String, Object> variables) {
        TemplateRequestDTO templateRequestDTO = new TemplateRequestDTO();
        templateRequestDTO.setId(emailTemplate.getId().replaceAll(EmailTemplateService.EMAIL_TEMPLATE_ID_PREFIX, "notification_subject."));
        templateRequestDTO.setVariables(variables);
        try {
            return emailTemplateService.parse(templateRequestDTO);
        } catch (Exception ex) {
            log.error("Parse email template was failure，TemplateId::[{}]", emailTemplate.getId());
            return null;
        }
    }

    public static Pattern pattern = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*? />");

    private static boolean isHtml(String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find() && content.contains("html");
    }

    private static String replaceVariables(String bodyText, Map<String, Object> variables) {
        for (String key : variables.keySet()) {
            bodyText = bodyText.replace(String.format("#{%s}", key), (String) variables.get(key));
        }
        return bodyText;
    }

}
