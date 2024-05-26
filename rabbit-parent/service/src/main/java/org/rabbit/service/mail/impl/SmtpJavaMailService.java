package org.rabbit.service.mail.impl;

import com.sun.mail.smtp.SMTPTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.service.mail.ISendEmailService;
import org.rabbit.service.mail.OAuth2AuthenticationMethod;
import org.rabbit.service.mail.google.GoogleOAuth2AuthenticationService;
import org.rabbit.service.mail.models.OAuth2SettingRequestDTO;
import org.rabbit.service.mail.models.SendEmailParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

/**
 * @author nine
 */
@Slf4j
@Service
public class SmtpJavaMailService implements ISendEmailService {

    private static final String SMTP_SERVER_PORT = "587";
    private static final String AUTH_X_OAUTH2 = "AUTH XOAUTH2 ";
    private String smtpServerHost;
    private String mailUserName;
    private String mailFromEmail;
    private String accessToken;
    private final GoogleOAuth2AuthenticationService googleOAuth2AuthenticationService;

    @Autowired
    public SmtpJavaMailService(GoogleOAuth2AuthenticationService googleOAuth2AuthenticationService) {
        this.googleOAuth2AuthenticationService = googleOAuth2AuthenticationService;
    }

    @Override
    public boolean match(OAuth2SettingRequestDTO setting) {
        boolean result = OAuth2AuthenticationMethod.GOOGLE == setting.getAuthenticationMethod();
        if (result) {
            smtpServerHost = ISendEmailService.getSmtpServerHost(setting.getAuthenticationMethod());
            mailUserName = setting.getSenderAddress();
            mailFromEmail = setting.getSenderAddress();
        }
        return result;
    }

    @Override
    public String getAccessToken(String senderAddress) {
        accessToken = googleOAuth2AuthenticationService.getAccessToken(senderAddress);
        if (null == accessToken) {
            throw new EmailException(ErrorCode.GLOBAL, "Missing google OAUTH2.0 accessToken when invoke send mail service");
        }
        return accessToken;
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.port", SMTP_SERVER_PORT);
        props.put("mail.smtp.host", smtpServerHost);
        props.put("mail.debug.auth", "true");
        return props;
    }

    @Override
    public boolean doSend(SendEmailParam param) {
        String contentType = SendEmailParam.EmailMimeType.HTML == param.getMimeType() ? "text/html; charset=utf-8" : "text/plain; charset=utf-8";
        try {
            Properties props = getProperties();
            Session session = Session.getInstance(props);
            // If you need to debug，Please open it
            if (log.isDebugEnabled()) {
                session.setDebug(true);
            }

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFromEmail));
            message.setSubject(param.getSubject());
            message.setContent(param.getMainBodyText(), contentType);
            message.setRecipients(Message.RecipientType.TO, buildAddress(param.getTos()));
            message.setRecipients(Message.RecipientType.CC, buildAddress(param.getCcs()));
            message.setRecipients(Message.RecipientType.BCC, buildAddress(param.getBcc()));

            if (CollectionUtils.isNotEmpty(param.getFiles())) {
                Multipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(param.getMainBodyText(), contentType);
                multipart.addBodyPart(messageBodyPart);
                if (null != param.getFiles() && param.getFiles().size() > 0) {
                    for (File file : param.getFiles()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        // Add attachment part
                        attachmentPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(file);
                        attachmentPart.setDataHandler(new DataHandler(source));
                        attachmentPart.setFileName(file.getName());
                        // Add the attachment to the multipart message, combine text and attachment
                        multipart.addBodyPart(attachmentPart);
                    }
                }
                message.setContent(multipart);
            }

            SMTPTransport transport = new SMTPTransport(session, null);
            transport.connect(smtpServerHost, mailUserName, null);
            transport.issueCommand(AUTH_X_OAUTH2 + tokenToBase64Str(mailUserName, accessToken), 235);
            transport.sendMessage(message, message.getAllRecipients());
            if (log.isDebugEnabled()) {
                log.debug("Send mail was success. Recipient ::{}", param.getTos().toString());
            }
            return true;
        } catch (MessagingException e) {
            // TODO add log
            log.error("Failed to send email when use oauth2 identity validate, the message is {}", e.getMessage());
            return false;
        }
    }

    private static String tokenToBase64Str(String userName, String accessToken) {
        final String ctrlA = Character.toString((char) 1);
        final String coded = "user=" + userName + ctrlA + "auth=Bearer " + accessToken + ctrlA + ctrlA;
        return Base64.getEncoder().encodeToString(coded.getBytes());
    }

    private InternetAddress[] buildAddress(List<String> addressList) throws AddressException {
        if (CollectionUtils.isNotEmpty(addressList)) {
            InternetAddress[] addresses = new InternetAddress[addressList.size()];
            for (int index = 0; index < addressList.size(); index++) {
                addresses[index] = new InternetAddress(addressList.get(index));
            }
            return addresses;
        }
        return new InternetAddress[0];
    }

}

