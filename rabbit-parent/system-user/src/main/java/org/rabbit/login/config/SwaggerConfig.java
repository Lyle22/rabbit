package org.rabbit.login.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration
 *
 * @author nine rabbit
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates REST API
     *
     * @return The REST API object
     */
    @Bean
    public OpenAPI createRestApi() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Login REST API")
                .description("Login REST API Documentation")
                .version("1.0.0");
    }
}
