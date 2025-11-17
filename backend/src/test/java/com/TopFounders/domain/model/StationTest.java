package com.TopFounders.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationTest {

    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station("STATION001", "Downtown Station", 
                              45.5017, -73.5673, "123 Main St", 10);
    }

    @Test
    void testStationCreation() {
        assertNotNull(station);
        assertEquals("STATION001", station.getStationID());
        assertEquals("Downtown Station", station.getName());
        assertEquals(45.5017, station.getLatitude());
        assertEquals(-73.5673, station.getLongitude());
        assertEquals("123 Main St", station.getAddress());
        assertEquals(10, station.getCapacity());
        assertEquals(StationOperationalState.ACTIVE, station.getOperationalState());
    }

    @Test
    void testStationDefaultConstructor() {
        Station newStation = new Station();
        assertNotNull(newStation);
    }

    @Test
    void testDocksInitialized() {
        assertNotNull(station.getDocks());
        assertEquals(10, station.getDocks().size());
    }

    @Test
    void testDockIDsFollowPattern() {
        var docks = station.getDocks();
        assertEquals("STATION001-1", docks.get(0).getDockID());
        assertEquals("STATION001-10", docks.get(9).getDockID());
    }

    @Test
    void testGetBikesAvailable() {
        assertEquals(0, station.getBikesAvailable());
        
        Bike bike1 = new Bike("BIKE001", BikeType.STANDARD);
        Bike bike2 = new Bike("BIKE002", BikeType.STANDARD);
        
        station.getDocks().get(0).occupy(bike1);
        station.getDocks().get(1).occupy(bike2);
        
        assertEquals(2, station.getBikesAvailable());
    }

    @Test
    void testGetFreeDocks() {
        assertEquals(10, station.getFreeDocks());
        
        Bike bike = new Bike("BIKE001", BikeType.STANDARD);
        station.getDocks().get(0).occupy(bike);
        
        assertEquals(9, station.getFreeDocks());
    }

    @Test
    void testGetOccupancyStatus() {
        assertEquals(StationOccupancyState.EMPTY, station.getOccupancyStatus());
        
        // Fill all docks
        Bike bike = new Bike("BIKE001", BikeType.STANDARD);
        for (Dock dock : station.getDocks()) {
            dock.occupy(bike);
        }
        
        assertEquals(StationOccupancyState.FULL, station.getOccupancyStatus());
        
        // Release one bike
        station.getDocks().get(0).release();
        
        assertEquals(StationOccupancyState.OCCUPIED, station.getOccupancyStatus());
    }

    @Test
    void testGetOccupancyStatusWhenOutOfService() {
        station.setOperationalState(StationOperationalState.OUT_OF_SERVICE);
        assertEquals(StationOccupancyState.OUT_OF_SERVICE, station.getOccupancyStatus());
    }

    @Test
    void testUpdateADock() {
        Dock updatedDock = new Dock("STATION001-1", "STATION001");
        Bike bike = new Bike("BIKE001", BikeType.STANDARD);
        updatedDock.occupy(bike);
        
        station.updateADock(updatedDock);
        
        assertEquals(DockState.OCCUPIED, station.getDocks().get(0).getState());
        assertEquals(bike, station.getDocks().get(0).getBike());
    }

    @Test
    void testSetOperationalState() {
        station.setOperationalState(StationOperationalState.OUT_OF_SERVICE);
        assertEquals(StationOperationalState.OUT_OF_SERVICE, station.getOperationalState());
    }

    @Test
    void testSetReservationHoldTime() {
        station.setReservationHoldTime(10.0);
        assertEquals(10.0, station.getReservationHoldTime());
    }

    @Test
    void testDefaultReservationHoldTime() {
        assertEquals(5.0, station.getReservationHoldTime());
    }
}

