package com.app.novaes;

import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NovaesApplication {
    private static final Logger log = (Logger) LoggerFactory.getLogger(NovaesApplication.class);
    
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(NovaesApplication.class, args);
        log.info("=== APPLICATION STARTED SUCCESSFULLY ===");  // Visible in logs
    }
}