package com.develop.logsentry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LogSentryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogSentryApplication.class, args);
    }

}