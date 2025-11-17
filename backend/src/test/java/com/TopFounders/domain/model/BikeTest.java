package com.TopFounders.domain.model;

import com.TopFounders.domain.observer.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BikeTest {

    private Bike bike;

    @BeforeEach
    void setUp() {
        bike = new Bike("BIKE001", BikeType.STANDARD);
    }

    @Test
    void testBikeCreation() {
        assertNotNull(bike);
        assertEquals("BIKE001", bike.getBikeID());
        assertEquals(BikeType.STANDARD, bike.getType());
        assertEquals("AVAILABLE", bike.getStateString());
        assertNotNull(bike.getState());
    }

    @Test
    void testBikeStateInitializedAsAvailable() {
        assertEquals("AVAILABLE", bike.getStateString());
    }

    @Test
    void testReserveBike() {
        bike.reserve();
        assertEquals("RESERVED", bike.getStateString());
    }

    @Test
    void testReserveBikeWhenNotAvailableThrowsException() {
        bike.reserve();
        assertThrows(IllegalStateException.class, () -> bike.reserve());
    }

    @Test
    void testCheckoutBike() {
        bike.reserve();
        bike.checkout();
        assertEquals("ONTRIP", bike.getStateString());
    }

    @Test
    void testCheckoutBikeWhenNotReservedThrowsException() {
        assertThrows(IllegalStateException.class, () -> bike.checkout());
    }

    @Test
    void testReturnBike() {
        bike.reserve();
        bike.checkout();
        bike.returnBike();
        assertEquals("AVAILABLE", bike.getStateString());
    }

    @Test
    void testMaintenance() {
        bike.maintenance();
        assertEquals("MAINTENANCE", bike.getStateString());
    }

    @Test
    void testMaintenanceWhenNotAvailableThrowsException() {
        bike.reserve();
        assertThrows(IllegalStateException.class, () -> bike.maintenance());
    }

    @Test
    void testSetBikeStateByString() {
        bike.setBikeStateByString("RESERVED");
        assertEquals("RESERVED", bike.getStateString());
        
        bike.setBikeStateByString("ONTRIP");
        assertEquals("ONTRIP", bike.getStateString());
        
        bike.setBikeStateByString("MAINTENANCE");
        assertEquals("MAINTENANCE", bike.getStateString());
        
        bike.setBikeStateByString("AVAILABLE");
        assertEquals("AVAILABLE", bike.getStateString());
    }

    @Test
    void testSetBikeStateByStringWithInvalidValueDefaultsToAvailable() {
        bike.setBikeStateByString("INVALID");
        assertEquals("AVAILABLE", bike.getStateString());
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        Subscriber testSubscriber = new Subscriber() {
            @Override
            public void update(String eventType, Object object) {
                // Test subscriber implementation
            }
        };
        
        bike.subscribe(testSubscriber);
        bike.unsubscribe(testSubscriber);
        // If no exception is thrown, the test passes
        assertTrue(true);
    }

    @Test
    void testSetDockID() {
        bike.setDockID("DOCK001");
        assertEquals("DOCK001", bike.getDockID());
    }
}

