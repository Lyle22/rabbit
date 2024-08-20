package org.rabbit.service.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.configuration.EmailConfiguration;
import org.rabbit.entity.logs.EmailLog;
import org.rabbit.entity.template.EmailTemplate;
import org.rabbit.service.email.impl.DefaultParseEmailVariablesHandler;
import org.rabbit.service.logs.impl.EmailLogService;
import org.rabbit.service.email.impl.EmailTemplateService;
import org.rabbit.service.email.impl.SendEmailServiceFactory;
import org.rabbit.service.email.models.MailSendRequest;
import org.rabbit.service.email.models.SendEmailParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * The class of mail send service
 * <br/>
 * The entrance for sending email
 * @author nine rabbit
 */
@Slf4j
@Service
@ConditionalOnBean(value = EmailConfiguration.class)
public class SendEmailService {

    private final SendEmailServiceFactory sendEmailServiceFactory;
    private final EmailTemplateService emailTemplateService;
    private final DefaultParseEmailVariablesHandler parseEmailVariablesService;
    private final EmailLogService emailLogService;

    @Autowired
    public SendEmailService(
            SendEmailServiceFactory sendEmailServiceFactory, EmailTemplateService emailTemplateService,
            DefaultParseEmailVariablesHandler parseEmailVariablesService,
            EmailLogService emailLogService) {
        this.sendEmailServiceFactory = sendEmailServiceFactory;
        this.emailTemplateService = emailTemplateService;
        this.parseEmailVariablesService = parseEmailVariablesService;
        this.emailLogService = emailLogService;
    }

    /**
     * Send email to all recipient
     *
     * @param mailSendRequest the request class of send email
     * @return Boolean true if success otherwise false
     */
    public boolean send(MailSendRequest mailSendRequest) {
        if (log.isDebugEnabled()) {
            log.debug("Send mail that request parameters:: {} ", JsonHelper.write(mailSendRequest));
        }
        SendEmailParam param = prepareParam(mailSendRequest);
        if (param == null) {
            throw new EmailException(ErrorCode.GLOBAL, "Failed to send email");
        }
        ISendEmailService mailSendService = sendEmailServiceFactory.build(mailSendRequest);
        String accessToken = mailSendService.getAccessToken(mailSendRequest.getFromEmail());
        param.setFromEmail(mailSendRequest.getFromEmail());
        param.setAccessToken(accessToken);
        if (log.isDebugEnabled()) {
            log.debug("After parse variables that send email params ::{} ", JsonHelper.write(param));
        }
        EmailLog emailLog = emailLogService.create(param, mailSendRequest);
        boolean result = false;
        try {
            result = mailSendService.doSend(param);
            emailLogService.update(emailLog.getId(), result);
        } catch (Exception ex) {
            emailLogService.doFail(emailLog.getId(), ex.getMessage());
        }
        return result;
    }

    /**
     * Prepare param when send email
     *
     * @param request the object of send mail request
     */
    private SendEmailParam prepareParam(MailSendRequest request) {
        if (StringUtils.isBlank(request.getTemplateId())) {
            // 采用自定义内容的方式发送邮件
            if (StringUtils.isAnyBlank(request.getText(), request.getSubject())) {
                throw new EmailException(ErrorCode.GLOBAL, "Missing subject or body text when send email");
            }
            if (CollectionUtils.isEmpty(request.getTos())) {
                throw new EmailException(ErrorCode.GLOBAL, "Missing receiver address");
            }
            List<String> emails = request.getTos().stream().filter(ISendEmailService::validateEmail).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(emails)) {
                throw new EmailException(ErrorCode.GLOBAL, "Missing receiver address");
            }
            return parseEmailVariablesService.processBeforeSendEmail(request);
        } else {
            // 采用邮件模板的方式发送邮件
            EmailTemplate EmailTemplate = Optional.ofNullable(emailTemplateService.findById(request.getTemplateId()))
                    .orElseThrow(() -> new EmailException(ErrorCode.GLOBAL, "Invalid email template"));
            if (StringUtils.isBlank(request.getSubject()) && StringUtils.isBlank(EmailTemplate.getSubject())) {
                throw new EmailException(ErrorCode.GLOBAL, "Missing email subject");
            }
            if (CollectionUtils.isEmpty(request.getTos()) && StringUtils.isBlank(EmailTemplate.getTos())) {
                throw new EmailException(ErrorCode.GLOBAL, "Missing receiver address");
            }
            return parseEmailVariablesService.processBeforeSendEmail(request, EmailTemplate);
        }
    }

    /**
     * Send test email through customize email template content
     *
     * @param request the request class of send email
     * @return Boolean true if success otherwise false
     */
    public boolean customizeSend(MailSendRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Send mail that request parameters:: {} ", JsonHelper.write(request));
        }
        // 采用自定义内容的方式发送邮件
        if (StringUtils.isAnyBlank(request.getText(), request.getSubject())) {
            throw new EmailException(ErrorCode.GLOBAL, "Missing subject or body text when send email");
        }
        if (CollectionUtils.isEmpty(request.getTos())) {
            throw new EmailException(ErrorCode.GLOBAL, "Missing receiver address");
        }
        List<String> emails = request.getTos().stream().filter(ISendEmailService::validateEmail).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(emails)) {
            throw new EmailException(ErrorCode.GLOBAL, "Missing receiver address");
        }
        // 解析邮件的标题和正文
        String subject = parseEmailVariablesService.parse(request.getSubject(), request.getVariables());
        String body = parseEmailVariablesService.parse(request.getText(), request.getVariables());
        request.setSubject(subject);
        request.setText(body);
        SendEmailParam param = parseEmailVariablesService.processBeforeSendEmail(request);
        if (param == null) {
            throw new EmailException(ErrorCode.GLOBAL, "Failed to send email");
        }
        ISendEmailService mailSendService = sendEmailServiceFactory.build(request);
        String accessToken = mailSendService.getAccessToken(request.getFromEmail());
        param.setFromEmail(request.getFromEmail());
        param.setAccessToken(accessToken);
        EmailLog log = emailLogService.create(param, request);
        boolean result = false;
        try {
            result = mailSendService.doSend(param);
            emailLogService.update(log.getId(), result);
        } catch (Exception ex) {
            emailLogService.doFail(log.getId(), ex.getMessage());
        }
        return result;
    }

}