package com.azhag_swe.tech_tutorial;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class TechTutorialApplication {
    public static void main(String[] args) {
        SpringApplication.run(TechTutorialApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("SYSTEM"); // Default for initialization
    }
}
