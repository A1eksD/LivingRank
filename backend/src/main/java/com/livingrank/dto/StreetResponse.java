package com.livingrank.dto;

import com.livingrank.entity.Street;

public record StreetResponse(
    Long id,
    String streetName,
    String postalCode,
    String city,
    String stateRegion,
    String country,
    Double lat,
    Double lon,
    Double averageRating,
    Long reviewCount
) {
    public static StreetResponse fromEntity(Street street, Double averageRating, Long reviewCount) {
        return new StreetResponse(
            street.getId(),
            street.getStreetName(),
            street.getPostalCode(),
            street.getCity(),
            street.getStateRegion(),
            street.getCountry(),
            street.getLat(),
            street.getLon(),
            averageRating,
            reviewCount
        );
    }
}
