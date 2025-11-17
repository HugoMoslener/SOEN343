package com.TopFounders.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationOperationalStateTest {

    @Test
    void testStationOperationalStateValues() {
        assertNotNull(StationOperationalState.ACTIVE);
        assertNotNull(StationOperationalState.OUT_OF_SERVICE);
    }

    @Test
    void testStationOperationalStateEnumValues() {
        StationOperationalState[] values = StationOperationalState.values();
        assertEquals(2, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(StationOperationalState.ACTIVE));
        assertTrue(java.util.Arrays.asList(values).contains(StationOperationalState.OUT_OF_SERVICE));
    }

    @Test
    void testStationOperationalStateValueOf() {
        assertEquals(StationOperationalState.ACTIVE, StationOperationalState.valueOf("ACTIVE"));
        assertEquals(StationOperationalState.OUT_OF_SERVICE, StationOperationalState.valueOf("OUT_OF_SERVICE"));
    }
}

