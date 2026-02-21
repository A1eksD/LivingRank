package com.livingrank.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(
            "test-secret-key-that-is-at-least-256-bits-long-for-hs256-testing",
            86400000L
        );
    }

    @Test
    void shouldGenerateValidToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenProvider.generateToken(userId, "test@email.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenProvider.generateToken(userId, "test@email.com");

        UUID extracted = tokenProvider.getUserIdFromToken(token);
        assertEquals(userId, extracted);
    }

    @Test
    void shouldValidateValidToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenProvider.generateToken(userId, "test@email.com");

        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid-token"));
    }

    @Test
    void shouldRejectNullToken() {
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    void shouldRejectEmptyToken() {
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    void shouldRejectTamperedToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenProvider.generateToken(userId, "test@email.com");
        String tampered = token + "tampered";

        assertFalse(tokenProvider.validateToken(tampered));
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = tokenProvider.generateToken(UUID.randomUUID(), "user1@email.com");
        String token2 = tokenProvider.generateToken(UUID.randomUUID(), "user2@email.com");

        assertNotEquals(token1, token2);
    }
}
