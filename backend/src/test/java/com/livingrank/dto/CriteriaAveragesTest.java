package com.livingrank.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CriteriaAveragesTest {

    @Test
    void fromQueryResult_nullInput_shouldReturnAllNulls() {
        CriteriaAverages averages = CriteriaAverages.fromQueryResult(null);

        assertNotNull(averages);
        assertNull(averages.dampInHouse());
        assertNull(averages.friendlyNeighbors());
        assertNull(averages.valueForMoney());
    }

    @Test
    void fromQueryResult_emptyArray_shouldReturnAllNulls() {
        CriteriaAverages averages = CriteriaAverages.fromQueryResult(new Object[0]);

        assertNotNull(averages);
        assertNull(averages.dampInHouse());
    }

    @Test
    void fromQueryResult_validData_shouldParseCorrectly() {
        Object[] row = new Object[18];
        for (int i = 0; i < 18; i++) {
            row[i] = 3.5;
        }

        CriteriaAverages averages = CriteriaAverages.fromQueryResult(row);

        assertEquals(3.5, averages.dampInHouse());
        assertEquals(3.5, averages.friendlyNeighbors());
        assertEquals(3.5, averages.valueForMoney());
    }

    @Test
    void fromQueryResult_mixedNulls_shouldHandleGracefully() {
        Object[] row = new Object[18];
        row[0] = 4.0;
        row[1] = null;
        row[2] = 3.0;

        CriteriaAverages averages = CriteriaAverages.fromQueryResult(row);

        assertEquals(4.0, averages.dampInHouse());
        assertNull(averages.friendlyNeighbors());
        assertEquals(3.0, averages.houseCondition());
    }
}
