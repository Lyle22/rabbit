package org.rabbit;

import org.rabbit.service.logs.SystemLoggerInterceptor;
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

    private final SystemLoggerInterceptor systemLoggerInterceptor;

    public WebConfiguration(SystemLoggerInterceptor systemLoggerInterceptor) {
        this.systemLoggerInterceptor = systemLoggerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(systemLoggerInterceptor);
    }
}