package com.livingrank.controller.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.AdminReviewResponse;
import com.livingrank.entity.User;
import com.livingrank.service.admin.AdminReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminReviewResponse>> getReviews(
            @RequestParam(required = false) Long streetId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Boolean visible,
            Pageable pageable) {
        return ResponseEntity.ok(adminReviewService.getReviews(streetId, userId, visible, pageable));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<AdminReviewResponse> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(adminReviewService.getReview(reviewId));
    }

    @PostMapping("/{reviewId}/hide")
    public ResponseEntity<MessageResponse> hideReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminReviewService.hideReview(reviewId, admin, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/{reviewId}/show")
    public ResponseEntity<MessageResponse> showReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminReviewService.showReview(reviewId, admin, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminReviewService.deleteReview(reviewId, admin, httpRequest.getRemoteAddr()));
    }
}
