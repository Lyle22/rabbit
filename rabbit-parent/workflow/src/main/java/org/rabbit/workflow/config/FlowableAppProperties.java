package org.rabbit.workflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable App Properties
 *
 * @author nine rabbit
 */
@Configuration
@ConfigurationProperties(prefix = "flowable.app", ignoreUnknownFields = true)
public class FlowableAppProperties {

    private String customName;

    private String targetNameSpace;


    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getTargetNameSpace() {
        return targetNameSpace;
    }

    public void setTargetNameSpace(String targetNameSpace) {
        this.targetNameSpace = targetNameSpace;
    }
}

