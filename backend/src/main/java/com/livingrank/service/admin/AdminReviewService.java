package com.livingrank.service.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.AdminReviewResponse;
import com.livingrank.entity.Review;
import com.livingrank.entity.User;
import com.livingrank.exception.BadRequestException;
import com.livingrank.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final AdminAuditService auditService;

    public AdminReviewService(ReviewRepository reviewRepository, AdminAuditService auditService) {
        this.reviewRepository = reviewRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<AdminReviewResponse> getReviews(Long streetId, UUID userId, Boolean visible, Pageable pageable) {
        if (streetId != null && visible != null) {
            return reviewRepository.findByStreetIdAndVisible(streetId, visible, pageable)
                    .map(AdminReviewResponse::fromEntity);
        }
        if (streetId != null) {
            return reviewRepository.findByStreetIdOrderByCreatedAtDesc(streetId, pageable)
                    .map(AdminReviewResponse::fromEntity);
        }
        if (userId != null) {
            return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                    .map(AdminReviewResponse::fromEntity);
        }
        if (visible != null) {
            return reviewRepository.findByVisible(visible, pageable)
                    .map(AdminReviewResponse::fromEntity);
        }
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AdminReviewResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public AdminReviewResponse getReview(Long reviewId) {
        Review review = findOrThrow(reviewId);
        return AdminReviewResponse.fromEntity(review);
    }

    @Transactional
    public MessageResponse hideReview(Long reviewId, User admin, String ip) {
        Review review = findOrThrow(reviewId);
        review.setVisible(false);
        reviewRepository.save(review);

        auditService.log(admin, "REVIEW_HIDDEN", "REVIEW", reviewId.toString(),
                Map.of("streetId", review.getStreet().getId(), "userId", review.getUser().getId().toString()), ip);

        return new MessageResponse("Review wurde ausgeblendet.");
    }

    @Transactional
    public MessageResponse showReview(Long reviewId, User admin, String ip) {
        Review review = findOrThrow(reviewId);
        review.setVisible(true);
        reviewRepository.save(review);

        auditService.log(admin, "REVIEW_SHOWN", "REVIEW", reviewId.toString(), null, ip);

        return new MessageResponse("Review wurde eingeblendet.");
    }

    @Transactional
    public MessageResponse deleteReview(Long reviewId, User admin, String ip) {
        Review review = findOrThrow(reviewId);

        auditService.log(admin, "REVIEW_DELETED", "REVIEW", reviewId.toString(),
                Map.of("streetId", review.getStreet().getId(),
                       "userId", review.getUser().getId().toString(),
                       "overallRating", review.getOverallRating(),
                       "comment", review.getComment() != null ? review.getComment() : ""), ip);

        reviewRepository.delete(review);

        return new MessageResponse("Review wurde gelöscht.");
    }

    @Transactional(readOnly = true)
    public long countHiddenReviews() {
        return reviewRepository.countByVisible(false);
    }

    @Transactional(readOnly = true)
    public long countTotalReviews() {
        return reviewRepository.count();
    }

    private Review findOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException("Review nicht gefunden."));
    }
}
