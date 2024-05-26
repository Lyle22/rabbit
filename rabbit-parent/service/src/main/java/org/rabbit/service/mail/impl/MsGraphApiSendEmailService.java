package org.rabbit.service.mail.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.graph.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.HttpRequestUtils;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.service.mail.ISendEmailService;
import org.rabbit.service.mail.OAuth2AuthenticationMethod;
import org.rabbit.service.mail.microsoft.MicrosoftOAuth2AuthenticationService;
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
 *
 * @author nine
 */
@Slf4j
@Service
public class MsGraphApiSendEmailService implements ISendEmailService {

    private static final String SEND_MAIL_URL = "https://graph.microsoft.com/v1.0/me/sendMail";
    private String accessToken;
    private final MicrosoftOAuth2AuthenticationService microsoftOAuth2AuthenticationService;

    @Autowired
    public MsGraphApiSendEmailService(MicrosoftOAuth2AuthenticationService microsoftOAuth2AuthenticationService) {
        this.microsoftOAuth2AuthenticationService = microsoftOAuth2AuthenticationService;
    }

    @Override
    public boolean match(OAuth2SettingRequestDTO setting) {
        return OAuth2AuthenticationMethod.MICROSOFT_OFFICE_365 == setting.getAuthenticationMethod();
    }

    @Override
    public String getAccessToken(String senderAddress) {
        accessToken = microsoftOAuth2AuthenticationService.getAccessToken(senderAddress);
        if (null == accessToken) {
            throw new EmailException(ErrorCode.GLOBAL, "Missing microsoft Exchange Online OAUTH2.0 accessToken when invoke send mail service");
        }
        return accessToken;
    }

    @Override
    public boolean doSend(SendEmailParam param) {
        if (CollectionUtils.isEmpty(param.getFiles())) {
            return execute(param);
        } else {
            return executeWithFiles(param);
        }
    }

    private boolean execute(SendEmailParam param) {
        BodyType bodyType = SendEmailParam.EmailMimeType.HTML == param.getMimeType() ? BodyType.HTML : BodyType.TEXT;
        Message message = this.initializeRecipients(param);
        message.subject = param.getSubject();
        ItemBody body = new ItemBody();
        body.contentType = bodyType;
        body.content = param.getMainBodyText();
        message.body = body;

        if (BodyType.HTML == bodyType) {
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
        }

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setMessage(message);
        emailMessage.setSaveToSentItems("true");
        String jsonParams = JsonHelper.write(emailMessage);
        try {
            String result = HttpRequestUtils.postByJson(SEND_MAIL_URL, jsonParams, accessToken);
            if (log.isDebugEnabled()) {
                log.debug("Send mail was success. Recipient ::{} Result::[{}]", param.getTos().toString(), result);
            }
            return true;
        } catch (IOException e) {
            log.error("Failed to send email, error is {}", e.getMessage());
        }
        return false;
    }

    private boolean executeWithFiles(SendEmailParam param) {
        BodyType bodyType = SendEmailParam.EmailMimeType.HTML == param.getMimeType() ? BodyType.HTML : BodyType.TEXT;
        MsItemBody body = new MsItemBody();
        body.setContentType(bodyType.name().toUpperCase());
        body.setContent(param.getMainBodyText());

        MsEmailMessage message = new MsEmailMessage();
        message.setSubject(param.getSubject());
        message.setBody(body);

        LinkedList<MsRecipient> toRecipientsList = new LinkedList<MsRecipient>();
        for (String to : param.getTos()) {
            MsEmailAddress emailAddress = new MsEmailAddress();
            emailAddress.setAddress(to);
            MsRecipient toRecipients = new MsRecipient();
            toRecipients.setEmailAddress(emailAddress);
            toRecipientsList.add(toRecipients);
        }
        message.setToRecipients(toRecipientsList);

        LinkedList<MsRecipient> ccRecipientsList = new LinkedList<MsRecipient>();
        param.getCcs().forEach(address -> {
            MsRecipient ccRecipients = new MsRecipient();
            MsEmailAddress emailAddress = new MsEmailAddress();
            emailAddress.setAddress(address);
            ccRecipients.setEmailAddress(emailAddress);
            ccRecipientsList.add(ccRecipients);
        });
        message.setCcRecipients(ccRecipientsList);

        LinkedList<MsRecipient> bccRecipientsList = new LinkedList<MsRecipient>();
        param.getBcc().forEach(address -> {
            MsRecipient recipient = new MsRecipient();
            MsEmailAddress emailAddress = new MsEmailAddress();
            emailAddress.setAddress(address);
            recipient.setEmailAddress(emailAddress);
            bccRecipientsList.add(recipient);
        });
        message.setBccRecipients(bccRecipientsList);

        LinkedList<MsEmailAttachment> attachmentsList = new LinkedList<MsEmailAttachment>();
        if (null != param.getFiles() && param.getFiles().size() > 0) {
            for (File file : param.getFiles()) {
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
        String jsonParam = gson.toJson(emailMessage);
        try {
            HttpRequestUtils.postByJson(SEND_MAIL_URL, jsonParam, accessToken);
            if (log.isDebugEnabled()) {
                log.debug("Send mail was success.  Recipient ::[{}]", param.getTos().toString());
            }
            return true;
        } catch (IOException e) {
            log.error("Failed to send email , error is {}", e.getMessage());
        }
        return false;
    }

    private Message initializeRecipients(SendEmailParam param) {
        Message message = new Message();
        LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
        for (String to : param.getTos()) {
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = to;
            Recipient toRecipients = new Recipient();
            toRecipients.emailAddress = emailAddress;
            toRecipientsList.add(toRecipients);
        }
        message.toRecipients = toRecipientsList;

        LinkedList<Recipient> ccRecipientsList = new LinkedList<Recipient>();
        param.getCcs().forEach(address -> {
            Recipient ccRecipients = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = address;
            ccRecipients.emailAddress = emailAddress;
            ccRecipientsList.add(ccRecipients);
        });
        message.ccRecipients = ccRecipientsList;

        LinkedList<Recipient> bccRecipientsList = new LinkedList<Recipient>();
        param.getBcc().forEach(address -> {
            Recipient bccRecipient = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = address;
            bccRecipient.emailAddress = emailAddress;
            bccRecipientsList.add(bccRecipient);
        });
        message.bccRecipients = bccRecipientsList;
        return message;
    }

}
