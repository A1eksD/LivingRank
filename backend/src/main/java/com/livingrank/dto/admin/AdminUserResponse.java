package com.livingrank.dto.admin;

import com.livingrank.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserResponse(
    UUID id,
    String email,
    String displayName,
    String authProvider,
    boolean emailVerified,
    String profileImageUrl,
    String role,
    String status,
    LocalDateTime suspendedAt,
    String suspendedReason,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static AdminUserResponse fromEntity(User user) {
        return new AdminUserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getAuthProvider().name(),
            user.isEmailVerified(),
            user.getProfileImageUrl(),
            user.getRole().name(),
            user.getStatus().name(),
            user.getSuspendedAt(),
            user.getSuspendedReason(),
            user.getLastLoginAt(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
