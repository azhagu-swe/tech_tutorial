package com.azhag_swe.tech_tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TechTutorialApplication {
    public static void main(String[] args) {
        SpringApplication.run(TechTutorialApplication.class, args);
    }
}
