package org.rabbit.workflow;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type of workflow application.
 *
 * @author nine rabbit
 */
@SpringBootApplication(proxyBeanMethods = false)
@Slf4j
@MapperScan({"org.rabbit.service.*.dao","org.rabbit.service.*.mapper"})
public class WorkflowApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }
}
