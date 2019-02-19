package org.rabbit.cofig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 *
 * @author zhengguang 日期：2019年1月2日 上午10:06:49
 */
@Configuration
public class CosrConfig {

  private CorsConfiguration buildConfig() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("*"); // 1允许任何域名使用
    corsConfiguration.addAllowedHeader("*"); // 2允许任何头
    corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
    return corsConfiguration;
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", buildConfig()); // 4
    return new CorsFilter(source);
  }

}
