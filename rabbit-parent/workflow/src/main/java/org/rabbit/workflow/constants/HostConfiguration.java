package org.rabbit.workflow.constants;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Host Configuration
 *
 * @author weltuser
 */
@Configuration
public class HostConfiguration {

    @Value("${API_VERSION}")
    private String apiVersion;

    public String getApiVersion() {
        return apiVersion;
    }

}

