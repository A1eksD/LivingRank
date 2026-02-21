package com.livingrank.security;

import com.livingrank.entity.AuthProvider;
import com.livingrank.entity.User;
import com.livingrank.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtTokenProvider tokenProvider,
                                @Value("${app.frontend-url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User(email, name != null ? name : "User", null, AuthProvider.GOOGLE);
            newUser.setEmailVerified(true);
            newUser.setProfileImageUrl(picture);
            return userRepository.save(newUser);
        });

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        String redirectUrl = frontendUrl + "/oauth2/callback?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
