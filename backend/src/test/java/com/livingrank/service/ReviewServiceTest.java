package com.livingrank.service;

import com.livingrank.dto.ReviewRequest;
import com.livingrank.dto.ReviewResponse;
import com.livingrank.entity.AuthProvider;
import com.livingrank.entity.Review;
import com.livingrank.entity.Street;
import com.livingrank.entity.User;
import com.livingrank.exception.ConflictException;
import com.livingrank.exception.ResourceNotFoundException;
import com.livingrank.repository.ReviewRepository;
import com.livingrank.repository.StreetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private StreetRepository streetRepository;

    @InjectMocks private ReviewService reviewService;

    private User testUser;
    private Street testStreet;

    @BeforeEach
    void setUp() {
        testUser = new User("test@test.com", "Test User", "hash", AuthProvider.LOCAL);
        testUser.setId(UUID.randomUUID());

        testStreet = new Street("Teststraße", "12345", "Berlin", "Berlin", "DE", 52.52, 13.405);
        testStreet.setId(1L);
    }

    @Test
    void createReview_validRequest_shouldSucceed() {
        ReviewRequest request = new ReviewRequest(4, 3, 4, 4, 3, 4, 3, 2, 3, 3, 4, 3, 3, 4, 3, 2, 4, 3, 4, "Gute Nachbarschaft");

        when(streetRepository.findById(1L)).thenReturn(Optional.of(testStreet));
        when(reviewRepository.existsByStreetIdAndUserId(1L, testUser.getId())).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            r.setId(1L);
            r.setCreatedAt(LocalDateTime.now());
            r.setUpdatedAt(LocalDateTime.now());
            return r;
        });

        ReviewResponse response = reviewService.createReview(1L, testUser, request);

        assertNotNull(response);
        assertEquals(4, response.overallRating());
        assertEquals("Gute Nachbarschaft", response.comment());
    }

    @Test
    void createReview_duplicateReview_shouldThrowConflict() {
        ReviewRequest request = new ReviewRequest(4, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        when(streetRepository.findById(1L)).thenReturn(Optional.of(testStreet));
        when(reviewRepository.existsByStreetIdAndUserId(1L, testUser.getId())).thenReturn(true);

        assertThrows(ConflictException.class, () ->
            reviewService.createReview(1L, testUser, request)
        );
    }

    @Test
    void createReview_nonExistentStreet_shouldThrowNotFound() {
        ReviewRequest request = new ReviewRequest(4, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        when(streetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            reviewService.createReview(999L, testUser, request)
        );
    }

    @Test
    void getReviewsForStreet_shouldReturnPagedResults() {
        Review review = new Review();
        review.setId(1L);
        review.setStreet(testStreet);
        review.setUser(testUser);
        review.setOverallRating(4);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Page<Review> page = new PageImpl<>(List.of(review));
        when(streetRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByStreetIdOrderByCreatedAtDesc(eq(1L), any())).thenReturn(page);

        Page<ReviewResponse> result = reviewService.getReviewsForStreet(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(4, result.getContent().get(0).overallRating());
    }

    @Test
    void getReviewsForStreet_nonExistentStreet_shouldThrow() {
        when(streetRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            reviewService.getReviewsForStreet(999L, PageRequest.of(0, 10))
        );
    }

    @Test
    void updateReview_ownReview_shouldSucceed() {
        ReviewRequest request = new ReviewRequest(5, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Updated");
        Review existing = new Review();
        existing.setId(1L);
        existing.setStreet(testStreet);
        existing.setUser(testUser);
        existing.setOverallRating(3);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        when(reviewRepository.findByIdAndUserId(1L, testUser.getId())).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        ReviewResponse response = reviewService.updateReview(1L, 1L, testUser, request);

        assertEquals(5, response.overallRating());
        assertEquals("Updated", response.comment());
    }

    @Test
    void updateReview_notOwnReview_shouldThrow() {
        ReviewRequest request = new ReviewRequest(5, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        when(reviewRepository.findByIdAndUserId(1L, testUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            reviewService.updateReview(1L, 1L, testUser, request)
        );
    }

    @Test
    void deleteReview_ownReview_shouldSucceed() {
        Review existing = new Review();
        existing.setId(1L);
        existing.setStreet(testStreet);
        existing.setUser(testUser);

        when(reviewRepository.findByIdAndUserId(1L, testUser.getId())).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> reviewService.deleteReview(1L, 1L, testUser));
        verify(reviewRepository).delete(existing);
    }

    @Test
    void deleteReview_wrongStreet_shouldThrow() {
        Street otherStreet = new Street("Andere Straße", "99999", "Hamburg", "Hamburg", "DE", 53.55, 9.99);
        otherStreet.setId(2L);

        Review existing = new Review();
        existing.setId(1L);
        existing.setStreet(otherStreet);
        existing.setUser(testUser);

        when(reviewRepository.findByIdAndUserId(1L, testUser.getId())).thenReturn(Optional.of(existing));

        assertThrows(ResourceNotFoundException.class, () ->
            reviewService.deleteReview(1L, 1L, testUser)
        );
    }

    @Test
    void getReviewsForUser_shouldReturnResults() {
        Review review = new Review();
        review.setId(1L);
        review.setStreet(testStreet);
        review.setUser(testUser);
        review.setOverallRating(3);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Page<Review> page = new PageImpl<>(List.of(review));
        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(eq(testUser.getId()), any())).thenReturn(page);

        Page<ReviewResponse> result = reviewService.getReviewsForUser(testUser.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }
}
