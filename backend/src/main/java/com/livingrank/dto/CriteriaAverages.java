package com.livingrank.dto;

public record CriteriaAverages(
    Double dampInHouse,
    Double friendlyNeighbors,
    Double houseCondition,
    Double infrastructureConnections,
    Double neighborsInGeneral,
    Double neighborsVolume,
    Double smellsBad,
    Double thinWalls,
    Double noiseFromStreet,
    Double publicSafetyFeeling,
    Double cleanlinessSharedAreas,
    Double parkingSituation,
    Double publicTransportAccess,
    Double internetQuality,
    Double pestIssues,
    Double heatingReliability,
    Double waterPressureOrQuality,
    Double valueForMoney
) {
    public static CriteriaAverages fromQueryResult(Object[] row) {
        if (row == null || row.length == 0 || row[0] == null) {
            return new CriteriaAverages(null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return new CriteriaAverages(
            toDouble(row[0]), toDouble(row[1]), toDouble(row[2]),
            toDouble(row[3]), toDouble(row[4]), toDouble(row[5]),
            toDouble(row[6]), toDouble(row[7]), toDouble(row[8]),
            toDouble(row[9]), toDouble(row[10]), toDouble(row[11]),
            toDouble(row[12]), toDouble(row[13]), toDouble(row[14]),
            toDouble(row[15]), toDouble(row[16]), toDouble(row[17])
        );
    }

    private static Double toDouble(Object value) {
        return value != null ? ((Number) value).doubleValue() : null;
    }
}
