package com.livingrank.dto;

public record StreetDetailResponse(
    StreetResponse street,
    CriteriaAverages criteriaAverages,
    boolean userHasReviewed
) {}
