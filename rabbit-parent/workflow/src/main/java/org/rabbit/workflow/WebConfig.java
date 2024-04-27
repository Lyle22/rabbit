package org.rabbit.workflow;

import org.flowable.engine.form.AbstractFormType;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.rabbit.workflow.constants.JsonFormType;
import org.rabbit.workflow.constants.ListFormType;
import org.rabbit.workflow.logs.LogInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
    }

    @Bean
    public BeanPostProcessor activitiConfigurer() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof SpringProcessEngineConfiguration) {
                    List<AbstractFormType> customFormTypes = Arrays.<AbstractFormType>asList(
                            new JsonFormType(), new ListFormType()
                    );
                    ((SpringProcessEngineConfiguration) bean).setCustomFormTypes(customFormTypes);
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };
    }
}