package com.alwaysrighttempinc.restapiservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("Anomalies").pathsToMatch("/v1/**").build();
    }

    @Bean
    public OpenAPI openApiInfo() {
        return new OpenAPI()
                .info(new Info().title("Anomalies REST API")
                .description("An api for displaying anomalies")
                .version("v0.0.1")
                .license(new License()));
    }
}
