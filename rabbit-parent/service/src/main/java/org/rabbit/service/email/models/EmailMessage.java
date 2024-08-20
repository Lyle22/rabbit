package org.rabbit.service.email.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microsoft.graph.models.Message;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailMessage {

    Message message;

    String saveToSentItems;

}
