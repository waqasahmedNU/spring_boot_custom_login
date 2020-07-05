package com.spring.boot.custom_login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CustomLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomLoginApplication.class, args);
    }

}
