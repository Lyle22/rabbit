package org.rabbit.mail.service;

import org.rabbit.common.OAuth2AuthenticationMethod;
import org.rabbit.exception.CustomException;
import org.rabbit.exception.ErrorCode;
import org.rabbit.login.security.oauth.OAuth2AuthenticationFactory;
import org.rabbit.mail.models.MailSendRequest;
import org.rabbit.mail.models.OAuth2SettingRequestDTO;
import com.sun.mail.smtp.SMTPTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

@Slf4j
@Service
public class SmtpJavaMailService implements OAuthMailSendService {

    private static final String SMTP_SERVER_PORT = "587";
    private static final String AUTH_X_OAUTH2 = "AUTH XOAUTH2 ";
    private String smtpServerHost;
    private String mailUserName;
    private String mailFromEmail;
    private String accessToken;
    private final OAuth2AuthenticationFactory oAuth2AuthenticationFactory;

    @Autowired
    public SmtpJavaMailService(OAuth2AuthenticationFactory oAuth2AuthenticationFactory) {
        this.oAuth2AuthenticationFactory = oAuth2AuthenticationFactory;
    }

    private String getSmtpServerHost(OAuth2AuthenticationMethod method) {
        switch (method) {
            case GOOGLE:
                return "smtp.gmail.com";
            case MICROSOFT_OFFICE_365:
                return "outlook.office365.com";
            default:
                return null;
        }
    }

    @Override
    public boolean match(OAuth2SettingRequestDTO setting) {
        boolean result = OAuth2AuthenticationMethod.GOOGLE == setting.getAuthenticationMethod();
        if (result) {
            smtpServerHost = getSmtpServerHost(setting.getAuthenticationMethod());
            mailUserName = setting.getSenderAddress();
            mailFromEmail = setting.getSenderAddress();
            accessToken = oAuth2AuthenticationFactory.build(setting.getAuthenticationMethod()).getAccessToken(setting.getSenderAddress());
            if (null == accessToken) {
                String errorMsg = String.format("Missing accessToken when using oauth2.0 mail service of %s", setting.getAuthenticationMethod());
                throw new CustomException(ErrorCode.GLOBAL, errorMsg);
            }
        }
        return result;
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
    public boolean sendText(MailSendRequest request) {
        return send(accessToken, request.getTos(), request.getSubject(), request.getText(), "text/plain", request.getCcs());
    }

    @Override
    public boolean sendWithHtml(MailSendRequest request) {
        return true;
    }

    @Override
    public boolean sendWithAttachment(MailSendRequest request) {
        try {
            Properties props = getProperties();
            Session session = Session.getInstance(props);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFromEmail));
            message.setSubject(request.getSubject());
            message.setRecipients(Message.RecipientType.TO, getTos(request));
            message.setRecipients(Message.RecipientType.CC, getCcs(request));
            // message.setContent(bodyText, "text/html");

            Multipart multipart = new MimeMultipart();
            // Text body
            String bodyText = getBodyText(request);
            BodyPart messageBodyPart = new MimeBodyPart();
            String strippedText = bodyText.replaceAll("<[^>]+>", " ");
            messageBodyPart.setText(strippedText);
            // File body
            multipart.addBodyPart(messageBodyPart);
            if (null != request.getFiles() && request.getFiles().size() > 0) {
                for (File file : request.getFiles()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    // attachment part
                    attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(file.getName());
                    // Add the attachment to the multipart message, combine text and attachment
                    multipart.addBodyPart(attachmentPart);
                }
            }

            message.setContent(multipart);
            SMTPTransport transport = new SMTPTransport(session, null);
            transport.connect(smtpServerHost, mailUserName, null);
            transport.issueCommand(AUTH_X_OAUTH2 + tokenToBase64Str(mailUserName, accessToken), 235);
            transport.sendMessage(message, message.getAllRecipients());
            return true;
        } catch (Exception ex) {
            log.error("Send email fail {} ", ex.getMessage(), ex);
        }
        return false;
    }

    private boolean send(String accessToken, List<String> tos, String subject, String bodyText, String contentType, List<String> ccs) {
        try {
            Properties props = getProperties();

            Session session = Session.getInstance(props);
            // If you need to debug，Please open it
            if (log.isInfoEnabled()) {
                session.setDebug(true);
            }

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFromEmail));

            InternetAddress[] addresses = new InternetAddress[tos.size()];
            for (int index = 0; index < tos.size(); index++) {
                addresses[index] = new InternetAddress(tos.get(index));
            }
            message.setRecipients(Message.RecipientType.TO, addresses);

            if (CollectionUtils.isNotEmpty(ccs)) {
                InternetAddress[] cssAddresses = new InternetAddress[ccs.size()];
                for (int index = 0; index < ccs.size(); index++) {
                    cssAddresses[index] = new InternetAddress(ccs.get(index));
                }
                message.setRecipients(Message.RecipientType.CC, cssAddresses);
            }
            message.setSubject(subject);
            message.setContent(bodyText, contentType);

            SMTPTransport transport = new SMTPTransport(session, null);
            transport.connect(smtpServerHost, mailUserName, null);
            transport.issueCommand(AUTH_X_OAUTH2 + tokenToBase64Str(mailUserName, accessToken), 235);
            transport.sendMessage(message, message.getAllRecipients());

            if (log.isInfoEnabled()) {
                log.info("The Email sent Successfully，recipient: {} , Message: {} ", addresses.toString(), bodyText);
            }
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send email when use oauth2 identity validate, the message is {}", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static String tokenToBase64Str(String userName, String accessToken) {
        final String ctrlA = Character.toString((char) 1);
        final String coded = "user=" + userName + ctrlA + "auth=Bearer " + accessToken + ctrlA + ctrlA;
        return Base64.getEncoder().encodeToString(coded.getBytes());
    }

    private String getBodyText(MailSendRequest request) {
        if (StringUtils.isNotBlank(request.getTemplateId())) {
            return null;
        } else {
            return request.getText();
        }
    }

    private InternetAddress[] getTos(MailSendRequest request) throws AddressException {
        List<String> tos = request.getTos();
        InternetAddress[] addresses = new InternetAddress[tos.size()];
        for (int index = 0; index < tos.size(); index++) {
            addresses[index] = new InternetAddress(tos.get(index));
        }
        return addresses;
    }

    private InternetAddress[] getCcs(MailSendRequest request) throws AddressException {
        if (CollectionUtils.isNotEmpty(request.getCcs())) {
            List<String> ccs = request.getCcs();
            InternetAddress[] cssAddresses = new InternetAddress[ccs.size()];
            for (int index = 0; index < ccs.size(); index++) {
                cssAddresses[index] = new InternetAddress(ccs.get(index));
            }
            return cssAddresses;
        }
        return new InternetAddress[0];
    }

}

