package org.rabbit.service.mail.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * the class that implements send mail
 *
 * @author ninerabbit
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailSendRequest {

    /**
     * email sender
     *
     * <p>Designated mail send service, </p>
     * <p>If null then use the default service of MailSendService </p>
     */
    String fromEmail;

    /**
     * the receiver of email
     */
    String to;
    /**
     * the receiver of emails
     */
    List<String> tos;

    /**
     * the cc-receiver of emails
     */
    List<String> ccs;

    /**
     * the subject of email
     */
    @NotNull
    String subject;
    /**
     * the body Text of email
     */
    String text;

    /**
     * the id of email template
     **/
    String templateId;

    /**
     * extra parameters
     */
    Map<String, Object> variables;

    List<File> files;

    String accessToken;

    public List<String> getTos() {
        if (tos == null || tos.size() == 0) {
            tos = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(getTo())) {
            tos.add(getTo());
        }
        tos = tos.stream().distinct().collect(Collectors.toList());
        return tos;
    }

    public List<String> getCcs() {
        if (ccs == null || ccs.size() == 0) {
            ccs = new ArrayList<>();
        }
        ccs = ccs.stream().distinct().collect(Collectors.toList());
        return ccs;
    }

}
