package com.Capstone.InterviewTracking.security;

import com.Capstone.InterviewTracking.constant.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the Interview Tracking application.
 */
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Creates a SecurityConfig with the JWT request filter.
     *
     * @param jwtFilter the JWT filter
     */
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the security filter chain with route-level access rules.
     *
     * @param http the HTTP security builder
     * @return the configured security filter chain
     * @throws Exception if the security configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AppConstants.AUTH_MATCHER).permitAll()
                        .requestMatchers(AppConstants.JOB_HR_PATH).hasRole("HR")
                        .requestMatchers(HttpMethod.PUT, AppConstants.JOB_TOGGLE_PATH).hasRole("HR")
                        .requestMatchers(HttpMethod.GET, AppConstants.JOB_LIST_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, AppConstants.JOB_BY_ID_PATH).permitAll()
                        .requestMatchers(AppConstants.JOB_MATCHER).hasRole("HR")
                        .requestMatchers(AppConstants.HR_MATCHER).hasRole("HR")
                        .requestMatchers(AppConstants.PANEL_MATCHER).hasRole("PANEL")
                        .requestMatchers(AppConstants.CANDIDATE_MATCHER).hasRole("CANDIDATE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a BCrypt password encoder bean.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the authentication manager as a Spring bean.
     *
     * @param config the authentication configuration
     * @return the authentication manager
     * @throws Exception if the manager cannot be retrieved
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
