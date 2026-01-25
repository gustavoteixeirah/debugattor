package com.kodegt.debugattor.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Additional security configuration for the "private" REST API
//    @Bean
//    @Order(1)
//    SecurityFilterChain configurePrivateApi(HttpSecurity http) throws Exception {
//        return http
//                .securityMatcher("/api/private/**")
//                // Ignore CSRF for private API calls, typically used by other services, not browsers
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/private/**"))
//                .authorizeHttpRequests(auth -> {
//                    auth.anyRequest().authenticated();
//                })
//                // so session management/cookie is not needed
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // HttpStatusEntryPoint only sets status code, Location header to login page makes no sense here
//                .httpBasic(cfg -> cfg.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
//                .build();
//    }

    // Additional security configuration for the "public" REST API
//    @Order(2)
//    @Bean
//    SecurityFilterChain configurePublicApi(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/api/**")
//                // Ignore CSRF for public API calls, typically used by other services, not browsers
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/public/**"))
//                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
//        return http.build();
//    }

}
