package org.rabbit.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author nine rabbit
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
public class MailSendConfiguration {

    public static String DEFAULT_SENDER_ADDRESS;

    @Value("${mail.default.from.address}")
    private void setDefaultSenderAddress(String defaultSenderAddress) {
        MailSendConfiguration.DEFAULT_SENDER_ADDRESS = defaultSenderAddress;
    }

}
