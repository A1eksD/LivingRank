package com.livingrank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingrank.dto.AuthResponse;
import com.livingrank.dto.LoginRequest;
import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.RegisterRequest;
import com.livingrank.dto.UserResponse;
import com.livingrank.exception.BadRequestException;
import com.livingrank.security.JwtAuthenticationFilter;
import com.livingrank.security.JwtTokenProvider;
import com.livingrank.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void register_validRequest_shouldReturn200() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.com", "Password123", "Test User");
        when(authService.register(any(), anyString())).thenReturn(
            new MessageResponse("Wenn die E-Mail-Adresse gültig ist, wurde eine Bestätigungsmail gesendet.")
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_invalidEmail_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest("invalid", "Password123", "Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_shortPassword_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.com", "short", "Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_blankName_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.com", "Password123", "");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_validCredentials_shouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "Password123");
        UserResponse userResp = new UserResponse(UUID.randomUUID(), "test@test.com", "Test", "LOCAL", true, null);
        when(authService.login(any(), anyString())).thenReturn(new AuthResponse("jwt-token", userResp));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_badCredentials_shouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "WrongPw");
        when(authService.login(any(), anyString())).thenThrow(
            new BadRequestException("E-Mail oder Passwort ist falsch.")
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmail_validToken_shouldReturn200() throws Exception {
        when(authService.verifyEmail("valid-token")).thenReturn(
            new MessageResponse("E-Mail-Adresse erfolgreich bestätigt.")
        );

        mockMvc.perform(get("/api/auth/verify-email").param("token", "valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("E-Mail-Adresse erfolgreich bestätigt."));
    }
}
