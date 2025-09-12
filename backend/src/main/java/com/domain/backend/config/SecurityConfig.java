package com.domain.backend.config;


import com.domain.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ReactiveAuthenticationManager authenticationManager
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for stateless API
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Disable form login
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Disable HTTP Basic
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // No session management
                .authenticationManager(authenticationManager) // Set custom authentication manager
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll() // Allow public access to auth endpoints
                        .pathMatchers("/words/**").permitAll() // Allow public access to word lookup (adjust as needed)
                        .pathMatchers("/notifications/**").permitAll() // Allow SSE connection for notifications
                        .pathMatchers("/quizzes/**").permitAll()
                        .pathMatchers("/attempts/**").permitAll()
                        .pathMatchers("/users/**").authenticated()  // Allow public access to user registration and profile fetching
                        .pathMatchers("/decks/**").permitAll() // Allow public access to flashcard decks
                        .pathMatchers("/search/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN") // Example: Admin-only routes
                        .anyExchange().authenticated() // All other requests require authentication
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("http://localhost:5173")); // Adjust for your frontend URL
        corsConfig.setMaxAge(3600L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true); // Allow sending cookies/auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}
