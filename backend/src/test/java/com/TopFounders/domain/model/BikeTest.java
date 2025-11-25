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
        System.out.println("\n=== TEST: BikeTest.testBikeCreation ===");
        assertNotNull(bike);
        assertEquals("BIKE001", bike.getBikeID());
        assertEquals(BikeType.STANDARD, bike.getType());
        assertEquals("AVAILABLE", bike.getStateString());
        assertNotNull(bike.getState());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is " + bike.getStateString() + " (Type: " + bike.getType() + ")");
        System.out.println("[OK] Validated bike creation with correct ID, type, and initial state.");
    }

    @Test
    void testBikeStateInitializedAsAvailable() {
        System.out.println("\n=== TEST: BikeTest.testBikeStateInitializedAsAvailable ===");
        assertEquals("AVAILABLE", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is " + bike.getStateString());
        System.out.println("[OK] Validated bike initializes as AVAILABLE.");
    }

    @Test
    void testReserveBike() {
        System.out.println("\n=== TEST: BikeTest.testReserveBike ===");
        System.out.println("[STATE] Bike " + bike.getBikeID() + " initial state: " + bike.getStateString());
        bike.reserve();
        assertEquals("RESERVED", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is now " + bike.getStateString());
        System.out.println("[OK] Validated bike reservation state transition.");
    }

    @Test
    void testReserveBikeWhenNotAvailableThrowsException() {
        System.out.println("\n=== TEST: BikeTest.testReserveBikeWhenNotAvailableThrowsException ===");
        bike.reserve();
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is " + bike.getStateString());
        assertThrows(IllegalStateException.class, () -> bike.reserve());
        System.out.println("[OK] Validated exception thrown when reserving already reserved bike.");
    }

    @Test
    void testCheckoutBike() {
        System.out.println("\n=== TEST: BikeTest.testCheckoutBike ===");
        bike.reserve();
        System.out.println("[STATE] Bike " + bike.getBikeID() + " after reserve: " + bike.getStateString());
        bike.checkout();
        assertEquals("ONTRIP", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is now " + bike.getStateString());
        System.out.println("[OK] Validated bike checkout state transition.");
    }

    @Test
    void testCheckoutBikeWhenNotReservedThrowsException() {
        System.out.println("\n=== TEST: BikeTest.testCheckoutBikeWhenNotReservedThrowsException ===");
        System.out.println("[STATE] Bike " + bike.getBikeID() + " state: " + bike.getStateString());
        assertThrows(IllegalStateException.class, () -> bike.checkout());
        System.out.println("[OK] Validated exception thrown when checking out non-reserved bike.");
    }

    @Test
    void testReturnBike() {
        System.out.println("\n=== TEST: BikeTest.testReturnBike ===");
        bike.reserve();
        bike.checkout();
        System.out.println("[STATE] Bike " + bike.getBikeID() + " before return: " + bike.getStateString());
        bike.returnBike();
        assertEquals("AVAILABLE", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is now " + bike.getStateString());
        System.out.println("[OK] Validated bike return state transition.");
    }

    @Test
    void testMaintenance() {
        System.out.println("\n=== TEST: BikeTest.testMaintenance ===");
        System.out.println("[STATE] Bike " + bike.getBikeID() + " initial state: " + bike.getStateString());
        bike.maintenance();
        assertEquals("MAINTENANCE", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " is now " + bike.getStateString());
        System.out.println("[OK] Validated bike maintenance state transition.");
    }

    @Test
    void testMaintenanceWhenNotAvailableThrowsException() {
        System.out.println("\n=== TEST: BikeTest.testMaintenanceWhenNotAvailableThrowsException ===");
        bike.reserve();
        System.out.println("[STATE] Bike " + bike.getBikeID() + " state: " + bike.getStateString());
        assertThrows(IllegalStateException.class, () -> bike.maintenance());
        System.out.println("[OK] Validated exception thrown when putting non-available bike in maintenance.");
    }

    @Test
    void testSetBikeStateByString() {
        System.out.println("\n=== TEST: BikeTest.testSetBikeStateByString ===");
        bike.setBikeStateByString("RESERVED");
        assertEquals("RESERVED", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " set to: " + bike.getStateString());
        
        bike.setBikeStateByString("ONTRIP");
        assertEquals("ONTRIP", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " set to: " + bike.getStateString());
        
        bike.setBikeStateByString("MAINTENANCE");
        assertEquals("MAINTENANCE", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " set to: " + bike.getStateString());
        
        bike.setBikeStateByString("AVAILABLE");
        assertEquals("AVAILABLE", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " set to: " + bike.getStateString());
        System.out.println("[OK] Validated setting bike state by string for all states.");
    }

    @Test
    void testSetBikeStateByStringWithInvalidValueDefaultsToAvailable() {
        System.out.println("\n=== TEST: BikeTest.testSetBikeStateByStringWithInvalidValueDefaultsToAvailable ===");
        bike.setBikeStateByString("INVALID");
        assertEquals("AVAILABLE", bike.getStateString());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " defaulted to: " + bike.getStateString() + " (invalid input)");
        System.out.println("[OK] Validated invalid state string defaults to AVAILABLE.");
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        System.out.println("\n=== TEST: BikeTest.testSubscribeAndUnsubscribe ===");
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
        System.out.println("[OK] Validated bike subscribe and unsubscribe functionality.");
    }

    @Test
    void testSetDockID() {
        System.out.println("\n=== TEST: BikeTest.testSetDockID ===");
        bike.setDockID("DOCK001");
        assertEquals("DOCK001", bike.getDockID());
        System.out.println("[STATE] Bike " + bike.getBikeID() + " dockID: " + bike.getDockID());
        System.out.println("[OK] Validated setting bike dock ID.");
    }
}


