package org.rabbit.workflow;

import org.rabbit.service.logs.SystemLoggerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author nine rabbit
 */
@Configuration
@ComponentScan(basePackages = {"org.rabbit"})
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    SystemLoggerInterceptor systemLoggerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(systemLoggerInterceptor);
    }
}