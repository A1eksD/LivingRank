package com.livingrank.service;

import com.livingrank.config.RateLimitConfig;
import com.livingrank.dto.*;
import com.livingrank.entity.AuthProvider;
import com.livingrank.entity.EmailVerificationToken;
import com.livingrank.entity.User;
import com.livingrank.exception.BadRequestException;
import com.livingrank.exception.RateLimitException;
import com.livingrank.repository.EmailVerificationTokenRepository;
import com.livingrank.repository.UserRepository;
import com.livingrank.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RateLimitConfig rateLimitConfig;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthService(UserRepository userRepository,
                       EmailVerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       EmailService emailService,
                       RateLimitConfig rateLimitConfig) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.rateLimitConfig = rateLimitConfig;
    }

    @Transactional
    public MessageResponse register(RegisterRequest request, String ipAddress) {
        checkRateLimit(ipAddress, "register");

        // Always return generic response to prevent email enumeration
        if (userRepository.existsByEmail(request.email())) {
            return new MessageResponse("Wenn die E-Mail-Adresse gültig ist, wurde eine Bestätigungsmail gesendet.");
        }

        User user = new User(
            request.email(),
            sanitizeText(request.displayName()),
            passwordEncoder.encode(request.password()),
            AuthProvider.LOCAL
        );
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
            user, token, LocalDateTime.now().plusHours(24)
        );
        tokenRepository.save(verificationToken);

        String verifyUrl = frontendUrl + "/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), user.getDisplayName(), verifyUrl);

        return new MessageResponse("Wenn die E-Mail-Adresse gültig ist, wurde eine Bestätigungsmail gesendet.");
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        checkRateLimit(ipAddress, "login");

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadRequestException("E-Mail oder Passwort ist falsch."));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new BadRequestException("E-Mail oder Passwort ist falsch.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("E-Mail oder Passwort ist falsch.");
        }

        if (!user.isEmailVerified()) {
            throw new BadRequestException("Bitte bestätigen Sie zuerst Ihre E-Mail-Adresse.");
        }

        if (user.getStatus() != com.livingrank.entity.UserStatus.ACTIVE) {
            throw new BadRequestException("Dieses Konto ist gesperrt oder deaktiviert.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, UserResponse.fromEntity(user));
    }

    @Transactional
    public MessageResponse verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByTokenAndUsedFalse(token)
            .orElseThrow(() -> new BadRequestException("Ungültiger oder abgelaufener Bestätigungslink."));

        if (verificationToken.isExpired()) {
            throw new BadRequestException("Der Bestätigungslink ist abgelaufen.");
        }

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return new MessageResponse("E-Mail-Adresse erfolgreich bestätigt. Sie können sich jetzt anmelden.");
    }

    private void checkRateLimit(String ipAddress, String action) {
        String key = action + ":" + ipAddress;
        if (!rateLimitConfig.resolveBucket(key).tryConsume(1)) {
            throw new RateLimitException("Zu viele Anfragen. Bitte versuchen Sie es später erneut.");
        }
    }

    private String sanitizeText(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "").trim();
    }
}
