package com.livingrank.dto;

import com.livingrank.entity.User;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String displayName,
    String authProvider,
    boolean emailVerified,
    String profileImageUrl,
    String role,
    String status
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getAuthProvider().name(),
            user.isEmailVerified(),
            user.getProfileImageUrl(),
            user.getRole().name(),
            user.getStatus().name()
        );
    }
}
