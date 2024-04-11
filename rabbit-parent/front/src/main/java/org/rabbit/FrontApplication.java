package org.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FrontApplication {

	protected static final Logger logger = LoggerFactory.getLogger(FrontApplication.class);

	public static void main(String[] args) {
	        SpringApplication app = new SpringApplication(FrontApplication.class);
	        app.setBannerMode(Banner.Mode.OFF);
	        app.run(args);
	        logger.info("Font Application running success!");
	        System.err.println("Access URL : http://localhost:1080/user/test");
	    }
}
