package org.rabbit.service.email.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplateMailSendRequest {

    /**
     * the id of email template
     **/
    @Getter
    String templateId;

    /**
     * email sender
     *
     * <p>Designated mail send service, </p>
     * <p>If null then use the default service of MailSendService </p>
     */
    @Setter
    @Getter
    private String fromEmail;

    /**
     * the receiver of emails
     */
    private List<String> tos;

    /**
     * the cc-receiver of emails
     */
    @Setter
    private List<String> ccs;
    /**
     * the bcc-receiver of emails
     */
    @Setter
    private List<String> bcc;

    /**
     * extra parameters
     */
    @Getter
    private Map<String, Object> variables;

    @Setter
    @Getter
    private List<File> files;

    public TemplateMailSendRequest(String templateId, List<String> tos, Map<String, Object> variables) {
        this.templateId = templateId;
        this.tos = tos;
        this.variables = variables;
    }

    public List<String> getTos() {
        if (tos == null || tos.isEmpty()) {
            tos = new ArrayList<>();
        }
        tos = tos.stream().distinct().collect(Collectors.toList());
        return tos;
    }

    public List<String> getCcs() {
        if (ccs == null || ccs.isEmpty()) {
            ccs = new ArrayList<>();
        }
        ccs = ccs.stream().distinct().collect(Collectors.toList());
        return ccs;
    }

    public List<String> getBcc() {
        if (bcc == null || bcc.isEmpty()) {
            bcc = new ArrayList<>();
        }
        bcc = bcc.stream().distinct().collect(Collectors.toList());
        return bcc;
    }

}
