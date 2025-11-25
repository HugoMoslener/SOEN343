package com.TopFounders.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EBikeTest {

    private EBike eBike;

    @BeforeEach
    void setUp() {
        eBike = new EBike("EBIKE001");
    }

    @Test
    void testEBikeCreation() {
        assertNotNull(eBike);
        assertEquals("EBIKE001", eBike.getBikeID());
        assertEquals(BikeType.E_BIKE, eBike.getType());
        assertEquals("AVAILABLE", eBike.getStateString());
    }

    @Test
    void testEBikeDefaultConstructor() {
        EBike newEBike = new EBike();
        assertNotNull(newEBike);
    }

    @Test
    void testEBikeInheritsBikeBehavior() {
        eBike.reserve();
        assertEquals("RESERVED", eBike.getStateString());
        
        eBike.checkout();
        assertEquals("ONTRIP", eBike.getStateString());
        
        eBike.returnBike();
        assertEquals("AVAILABLE", eBike.getStateString());
    }
}


