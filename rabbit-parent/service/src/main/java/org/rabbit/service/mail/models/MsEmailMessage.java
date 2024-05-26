package org.rabbit.service.mail.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MsEmailMessage {

    public String subject;

    public MsItemBody body;

    public List<MsRecipient> toRecipients;

    public List<MsRecipient> ccRecipients;

    public List<MsRecipient> bccRecipients;

    public LinkedList<MsEmailAttachment> attachments;

}
