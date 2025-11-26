package com.TopFounders.domain.model;

import com.TopFounders.domain.observer.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockTest {

    private Dock dock;
    private Bike bike;

    @BeforeEach
    void setUp() {
        dock = new Dock("DOCK001", "STATION001");
        bike = new Bike("BIKE001", BikeType.STANDARD);
    }

    @Test
    void testDockCreation() {
        assertNotNull(dock);
        assertEquals("DOCK001", dock.getDockID());
        assertEquals("STATION001", dock.getStationID());
        assertEquals(DockState.EMPTY, dock.getState());
        assertNull(dock.getBike());
    }

    @Test
    void testDockDefaultConstructor() {
        Dock newDock = new Dock();
        assertNotNull(newDock);
    }

    @Test
    void testOccupyDock() {
        dock.occupy(bike);
        assertEquals(DockState.OCCUPIED, dock.getState());
        assertEquals(bike, dock.getBike());
    }

    @Test
    void testOccupyDockWhenNotEmptyThrowsException() {
        dock.occupy(bike);
        Bike anotherBike = new Bike("BIKE002", BikeType.STANDARD);
        assertThrows(IllegalStateException.class, () -> dock.occupy(anotherBike));
    }

    @Test
    void testReleaseBike() {
        dock.occupy(bike);
        Bike releasedBike = dock.release();
        assertEquals(bike, releasedBike);
        assertEquals(DockState.EMPTY, dock.getState());
        assertNull(dock.getBike());
    }

    @Test
    void testReleaseBikeWhenNotOccupiedThrowsException() {
        assertThrows(IllegalStateException.class, () -> dock.release());
    }

    @Test
    void testSetOutOfService() {
        dock.setOutOfService();
        assertEquals(DockState.OUT_OF_SERVICE, dock.getState());
    }

    @Test
    void testSetOutOfServiceWhenOccupiedThrowsException() {
        dock.occupy(bike);
        assertThrows(IllegalStateException.class, () -> dock.setOutOfService());
    }

    @Test
    void testRepair() {
        dock.setOutOfService();
        dock.repair();
        assertEquals(DockState.EMPTY, dock.getState());
    }

    @Test
    void testSetState() {
        dock.setState(DockState.OCCUPIED);
        assertEquals(DockState.OCCUPIED, dock.getState());
    }

    @Test
    void testSetBike() {
        dock.setBike(bike);
        assertEquals(bike, dock.getBike());
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        Subscriber testSubscriber = new Subscriber() {
            @Override
            public void update(String eventType, Object object) {
                // Test subscriber implementation
            }
        };
        
        dock.subscribe(testSubscriber);
        dock.unsubscribe(testSubscriber);
        // If no exception is thrown, the test passes
        assertTrue(true);
    }
}


