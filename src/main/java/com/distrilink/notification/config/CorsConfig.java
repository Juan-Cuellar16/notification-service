package com.distrilink.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS: lets the web client (distrilink-web, served by Live Server on port 5500)
 * call this API from the browser. Without it the browser blocks the requests,
 * because the page and the API run on different ports.
 *
 * The allowed origins come from application.properties (app.cors.allowed-origins),
 * so a new address can be added without touching the code.
 *
 * SAME FILE FOR THE 4 SERVICES: copy it and change only the package name.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .exposedHeaders("Location")
                .maxAge(3600);
    }
}
