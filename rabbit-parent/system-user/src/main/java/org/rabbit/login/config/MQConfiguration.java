package org.rabbit.login.config;

import org.rabbit.login.models.MQProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "message.queue")
public class MQConfiguration {

    /**
     * the configuration information of publish message
     */
    MQProperties ocr;

    /**
     * the configuration information of process result
     */
    MQProperties ocrResult;

    MQProperties pdf;

    MQProperties pdfResult;

    MQProperties conversion;

    MQProperties conversionResult;

    MQProperties damConversion;
    MQProperties damConversionResult;

    public List<MQProperties> getProperties() {
        List<MQProperties> list = new ArrayList<>();
        list.add(ocr);
        list.add(ocrResult);
        list.add(pdf);
        list.add(pdfResult);
        list.add(conversion);
        list.add(conversionResult);
        list.add(damConversion);
        list.add(damConversionResult);
        return list;
    }

}
