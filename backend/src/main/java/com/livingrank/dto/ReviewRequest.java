package com.livingrank.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
    @NotNull(message = "Gesamtbewertung ist erforderlich")
    @Min(value = 1, message = "Bewertung muss zwischen 1 und 5 liegen")
    @Max(value = 5, message = "Bewertung muss zwischen 1 und 5 liegen")
    Integer overallRating,

    @Min(1) @Max(5) Integer dampInHouse,
    @Min(1) @Max(5) Integer friendlyNeighbors,
    @Min(1) @Max(5) Integer houseCondition,
    @Min(1) @Max(5) Integer infrastructureConnections,
    @Min(1) @Max(5) Integer neighborsInGeneral,
    @Min(1) @Max(5) Integer neighborsVolume,
    @Min(1) @Max(5) Integer smellsBad,
    @Min(1) @Max(5) Integer thinWalls,
    @Min(1) @Max(5) Integer noiseFromStreet,
    @Min(1) @Max(5) Integer publicSafetyFeeling,
    @Min(1) @Max(5) Integer cleanlinessSharedAreas,
    @Min(1) @Max(5) Integer parkingSituation,
    @Min(1) @Max(5) Integer publicTransportAccess,
    @Min(1) @Max(5) Integer internetQuality,
    @Min(1) @Max(5) Integer pestIssues,
    @Min(1) @Max(5) Integer heatingReliability,
    @Min(1) @Max(5) Integer waterPressureOrQuality,
    @Min(1) @Max(5) Integer valueForMoney,

    @Size(max = 2000, message = "Kommentar darf maximal 2000 Zeichen lang sein")
    String comment
) {}
