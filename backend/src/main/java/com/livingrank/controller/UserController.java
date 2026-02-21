package com.livingrank.controller;

import com.livingrank.dto.ReviewResponse;
import com.livingrank.dto.UpdateProfileRequest;
import com.livingrank.dto.UserResponse;
import com.livingrank.entity.User;
import com.livingrank.service.ReviewService;
import com.livingrank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;

    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(reviewService.getReviewsForUser(user.getId(), pageable));
    }
}
