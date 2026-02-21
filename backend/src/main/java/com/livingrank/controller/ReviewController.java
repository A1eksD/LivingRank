package com.livingrank.controller;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.ReviewRequest;
import com.livingrank.dto.ReviewResponse;
import com.livingrank.entity.User;
import com.livingrank.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/streets/{streetId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long streetId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviewsForStreet(streetId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long streetId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ReviewResponse review = reviewService.createReview(streetId, user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long streetId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ReviewResponse review = reviewService.updateReview(streetId, reviewId, user, request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(
            @PathVariable Long streetId,
            @PathVariable Long reviewId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        reviewService.deleteReview(streetId, reviewId, user);
        return ResponseEntity.ok(new MessageResponse("Bewertung erfolgreich gelöscht."));
    }
}
