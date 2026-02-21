package com.livingrank.service;

import com.livingrank.config.RateLimitConfig;
import com.livingrank.dto.LoginRequest;
import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.RegisterRequest;
import com.livingrank.dto.AuthResponse;
import com.livingrank.entity.AuthProvider;
import com.livingrank.entity.EmailVerificationToken;
import com.livingrank.entity.User;
import com.livingrank.exception.BadRequestException;
import com.livingrank.exception.RateLimitException;
import com.livingrank.repository.EmailVerificationTokenRepository;
import com.livingrank.repository.UserRepository;
import com.livingrank.security.JwtTokenProvider;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private EmailVerificationTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private EmailService emailService;
    @Mock private RateLimitConfig rateLimitConfig;
    @Mock private Bucket bucket;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:4200");
        when(rateLimitConfig.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
    }

    @Test
    void register_newEmail_shouldReturnGenericMessage() {
        RegisterRequest request = new RegisterRequest("new@test.com", "Password123", "Test User");
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });
        when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponse response = authService.register(request, "127.0.0.1");

        assertNotNull(response);
        assertTrue(response.message().contains("Bestätigungsmail"));
        verify(emailService).sendVerificationEmail(eq("new@test.com"), anyString(), anyString());
    }

    @Test
    void register_existingEmail_shouldReturnSameGenericMessage() {
        RegisterRequest request = new RegisterRequest("existing@test.com", "Password123", "Test User");
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        MessageResponse response = authService.register(request, "127.0.0.1");

        assertNotNull(response);
        assertTrue(response.message().contains("Bestätigungsmail"));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void login_validCredentials_shouldReturnToken() {
        LoginRequest request = new LoginRequest("test@test.com", "Password123");
        User user = new User("test@test.com", "Test", "hashedPw", AuthProvider.LOCAL);
        user.setId(UUID.randomUUID());
        user.setEmailVerified(true);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashedPw")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("jwt-token");
        when(userRepository.save(any())).thenReturn(user);

        AuthResponse response = authService.login(request, "127.0.0.1");

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_wrongPassword_shouldThrowBadRequest() {
        LoginRequest request = new LoginRequest("test@test.com", "WrongPassword");
        User user = new User("test@test.com", "Test", "hashedPw", AuthProvider.LOCAL);
        user.setId(UUID.randomUUID());
        user.setEmailVerified(true);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPw")).thenReturn(false);

        assertThrows(BadRequestException.class, () ->
            authService.login(request, "127.0.0.1")
        );
    }

    @Test
    void login_emailNotVerified_shouldThrowBadRequest() {
        LoginRequest request = new LoginRequest("test@test.com", "Password123");
        User user = new User("test@test.com", "Test", "hashedPw", AuthProvider.LOCAL);
        user.setId(UUID.randomUUID());
        user.setEmailVerified(false);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashedPw")).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
            authService.login(request, "127.0.0.1")
        );
    }

    @Test
    void login_googleUser_shouldThrowBadRequest() {
        LoginRequest request = new LoginRequest("google@test.com", "Password123");
        User user = new User("google@test.com", "Google User", null, AuthProvider.GOOGLE);
        user.setId(UUID.randomUUID());
        user.setEmailVerified(true);

        when(userRepository.findByEmail("google@test.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () ->
            authService.login(request, "127.0.0.1")
        );
    }

    @Test
    void login_nonExistentEmail_shouldThrowBadRequest() {
        LoginRequest request = new LoginRequest("nonexistent@test.com", "Password123");
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () ->
            authService.login(request, "127.0.0.1")
        );
    }

    @Test
    void verifyEmail_validToken_shouldVerifyUser() {
        User user = new User("test@test.com", "Test", "hash", AuthProvider.LOCAL);
        user.setId(UUID.randomUUID());
        EmailVerificationToken token = new EmailVerificationToken(
            user, "valid-token", LocalDateTime.now().plusHours(24)
        );

        when(tokenRepository.findByTokenAndUsedFalse("valid-token")).thenReturn(Optional.of(token));
        when(tokenRepository.save(any())).thenReturn(token);
        when(userRepository.save(any())).thenReturn(user);

        MessageResponse response = authService.verifyEmail("valid-token");

        assertTrue(response.message().contains("bestätigt"));
        assertTrue(user.isEmailVerified());
        assertTrue(token.isUsed());
    }

    @Test
    void verifyEmail_expiredToken_shouldThrow() {
        User user = new User("test@test.com", "Test", "hash", AuthProvider.LOCAL);
        user.setId(UUID.randomUUID());
        EmailVerificationToken token = new EmailVerificationToken(
            user, "expired-token", LocalDateTime.now().minusHours(1)
        );

        when(tokenRepository.findByTokenAndUsedFalse("expired-token")).thenReturn(Optional.of(token));

        assertThrows(BadRequestException.class, () ->
            authService.verifyEmail("expired-token")
        );
    }

    @Test
    void verifyEmail_invalidToken_shouldThrow() {
        when(tokenRepository.findByTokenAndUsedFalse("invalid")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () ->
            authService.verifyEmail("invalid")
        );
    }

    @Test
    void register_rateLimited_shouldThrow() {
        when(bucket.tryConsume(1)).thenReturn(false);

        RegisterRequest request = new RegisterRequest("test@test.com", "Password123", "Test");

        assertThrows(RateLimitException.class, () ->
            authService.register(request, "127.0.0.1")
        );
    }

    @Test
    void login_rateLimited_shouldThrow() {
        when(bucket.tryConsume(1)).thenReturn(false);

        LoginRequest request = new LoginRequest("test@test.com", "Password123");

        assertThrows(RateLimitException.class, () ->
            authService.login(request, "127.0.0.1")
        );
    }
}
