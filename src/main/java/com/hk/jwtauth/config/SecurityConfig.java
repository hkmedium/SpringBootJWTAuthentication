package com.hk.jwtauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hk.jwtauth.exception.ErrorResponse;
import com.hk.jwtauth.filter.JwtTokenFilter;
import com.hk.jwtauth.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    private static final String[] WHITELIST_URLS = {"/api/auth/login/**","/api/auth/users", "/api/auth/access-token"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req.requestMatchers(WHITELIST_URLS).permitAll() // Allow access without authentication
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .anyRequest().authenticated() // All other requests require authentication
                )
                .userDetailsService(userDetailsServiceImp)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        e -> e.accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {
                                            ErrorResponse errorResponse = new ErrorResponse(
                                                    System.currentTimeMillis(),
                                                    accessDeniedException.getLocalizedMessage(),
                                                    request.getRequestURI(),
                                                    HttpStatus.FORBIDDEN,
                                                    HttpStatus.FORBIDDEN.value());

                                            response.setStatus(HttpStatus.FORBIDDEN.value());
                                            response.setContentType("application/json");
                                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                                        }
                                )
                                .authenticationEntryPoint((request, response, authException) -> {

                                    ErrorResponse errorResponse = new ErrorResponse(
                                            System.currentTimeMillis(),
                                            authException.getLocalizedMessage(),
                                            request.getRequestURI(),
                                            HttpStatus.UNAUTHORIZED,
                                            HttpStatus.UNAUTHORIZED.value());

                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType("application/json");
                                    response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                                }));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



