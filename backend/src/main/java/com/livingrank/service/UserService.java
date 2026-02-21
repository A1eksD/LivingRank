package com.livingrank.service;

import com.livingrank.dto.UpdateProfileRequest;
import com.livingrank.dto.UserResponse;
import com.livingrank.entity.User;
import com.livingrank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(User user) {
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        if (request.displayName() != null && !request.displayName().isBlank()) {
            user.setDisplayName(sanitizeText(request.displayName()));
        }
        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl());
        }
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    private String sanitizeText(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "").trim();
    }
}
