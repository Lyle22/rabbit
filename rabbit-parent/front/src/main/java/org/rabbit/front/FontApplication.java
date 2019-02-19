package org.rabbit.front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class FontApplication {

    protected final static Logger logger = LoggerFactory.getLogger(FontApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FontApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        logger.info("FontApplication runing success!");
        System.err.println("Access URL : http://localhost:8099/user/test");
    }

}
