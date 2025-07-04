package com.dhiram.ecom_pro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dhiram.ecom_pro.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    private static final String[] PERMIT_ALL_PATHS = {
            // USER ALL PATH
            "/api/users/login",
            "/api/users/register",
            "/api/users/forgot-password",
            "/api/users/reset-password",
            // ADMIN ALL PATH
            "/api/admin/login",
            // PUBLIC ALL PATH
            "/api/public/**",
            // BUYER-USER ALL PATH
            "/api/buyer-user/varify-login-otp",
            "/api/buyer-user/login",
            "/api/buyer-user/registration",
            "/api/buyer-user/resend-login-otp",
            "/api/buyer-user/forgot-password",
            "/api/buyer-user/reset-password",
            // SWAGGER DOCS
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                        .requestMatchers("/api/admin/**", "/api/users").hasRole("ADMIN")
                        .requestMatchers("/api/products/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/api/buyer-user/**").hasAnyRole("BUYER")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authException) -> {
                            // Only handle actual authentication failures
                            if (!req.getRequestURI().startsWith("/api/auth")) {
                                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                res.setContentType("application/json");
                                res.getWriter().write("{\"message\": \"Authentication required\", \"status\": 401}");
                            }
                        })
                        .accessDeniedHandler((req, res, accessDeniedException) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"message\": \"Insufficient permissions\", \"status\": 403}");
                        }))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}