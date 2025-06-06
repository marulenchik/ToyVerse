package com.toyverse.toyverse_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/**",// Public user-related API
                                "/api/order-items/**",
                                "/api/order-items",
                                "/api/orders",
                                "/api/purchases",
                                "/api/toys/**",
                                "/swagger-ui/**",          // Swagger UI static resources
                                "/v3/api-docs/**",         // OpenAPI JSON spec
                                "/swagger-ui.html"         // Optional: direct Swagger UI HTML
                        ).permitAll()
                        .anyRequest().authenticated()  // Secure everything else
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
