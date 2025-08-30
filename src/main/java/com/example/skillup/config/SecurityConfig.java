package com.example.skillup.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - Authentication
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/stats/**").permitAll()
                
                // Public endpoints - Course browsing (GET only)
                .requestMatchers("GET", "/api/courses").permitAll()
                .requestMatchers("GET", "/api/courses/").permitAll()
                .requestMatchers("GET", "/api/courses/categories").permitAll()
                .requestMatchers("GET", "/api/courses/search").permitAll()
                .requestMatchers("GET", "/api/courses/category/**").permitAll()
                .requestMatchers("GET", "/api/courses/{id}").permitAll()
                
                // Protected course management endpoints (POST, PUT, DELETE)
                .requestMatchers("POST", "/api/courses").authenticated()
                .requestMatchers("PUT", "/api/courses/{id}").authenticated()
                .requestMatchers("DELETE", "/api/courses/{id}").authenticated()
                
                // Protected course content endpoints
                .requestMatchers("/api/courses/{id}/modules").authenticated()
                .requestMatchers("/api/courses/{id}/modules/**").authenticated()
                .requestMatchers("/api/courses/{id}/lessons").authenticated()
                .requestMatchers("/api/courses/{id}/lessons/**").authenticated()
                
                // Other protected endpoints
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/student/**").authenticated()
                .requestMatchers("/api/instructor/**").authenticated()
                .requestMatchers("/api/admin/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://skillup-website-swart.vercel.app",
            "https://skillup-admindashboard.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
