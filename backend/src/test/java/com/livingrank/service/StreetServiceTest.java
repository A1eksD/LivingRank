package com.livingrank.service;

import com.livingrank.dto.StreetDetailResponse;
import com.livingrank.dto.StreetResponse;
import com.livingrank.entity.AuthProvider;
import com.livingrank.entity.Street;
import com.livingrank.entity.User;
import com.livingrank.exception.ResourceNotFoundException;
import com.livingrank.repository.ReviewRepository;
import com.livingrank.repository.StreetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreetServiceTest {

    @Mock private StreetRepository streetRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private NominatimService nominatimService;

    @InjectMocks private StreetService streetService;

    @Test
    void searchStreets_shortQuery_shouldReturnEmpty() {
        List<StreetResponse> results = streetService.searchStreets("a");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchStreets_nullQuery_shouldReturnEmpty() {
        List<StreetResponse> results = streetService.searchStreets(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void searchStreets_validQuery_shouldReturnResults() {
        Street street = new Street("Berliner Straße", "10115", "Berlin", "Berlin", "DE", 52.52, 13.405);
        street.setId(1L);

        when(streetRepository.searchByQuery("Berlin")).thenReturn(List.of(street));
        when(reviewRepository.getAverageOverallRating(1L)).thenReturn(4.2);
        when(reviewRepository.getReviewCount(1L)).thenReturn(5L);

        List<StreetResponse> results = streetService.searchStreets("Berlin");

        assertEquals(1, results.size());
        assertEquals("Berliner Straße", results.get(0).streetName());
        assertEquals(4.2, results.get(0).averageRating());
        assertEquals(5L, results.get(0).reviewCount());
    }

    @Test
    void searchStreets_fewDbResults_shouldQueryNominatim() {
        when(streetRepository.searchByQuery("Unbekannt")).thenReturn(List.of());
        when(nominatimService.searchAndCache("Unbekannt")).thenReturn(List.of());

        List<StreetResponse> results = streetService.searchStreets("Unbekannt");

        verify(nominatimService).searchAndCache("Unbekannt");
    }

    @Test
    void getStreetDetail_existingStreet_shouldReturnDetail() {
        Street street = new Street("Teststraße", "12345", "München", "Bayern", "DE", 48.13, 11.58);
        street.setId(1L);

        when(streetRepository.findById(1L)).thenReturn(Optional.of(street));
        when(reviewRepository.getAverageOverallRating(1L)).thenReturn(3.8);
        when(reviewRepository.getReviewCount(1L)).thenReturn(10L);
        when(reviewRepository.getCriteriaAverages(1L)).thenReturn(List.of());

        StreetDetailResponse detail = streetService.getStreetDetail(1L, null);

        assertNotNull(detail);
        assertEquals("Teststraße", detail.street().streetName());
        assertFalse(detail.userHasReviewed());
    }

    @Test
    void getStreetDetail_withAuthUser_shouldCheckReviewStatus() {
        User user = new User("test@test.com", "Test", "hash", AuthProvider.LOCAL);
        user.setId(UUID.randomUUID());

        Street street = new Street("Teststraße", "12345", "München", "Bayern", "DE", 48.13, 11.58);
        street.setId(1L);

        when(streetRepository.findById(1L)).thenReturn(Optional.of(street));
        when(reviewRepository.getAverageOverallRating(1L)).thenReturn(null);
        when(reviewRepository.getReviewCount(1L)).thenReturn(0L);
        when(reviewRepository.getCriteriaAverages(1L)).thenReturn(List.of());
        when(reviewRepository.existsByStreetIdAndUserId(1L, user.getId())).thenReturn(true);

        StreetDetailResponse detail = streetService.getStreetDetail(1L, user);

        assertTrue(detail.userHasReviewed());
    }

    @Test
    void getStreetDetail_nonExistentStreet_shouldThrow() {
        when(streetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            streetService.getStreetDetail(999L, null)
        );
    }
}
