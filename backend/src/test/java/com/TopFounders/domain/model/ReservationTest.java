package com.TopFounders.domain.model;

import com.TopFounders.domain.observer.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Reservation reservation;
    private Rider rider;
    private Bike bike;

    @BeforeEach
    void setUp() {
        rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        bike = new Bike("BIKE001", BikeType.STANDARD);
        reservation = new Reservation(rider, bike);
    }

    @Test
    void testReservationCreation() {
        assertNotNull(reservation);
        assertNotNull(reservation.getReservationID());
        assertNotNull(reservation.getDate());
        assertNotNull(reservation.getTime());
        assertEquals(rider, reservation.getRider());
        assertEquals(bike, reservation.getBike());
        assertEquals(ReservationState.PENDING, reservation.getState());
    }

    @Test
    void testReservationDefaultConstructor() {
        Reservation newReservation = new Reservation();
        assertNotNull(newReservation);
    }

    @Test
    void testSetAndGetReservationID() {
        String newID = "RES123";
        reservation.setReservationID(newID);
        assertEquals(newID, reservation.getReservationID());
    }

    @Test
    void testSetAndGetDate() {
        String newDate = "2024-01-15";
        reservation.setDate(newDate);
        assertEquals(newDate, reservation.getDate());
    }

    @Test
    void testSetAndGetTime() {
        String newTime = "10:30:00";
        reservation.setTime(newTime);
        assertEquals(newTime, reservation.getTime());
    }

    @Test
    void testSetAndGetRider() {
        Rider newRider = new Rider("rider2", "payment456", "rider2@test.com", "Jane Doe", "456 St", "rider");
        reservation.setRider(newRider);
        assertEquals(newRider, reservation.getRider());
    }

    @Test
    void testSetAndGetBike() {
        Bike newBike = new Bike("BIKE002", BikeType.E_BIKE);
        reservation.setBike(newBike);
        assertEquals(newBike, reservation.getBike());
    }

    @Test
    void testSetAndGetState() {
        reservation.setState(ReservationState.CONFIRMED);
        assertEquals(ReservationState.CONFIRMED, reservation.getState());
        
        reservation.setState(ReservationState.CANCELLED);
        assertEquals(ReservationState.CANCELLED, reservation.getState());
    }

    @Test
    void testSetAndGetTripID() {
        String tripID = "TRIP001";
        reservation.setTripID(tripID);
        assertEquals(tripID, reservation.getTripID());
    }

    @Test
    void testCreateTrip() {
        Payment payment = new Payment("credit", 50.0);
        PricingPlan pricingPlan = new PricingPlan("1");
        pricingPlan.setPricingPlan1();
        
        Trip trip = reservation.createTrip("Origin Station", payment, pricingPlan);
        
        assertNotNull(trip);
        assertNotNull(trip.getTripID());
        assertEquals("Origin Station", trip.getOrigin());
        assertEquals(payment, trip.getPayment());
        assertEquals(pricingPlan, trip.getPricingPlan());
        assertEquals(trip.getTripID(), reservation.getTripID());
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        Subscriber testSubscriber = new Subscriber() {
            @Override
            public void update(String eventType, Object object) {
                // Test subscriber implementation
            }
        };
        
        reservation.subscribe(testSubscriber);
        reservation.unsubscribe(testSubscriber);
        // If no exception is thrown, the test passes
        assertTrue(true);
    }
}


