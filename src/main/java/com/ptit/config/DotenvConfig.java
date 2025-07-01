package com.ptit.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Load .env file from project root
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // Convert dotenv entries to a map
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                envMap.put(entry.getKey(), entry.getValue());
                System.out.println("Loaded env variable: " + entry.getKey() + " = " + 
                    (entry.getKey().contains("PASSWORD") || entry.getKey().contains("SECRET") ? "***" : entry.getValue()));
            });

            // Add to Spring Environment
            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
            
            System.out.println("Environment variables loaded successfully from .env file");

        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file - " + e.getMessage());
        }
    }
}
