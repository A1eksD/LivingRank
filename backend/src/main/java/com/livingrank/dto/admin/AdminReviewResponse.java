package com.livingrank.dto.admin;

import com.livingrank.entity.Review;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminReviewResponse(
    Long id,
    Long streetId,
    String streetName,
    String streetCity,
    UUID userId,
    String userEmail,
    String userDisplayName,
    Integer overallRating,
    String comment,
    boolean visible,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static AdminReviewResponse fromEntity(Review review) {
        return new AdminReviewResponse(
            review.getId(),
            review.getStreet().getId(),
            review.getStreet().getStreetName(),
            review.getStreet().getCity(),
            review.getUser().getId(),
            review.getUser().getEmail(),
            review.getUser().getDisplayName(),
            review.getOverallRating(),
            review.getComment(),
            review.isVisible(),
            review.getCreatedAt(),
            review.getUpdatedAt()
        );
    }
}
