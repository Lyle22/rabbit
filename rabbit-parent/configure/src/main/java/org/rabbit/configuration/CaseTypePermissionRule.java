package org.rabbit.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * @author Lyle
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "docpal.case-type.permission")
public class CaseTypePermissionRule {

    private Map<String, Integer> rules;

    public Map<String, Integer> getRules() {
        return rules;
    }

    public void setRules(Map<String, Integer> rules) {
        this.rules = rules;
    }
}
