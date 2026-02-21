package com.livingrank.config;

import com.livingrank.security.AdminRateLimitFilter;
import com.livingrank.security.JwtAuthenticationFilter;
import com.livingrank.security.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AdminRateLimitFilter adminRateLimitFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          AdminRateLimitFilter adminRateLimitFilter,
                          OAuth2SuccessHandler oAuth2SuccessHandler,
                          @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.jwtFilter = jwtFilter;
        this.adminRateLimitFilter = adminRateLimitFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(cto -> {})
                .frameOptions(fo -> fo.deny())
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/streets/**").permitAll()

                // User endpoints
                .requestMatchers(HttpMethod.POST, "/api/streets/*/reviews").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/streets/*/reviews/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/streets/*/reviews/*").authenticated()
                .requestMatchers("/api/me/**").authenticated()

                // Admin endpoints – SUPER_ADMIN only
                .requestMatchers(HttpMethod.DELETE, "/api/admin/users/*").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/users/*/role").hasRole("SUPER_ADMIN")

                // Admin endpoints – ADMIN and SUPER_ADMIN
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(adminRateLimitFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
