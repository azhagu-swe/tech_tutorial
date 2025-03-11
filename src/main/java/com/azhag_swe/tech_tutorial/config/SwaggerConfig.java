package com.azhag_swe.tech_tutorial.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tech Tutorials API")
                        .version("1.0")
                        .description("API documentation for the Tech Tutorials application")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("azhagu.swe@gmail.com")
                                .url("https://azhagu_tech.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
