package org.rabbit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ninerabbit
 */
@SpringBootApplication
@Slf4j
@MapperScan({"org.rabbit.service.*.dao","org.rabbit.service.*.mapper"})
public class UserApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
		log.info(" -------> Starting user model application...");
	}
}
