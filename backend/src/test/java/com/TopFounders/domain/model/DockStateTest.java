package com.TopFounders.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockStateTest {

    @Test
    void testDockStateValues() {
        assertNotNull(DockState.EMPTY);
        assertNotNull(DockState.OCCUPIED);
        assertNotNull(DockState.OUT_OF_SERVICE);
    }

    @Test
    void testDockStateEnumValues() {
        DockState[] values = DockState.values();
        assertEquals(3, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(DockState.EMPTY));
        assertTrue(java.util.Arrays.asList(values).contains(DockState.OCCUPIED));
        assertTrue(java.util.Arrays.asList(values).contains(DockState.OUT_OF_SERVICE));
    }

    @Test
    void testDockStateValueOf() {
        assertEquals(DockState.EMPTY, DockState.valueOf("EMPTY"));
        assertEquals(DockState.OCCUPIED, DockState.valueOf("OCCUPIED"));
        assertEquals(DockState.OUT_OF_SERVICE, DockState.valueOf("OUT_OF_SERVICE"));
    }
}

