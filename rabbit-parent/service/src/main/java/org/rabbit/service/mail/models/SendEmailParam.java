package org.rabbit.service.mail.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author weltuser
 */
@Data
@Builder
public class SendEmailParam implements Serializable {

    String fromEmail;
    /**
     * the id of email template
     **/
    String templateId;

    @NonNull
    List<String> tos;

    /**
     * the cc-receiver of emails
     */
    List<String> ccs;

    /**
     * the subject of email
     */
    String subject;

    /**
     * the body Text of email
     */
    String text;

    /**
     * extra parameters
     */
    Map<String, Object> variables;

    /**
     * the file list
     */
    List<File> files;

}
