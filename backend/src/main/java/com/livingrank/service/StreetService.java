package com.livingrank.service;

import com.livingrank.dto.*;
import com.livingrank.entity.Street;
import com.livingrank.entity.User;
import com.livingrank.exception.ResourceNotFoundException;
import com.livingrank.repository.ReviewRepository;
import com.livingrank.repository.StreetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StreetService {

    private final StreetRepository streetRepository;
    private final ReviewRepository reviewRepository;
    private final NominatimService nominatimService;

    public StreetService(StreetRepository streetRepository, ReviewRepository reviewRepository,
                         NominatimService nominatimService) {
        this.streetRepository = streetRepository;
        this.reviewRepository = reviewRepository;
        this.nominatimService = nominatimService;
    }

    @Transactional(readOnly = true)
    public List<StreetResponse> searchStreets(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        String trimmed = query.trim();

        // First search in DB cache
        List<Street> dbResults = streetRepository.searchByQuery(trimmed);

        // If not enough results, query Nominatim and cache
        if (dbResults.size() < 5) {
            List<Street> nominatimResults = nominatimService.searchAndCache(trimmed);
            // Re-query DB to include newly cached results
            dbResults = streetRepository.searchByQuery(trimmed);
        }

        return dbResults.stream()
            .limit(20)
            .map(street -> {
                Double avg = reviewRepository.getAverageOverallRating(street.getId());
                Long count = reviewRepository.getReviewCount(street.getId());
                return StreetResponse.fromEntity(street, avg, count != null ? count : 0L);
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public StreetDetailResponse getStreetDetail(Long streetId, User currentUser) {
        Street street = streetRepository.findById(streetId)
            .orElseThrow(() -> new ResourceNotFoundException("Straße nicht gefunden."));

        Double avg = reviewRepository.getAverageOverallRating(streetId);
        Long count = reviewRepository.getReviewCount(streetId);
        StreetResponse streetResponse = StreetResponse.fromEntity(street, avg, count != null ? count : 0L);

        List<Object[]> criteriaRows = reviewRepository.getCriteriaAverages(streetId);
        CriteriaAverages criteriaAverages = criteriaRows.isEmpty()
            ? CriteriaAverages.fromQueryResult(null)
            : CriteriaAverages.fromQueryResult(criteriaRows.get(0));

        boolean userHasReviewed = false;
        if (currentUser != null) {
            userHasReviewed = reviewRepository.existsByStreetIdAndUserId(streetId, currentUser.getId());
        }

        return new StreetDetailResponse(streetResponse, criteriaAverages, userHasReviewed);
    }
}
