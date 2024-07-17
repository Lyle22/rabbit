package org.rabbit.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author nine rabbit
 **/
@Configuration
@MapperScan({"org.rabbit.service.*.dao","org.rabbit.service.*.mapper"})
public class MapperScanConfiguration {

}
