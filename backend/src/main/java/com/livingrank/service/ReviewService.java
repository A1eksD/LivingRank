package com.livingrank.service;

import com.livingrank.dto.ReviewRequest;
import com.livingrank.dto.ReviewResponse;
import com.livingrank.entity.Review;
import com.livingrank.entity.Street;
import com.livingrank.entity.User;
import com.livingrank.exception.ConflictException;
import com.livingrank.exception.ResourceNotFoundException;
import com.livingrank.repository.ReviewRepository;
import com.livingrank.repository.StreetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StreetRepository streetRepository;

    public ReviewService(ReviewRepository reviewRepository, StreetRepository streetRepository) {
        this.reviewRepository = reviewRepository;
        this.streetRepository = streetRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForStreet(Long streetId, Pageable pageable) {
        if (!streetRepository.existsById(streetId)) {
            throw new ResourceNotFoundException("Straße nicht gefunden.");
        }
        return reviewRepository.findByStreetIdOrderByCreatedAtDesc(streetId, pageable)
            .map(ReviewResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForUser(UUID userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(ReviewResponse::fromEntity);
    }

    @Transactional
    public ReviewResponse createReview(Long streetId, User user, ReviewRequest request) {
        Street street = streetRepository.findById(streetId)
            .orElseThrow(() -> new ResourceNotFoundException("Straße nicht gefunden."));

        if (reviewRepository.existsByStreetIdAndUserId(streetId, user.getId())) {
            throw new ConflictException("Sie haben diese Straße bereits bewertet.");
        }

        Review review = new Review();
        review.setStreet(street);
        review.setUser(user);
        review.setOverallRating(request.overallRating());
        review.setDampInHouse(request.dampInHouse());
        review.setFriendlyNeighbors(request.friendlyNeighbors());
        review.setHouseCondition(request.houseCondition());
        review.setInfrastructureConnections(request.infrastructureConnections());
        review.setNeighborsInGeneral(request.neighborsInGeneral());
        review.setNeighborsVolume(request.neighborsVolume());
        review.setSmellsBad(request.smellsBad());
        review.setThinWalls(request.thinWalls());
        review.setNoiseFromStreet(request.noiseFromStreet());
        review.setPublicSafetyFeeling(request.publicSafetyFeeling());
        review.setCleanlinessSharedAreas(request.cleanlinessSharedAreas());
        review.setParkingSituation(request.parkingSituation());
        review.setPublicTransportAccess(request.publicTransportAccess());
        review.setInternetQuality(request.internetQuality());
        review.setPestIssues(request.pestIssues());
        review.setHeatingReliability(request.heatingReliability());
        review.setWaterPressureOrQuality(request.waterPressureOrQuality());
        review.setValueForMoney(request.valueForMoney());
        review.setComment(sanitizeComment(request.comment()));

        Review saved = reviewRepository.save(review);
        return ReviewResponse.fromEntity(saved);
    }

    @Transactional
    public ReviewResponse updateReview(Long streetId, Long reviewId, User user, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Bewertung nicht gefunden."));

        if (!review.getStreet().getId().equals(streetId)) {
            throw new ResourceNotFoundException("Bewertung nicht gefunden.");
        }

        review.setOverallRating(request.overallRating());
        review.setDampInHouse(request.dampInHouse());
        review.setFriendlyNeighbors(request.friendlyNeighbors());
        review.setHouseCondition(request.houseCondition());
        review.setInfrastructureConnections(request.infrastructureConnections());
        review.setNeighborsInGeneral(request.neighborsInGeneral());
        review.setNeighborsVolume(request.neighborsVolume());
        review.setSmellsBad(request.smellsBad());
        review.setThinWalls(request.thinWalls());
        review.setNoiseFromStreet(request.noiseFromStreet());
        review.setPublicSafetyFeeling(request.publicSafetyFeeling());
        review.setCleanlinessSharedAreas(request.cleanlinessSharedAreas());
        review.setParkingSituation(request.parkingSituation());
        review.setPublicTransportAccess(request.publicTransportAccess());
        review.setInternetQuality(request.internetQuality());
        review.setPestIssues(request.pestIssues());
        review.setHeatingReliability(request.heatingReliability());
        review.setWaterPressureOrQuality(request.waterPressureOrQuality());
        review.setValueForMoney(request.valueForMoney());
        review.setComment(sanitizeComment(request.comment()));

        Review saved = reviewRepository.save(review);
        return ReviewResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteReview(Long streetId, Long reviewId, User user) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Bewertung nicht gefunden."));

        if (!review.getStreet().getId().equals(streetId)) {
            throw new ResourceNotFoundException("Bewertung nicht gefunden.");
        }

        reviewRepository.delete(review);
    }

    private String sanitizeComment(String comment) {
        if (comment == null) return null;
        // Strip HTML tags to prevent XSS
        return comment.replaceAll("<[^>]*>", "").trim();
    }
}
