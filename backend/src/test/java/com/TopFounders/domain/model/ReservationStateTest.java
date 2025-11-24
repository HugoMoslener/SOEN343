package com.TopFounders.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationStateTest {

    @Test
    void testReservationStateValues() {
        assertNotNull(ReservationState.PENDING);
        assertNotNull(ReservationState.CONFIRMED);
        assertNotNull(ReservationState.CANCELLED);
    }

    @Test
    void testReservationStateEnumValues() {
        ReservationState[] values = ReservationState.values();
        assertEquals(3, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(ReservationState.PENDING));
        assertTrue(java.util.Arrays.asList(values).contains(ReservationState.CONFIRMED));
        assertTrue(java.util.Arrays.asList(values).contains(ReservationState.CANCELLED));
    }

    @Test
    void testReservationStateValueOf() {
        assertEquals(ReservationState.PENDING, ReservationState.valueOf("PENDING"));
        assertEquals(ReservationState.CONFIRMED, ReservationState.valueOf("CONFIRMED"));
        assertEquals(ReservationState.CANCELLED, ReservationState.valueOf("CANCELLED"));
    }
}


