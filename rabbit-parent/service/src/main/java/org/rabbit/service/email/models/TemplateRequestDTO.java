package org.rabbit.service.email.models;

import lombok.Data;

import java.util.Map;

@Data
public class TemplateRequestDTO {
    private String id;
    private Boolean subject;
    private Map<String, Object> variables;
}
