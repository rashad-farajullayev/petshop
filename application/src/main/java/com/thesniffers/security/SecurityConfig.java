package com.thesniffers.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(
            @Value("${security.admin.token}") String adminToken,
            @Value("${security.tenant1.token}") String tenant1Token,
            @Value("${security.tenant2.token}") String tenant2Token) {
        return new TokenAuthenticationFilter(adminToken, tenant1Token, tenant2Token);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Allow HomeController (`/`) without authentication
                        .requestMatchers("/").permitAll()
                        // Admin can access everything
                        .requestMatchers("/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/shopping-baskets/**").hasAnyRole("ADMIN", "TENANT")
                        .requestMatchers("/api/v1/items/**").hasAnyRole("ADMIN", "TENANT")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
