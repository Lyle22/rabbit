package org.rabbit.service.mail.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MsEmailMessageRequest {

    MsEmailMessage message;

    boolean saveToSentItems;

}
