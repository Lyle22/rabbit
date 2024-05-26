package org.rabbit.service.mail.models;

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
public class CustomizeMailSendRequest {

    /**
     * the subject of email
     */
    @Getter
    String subject;

    /**
     * the body Text of email
     */
    @Getter
    String text;

    /**
     * the receiver of emails
     */
    private List<String> tos;

    /**
     * 必传参数
     */
    public CustomizeMailSendRequest(String subject, String text, List<String> tos) {
        this.subject = subject;
        this.text = text;
        this.tos = tos;
    }

    public CustomizeMailSendRequest(String subject, String text, List<String> tos, Map<String, Object> variables) {
        this.subject = subject;
        this.text = text;
        this.tos = tos;
        this.variables = variables;
    }

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
    @Setter
    @Getter
    private Map<String, Object> variables;

    @Setter
    @Getter
    private List<File> files;

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
