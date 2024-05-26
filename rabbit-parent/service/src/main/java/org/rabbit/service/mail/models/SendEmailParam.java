package org.rabbit.service.mail.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.util.List;

/**
 *  Request parameters for sending email
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailParam {

    public enum EmailMimeType {
        TEXT, HTML, UNEXPECTED
    }

    EmailMimeType mimeType;

    /**
     * email sender
     *
     * <p>Designated mail send service, </p>
     * <p>If null then use the default service of MailSendService </p>
     */
    String fromEmail;

    /**
     * the subject of email
     */
    String subject;

    /**
     * the body Text of email
     */
    String mainBodyText;

    /**
     * the receiver of emails
     */
    List<String> tos;

    /**
     * the cc-receiver of emails
     */
    List<String> ccs;

    /**
     * the bcc-receiver of emails
     */
    List<String> bcc;

    /**
     * the attachment files
     */
    List<File> files;

    String accessToken;

}