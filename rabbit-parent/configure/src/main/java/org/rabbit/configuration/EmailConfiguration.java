package org.rabbit.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Email configuration class
 *
 * @author nine rabbit
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
public class EmailConfiguration {

    public static String DEFAULT_SENDER_ADDRESS;

    @Value("${email.default.from.address:abc-example@163.com}")
    private void setDefaultSenderAddress(String defaultSenderAddress) {
        EmailConfiguration.DEFAULT_SENDER_ADDRESS = defaultSenderAddress;
    }

}
