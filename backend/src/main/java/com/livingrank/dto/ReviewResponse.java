package com.livingrank.dto;

import com.livingrank.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long streetId,
    Integer overallRating,
    Integer dampInHouse,
    Integer friendlyNeighbors,
    Integer houseCondition,
    Integer infrastructureConnections,
    Integer neighborsInGeneral,
    Integer neighborsVolume,
    Integer smellsBad,
    Integer thinWalls,
    Integer noiseFromStreet,
    Integer publicSafetyFeeling,
    Integer cleanlinessSharedAreas,
    Integer parkingSituation,
    Integer publicTransportAccess,
    Integer internetQuality,
    Integer pestIssues,
    Integer heatingReliability,
    Integer waterPressureOrQuality,
    Integer valueForMoney,
    String comment,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ReviewResponse fromEntity(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getStreet().getId(),
            review.getOverallRating(),
            review.getDampInHouse(),
            review.getFriendlyNeighbors(),
            review.getHouseCondition(),
            review.getInfrastructureConnections(),
            review.getNeighborsInGeneral(),
            review.getNeighborsVolume(),
            review.getSmellsBad(),
            review.getThinWalls(),
            review.getNoiseFromStreet(),
            review.getPublicSafetyFeeling(),
            review.getCleanlinessSharedAreas(),
            review.getParkingSituation(),
            review.getPublicTransportAccess(),
            review.getInternetQuality(),
            review.getPestIssues(),
            review.getHeatingReliability(),
            review.getWaterPressureOrQuality(),
            review.getValueForMoney(),
            review.getComment(),
            review.getCreatedAt(),
            review.getUpdatedAt()
        );
    }
}
