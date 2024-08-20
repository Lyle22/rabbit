package org.rabbit.service.email.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * the bcc-receiver of emails
     */
    List<String> bcc;

    /**
     * the subject of email
     */
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
            tos.add(to);
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

    public List<String> getBcc() {
        if (bcc == null || bcc.size() == 0) {
            bcc = new ArrayList<>();
        }
        bcc = bcc.stream().distinct().collect(Collectors.toList());
        return bcc;
    }

}
