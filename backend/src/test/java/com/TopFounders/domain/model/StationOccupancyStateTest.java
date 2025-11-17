package com.TopFounders.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationOccupancyStateTest {

    @Test
    void testStationOccupancyStateValues() {
        assertNotNull(StationOccupancyState.EMPTY);
        assertNotNull(StationOccupancyState.OCCUPIED);
        assertNotNull(StationOccupancyState.FULL);
        assertNotNull(StationOccupancyState.OUT_OF_SERVICE);
    }

    @Test
    void testStationOccupancyStateEnumValues() {
        StationOccupancyState[] values = StationOccupancyState.values();
        assertEquals(4, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(StationOccupancyState.EMPTY));
        assertTrue(java.util.Arrays.asList(values).contains(StationOccupancyState.OCCUPIED));
        assertTrue(java.util.Arrays.asList(values).contains(StationOccupancyState.FULL));
        assertTrue(java.util.Arrays.asList(values).contains(StationOccupancyState.OUT_OF_SERVICE));
    }

    @Test
    void testStationOccupancyStateValueOf() {
        assertEquals(StationOccupancyState.EMPTY, StationOccupancyState.valueOf("EMPTY"));
        assertEquals(StationOccupancyState.OCCUPIED, StationOccupancyState.valueOf("OCCUPIED"));
        assertEquals(StationOccupancyState.FULL, StationOccupancyState.valueOf("FULL"));
        assertEquals(StationOccupancyState.OUT_OF_SERVICE, StationOccupancyState.valueOf("OUT_OF_SERVICE"));
    }
}

