package com.election.backendjava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Global CORS configuration — allows frontend access.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://oege.ie.hva.nl:8888"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeHttpRequests(authz -> authz

                        // Allow CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public auth endpoints
                        .requestMatchers("/auth/**").permitAll()

                        // FIX: Email change confirm moet PUBLIC zijn (anders 403)
                        .requestMatchers(HttpMethod.GET, "/api/user/confirm-email-change").permitAll()

                        // Public election pages
                        .requestMatchers("/electionresults/**").permitAll()

                        // likes endpoints
                        .requestMatchers(HttpMethod.POST, "/api/topics/*/likes").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/topics/*/likes").authenticated()

                        // Forum endpoints
                        .requestMatchers(HttpMethod.GET, "/api/topics/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/topics").authenticated()

                        // User endpoints
                        .requestMatchers(HttpMethod.GET, "/api/user/persona/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/profile/by-username/**").permitAll()
                        .requestMatchers("/api/user/**").authenticated()

                        // Admin endpoints
                        .requestMatchers(HttpMethod.POST, "/api/admin/reported-users").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")


                        .requestMatchers("/api/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/comments/topic/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/profile/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/profile/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/comments/*").permitAll()

                        // Default rule
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
