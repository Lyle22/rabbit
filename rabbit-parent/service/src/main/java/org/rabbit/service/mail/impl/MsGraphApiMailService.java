package org.rabbit.service.mail.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.graph.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.exception.CustomException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.HttpRequestUtils;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.service.mail.OAuth2AuthenticationFactory;
import org.rabbit.service.mail.OAuth2AuthenticationMethod;
import org.rabbit.service.mail.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.LinkedList;

/**
 * GraphApi mail service of Microsoft Office 365
 */
@Slf4j
@Service
public class MsGraphApiMailService implements OAuthMailSendService {

    private static final String SEND_MAIL_URL = "https://graph.microsoft.com/v1.0/me/sendMail";
    private String accessToken;
    private final OAuth2AuthenticationFactory oauth2AuthenticationFactory;

    @Autowired
    public MsGraphApiMailService(OAuth2AuthenticationFactory oauth2AuthenticationFactory) {
        this.oauth2AuthenticationFactory = oauth2AuthenticationFactory;
    }

    @Override
    public boolean match(OAuth2SettingRequestDTO setting) {
        boolean result = OAuth2AuthenticationMethod.MICROSOFT_OFFICE_365 == setting.getAuthenticationMethod();
        if (result) {
            accessToken = oauth2AuthenticationFactory.build(OAuth2AuthenticationMethod.MICROSOFT_OFFICE_365)
                    .getAccessToken(setting.getSenderAddress());
            if (null == accessToken) {
                String errorMsg = String.format("Missing accessToken when using oauth2.0 mail service of %s",
                        setting.getAuthenticationMethod());
                throw new CustomException(ErrorCode.GLOBAL, errorMsg);
            }
        }
        return result;
    }

    @Override
    public boolean sendText(MailSendRequest mailSendRequest) {
        Message message = new Message();
        message.subject = mailSendRequest.getSubject();
        ItemBody body = new ItemBody();
        body.contentType = BodyType.TEXT;
        body.content = mailSendRequest.getText();
        message.body = body;

        LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
        Recipient toRecipients = new Recipient();
        for (String to : mailSendRequest.getTos()) {
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = to;
            toRecipients.emailAddress = emailAddress;
            toRecipientsList.add(toRecipients);
        }
        message.toRecipients = toRecipientsList;

        LinkedList<Recipient> ccRecipientsList = new LinkedList<Recipient>();
        mailSendRequest.getTos().forEach(address -> {
            Recipient ccRecipients = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = address;
            ccRecipients.emailAddress = emailAddress;
            ccRecipientsList.add(ccRecipients);
        });
        message.ccRecipients = ccRecipientsList;

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setMessage(message);
        emailMessage.setSaveToSentItems(true);
        String param = JsonHelper.write(emailMessage);
        try {
            String result = HttpRequestUtils.postByJson(SEND_MAIL_URL, param, accessToken);
            return true;
        } catch (IOException e) {
            log.error("Failed to send email , error is {}", e.getMessage());
        }
        return false;
    }

    private String getBodyText(MailSendRequest request) {
        return null;
    }

    @Override
    public boolean sendWithHtml(MailSendRequest mailSendRequest) {
        ItemBody body = new ItemBody();
        body.contentType = BodyType.HTML;
        if (StringUtils.isNotBlank(mailSendRequest.getTemplateId())) {
            body.content = getBodyText(mailSendRequest);
        } else {
            body.content = mailSendRequest.getText();
        }
        Message message = new Message();
        message.subject = mailSendRequest.getSubject();
        message.body = body;

        LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
        Recipient toRecipients = new Recipient();
        for (String to : mailSendRequest.getTos()) {
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = to;
            toRecipients.emailAddress = emailAddress;
            toRecipientsList.add(toRecipients);
        }
        message.toRecipients = toRecipientsList;

        LinkedList<Recipient> ccRecipientsList = new LinkedList<Recipient>();
        mailSendRequest.getTos().forEach(address -> {
            Recipient ccRecipients = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = address;
            ccRecipients.emailAddress = emailAddress;
            ccRecipientsList.add(ccRecipients);
        });
        message.ccRecipients = ccRecipientsList;

        LinkedList<InternetMessageHeader> internetMessageHeadersList = new LinkedList<InternetMessageHeader>();
        InternetMessageHeader internetMessageHeaders = new InternetMessageHeader();
        internetMessageHeaders.name = "x-custom-header-group-name";
        internetMessageHeaders.value = "Nevada";
        internetMessageHeadersList.add(internetMessageHeaders);
        InternetMessageHeader internetMessageHeaders1 = new InternetMessageHeader();
        internetMessageHeaders1.name = "x-custom-header-group-id";
        internetMessageHeaders1.value = "NV001";
        internetMessageHeadersList.add(internetMessageHeaders1);
        message.internetMessageHeaders = internetMessageHeadersList;

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setMessage(message);
        emailMessage.setSaveToSentItems(true);
        String param = JsonHelper.write(emailMessage);
        try {
            String result = HttpRequestUtils.postByJson(SEND_MAIL_URL, param, accessToken);
            return true;
        } catch (IOException e) {
            log.error("Failed to send email , error is {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean sendWithAttachment(MailSendRequest request) {
        log.debug("receive email {}", request.getTos().toString());
        MsItemBody body = new MsItemBody();
        if (StringUtils.isNotBlank(request.getTemplateId())) {
            body.setContent(getBodyText(request));
            body.setContentType("HTML");
        } else {
            body.setContentType("TEXT");
            body.setContent(request.getText());
        }
        MsEmailMessage message = new MsEmailMessage();
        message.setSubject(request.getSubject());
        message.setBody(body);

        LinkedList<MsRecipient> toRecipientsList = new LinkedList<MsRecipient>();
        MsRecipient toRecipients = new MsRecipient();
        request.getTos().forEach(to -> {
            MsEmailAddress emailAddress = new MsEmailAddress();
            emailAddress.setAddress(to);
            toRecipients.setEmailAddress(emailAddress);
            toRecipientsList.add(toRecipients);
        });
        message.setToRecipients(toRecipientsList);

        LinkedList<MsRecipient> ccRecipientsList = new LinkedList<MsRecipient>();
        request.getCcs().forEach(address -> {
            MsRecipient ccRecipients = new MsRecipient();
            MsEmailAddress emailAddress = new MsEmailAddress();
            emailAddress.setAddress(address);
            ccRecipients.setEmailAddress(emailAddress);
            ccRecipientsList.add(ccRecipients);
        });
        message.setCcRecipients(ccRecipientsList);

        LinkedList<MsEmailAttachment> attachmentsList = new LinkedList<MsEmailAttachment>();
        if (null != request.getFiles() && request.getFiles().size() > 0) {
            for (File file : request.getFiles()) {
                MsEmailAttachment attachments = new MsEmailAttachment();
                attachments.setName(file.getName());
                attachments.setContentType(new MimetypesFileTypeMap().getContentType(file));
                attachments.setOdataType("#microsoft.graph.fileAttachment");
                attachments.setIsInline(true);
                try {
                    attachments.setContentBytes(Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                attachmentsList.add(attachments);
            }
        }
        message.setAttachments(attachmentsList);

        MsEmailMessageRequest emailMessage = new MsEmailMessageRequest();
        emailMessage.setMessage(message);
        emailMessage.setSaveToSentItems(true);

        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(message));
        String param = gson.toJson(emailMessage);
        try {
            HttpRequestUtils.postByJson(SEND_MAIL_URL, param, accessToken);
            return true;
        } catch (IOException e) {
            log.error("Failed to send email , error is {}", e.getMessage());
        }
        return false;
    }

}
