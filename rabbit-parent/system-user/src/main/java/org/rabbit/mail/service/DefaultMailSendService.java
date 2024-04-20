package org.rabbit.mail.service;

import org.rabbit.common.OAuth2AuthenticationMethod;
import org.rabbit.mail.models.MailSendRequest;
import org.rabbit.mail.models.OAuth2SettingRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * The class of default mailSendService
 */
@Slf4j
@Service
public class DefaultMailSendService implements OAuthMailSendService {

    @Value("${mail.default.from.address}")
    private String mailFrom;

    private final JavaMailSender javaMailSender;

    @Autowired
    public DefaultMailSendService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public boolean match(OAuth2SettingRequestDTO setting) {
        return setting.getAuthenticationMethod() == null ||
                setting.getAuthenticationMethod() == OAuth2AuthenticationMethod.DEFAULT;
    }

    @Override
    public boolean sendText(MailSendRequest mailSendRequest) {
        String[] tos = mailSendRequest.getTos().toArray(String[]::new);
        String[] ccs = mailSendRequest.getCcs().toArray(String[]::new);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(tos);
        message.setCc(ccs);
        message.setSubject(mailSendRequest.getSubject());
        message.setText(mailSendRequest.getText());
        message.setFrom(mailFrom);
        javaMailSender.send(message);
        return true;
    }

    @Override
    public boolean sendWithHtml(MailSendRequest mailSendRequest) {
        String[] tos = mailSendRequest.getTos().toArray(String[]::new);
        String[] ccs = mailSendRequest.getCcs().toArray(String[]::new);
        try {
            String bodyText = getBodyText(mailSendRequest);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(mailSendRequest.getSubject());
            helper.setFrom(mailFrom);
            helper.setTo(tos);
            helper.setCc(ccs);
            helper.setText(bodyText, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while sending email [{}] to {}", mailSendRequest.getSubject(), tos);
        }
        return true;
    }

    @Override
    public boolean sendWithAttachment(MailSendRequest mailSendRequest) {
        String[] tos = mailSendRequest.getTos().stream().toArray(String[]::new);
        String[] ccs = mailSendRequest.getCcs().toArray(String[]::new);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(mailSendRequest.getSubject());
            helper.setFrom(mailFrom);
            helper.setTo(tos);
            helper.setCc(ccs);
            helper.setText(getBodyText(mailSendRequest), true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while sending email using template: {}, variables: {}, subject: {}, to: {}, error: {}",
                    mailSendRequest.getTemplateId(), mailSendRequest.getVariables(), mailSendRequest.getSubject(),
                    tos, e.getMessage());
        }
        return true;
    }

    private String getBodyText(MailSendRequest mailSendRequest) {
        return mailSendRequest.getText();
    }
}
