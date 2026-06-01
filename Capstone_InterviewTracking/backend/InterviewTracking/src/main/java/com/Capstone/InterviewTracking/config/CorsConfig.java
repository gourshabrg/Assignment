package com.Capstone.InterviewTracking.config;

import com.Capstone.InterviewTracking.constant.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS configuration for the application.
 */
@Configuration
public class CorsConfig {

    /** Comma-separated list of allowed origins; defaults to localhost:5500 and 127.0.0.1:5500. */
    @Value("#{'${app.cors.allowed-origins:http://127.0.0.1:5500,http://localhost:5500}'.split(',')}")
    private List<String> allowedOrigins;

    /** Comma-separated list of allowed HTTP methods; defaults to GET, POST, PUT, DELETE, OPTIONS. */
    @Value("#{'${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}'.split(',')}")
    private List<String> allowedMethods;

    /**
     * Registers the global CORS filter applied to all API paths.
     *
     * @return the CORS filter bean
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(allowedMethods);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(AppConstants.ALL_PATHS, config);

        return new CorsFilter(source);
    }
}
