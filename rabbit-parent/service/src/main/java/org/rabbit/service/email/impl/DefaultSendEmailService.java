package org.rabbit.service.email.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.configuration.EmailConfiguration;
import org.rabbit.service.email.ISendEmailService;
import org.rabbit.service.email.OAuthWay;
import org.rabbit.service.email.models.OAuth2SettingRequestDTO;
import org.rabbit.service.email.models.SendEmailParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * The implement class of default sending email service
 */
@Slf4j
@Service
@ConditionalOnBean(value = EmailConfiguration.class)
public class DefaultSendEmailService implements ISendEmailService {

    private final JavaMailSender javaMailSender;

    public DefaultSendEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public boolean match(OAuth2SettingRequestDTO setting) {
        return setting.getAuthenticationMethod() == OAuthWay.DEFAULT;
    }

    @Override
    public String getAccessToken(String senderAddress) {
        return null;
    }

    @Override
    public boolean doSend(SendEmailParam param) {
        boolean isHTML = SendEmailParam.EmailMimeType.HTML == param.getMimeType();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(EmailConfiguration.DEFAULT_SENDER_ADDRESS);
            helper.setSubject(param.getSubject());
            helper.setTo(param.getTos().toArray(String[]::new));
            helper.setCc(param.getCcs().toArray(String[]::new));
            helper.setBcc(param.getBcc().toArray(String[]::new));
            helper.setText(param.getMainBodyText(), isHTML);
            if (CollectionUtils.isNotEmpty(param.getFiles())) {
                for (File file : param.getFiles()) {
                    if (file.exists()) {
                        helper.addAttachment(file.getName(), file);
                    }
                }
            }
            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            log.error("Error occurred while sending email [{}] to {}", param.getSubject(), JsonHelper.write(param));
            e.printStackTrace();
        }
        return false;
    }

}

