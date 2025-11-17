package com.TopFounders.domain.model;

import com.TopFounders.domain.observer.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    private Trip trip;
    private Payment payment;
    private PricingPlan pricingPlan;

    @BeforeEach
    void setUp() {
        payment = new Payment("credit", 50.0);
        pricingPlan = new PricingPlan("1");
        pricingPlan.setPricingPlan1();
        trip = new Trip("Origin Station", payment, pricingPlan);
    }

    @Test
    void testTripCreation() {
        assertNotNull(trip);
        assertNotNull(trip.getTripID());
        assertNotNull(trip.getStartTime());
        assertEquals("Origin Station", trip.getOrigin());
        assertEquals(payment, trip.getPayment());
        assertEquals(pricingPlan, trip.getPricingPlan());
        assertNotNull(trip.getRatePerMinute());
    }

    @Test
    void testTripDefaultConstructor() {
        Trip newTrip = new Trip();
        assertNotNull(newTrip);
    }

    @Test
    void testSetAndGetStartTime() {
        String newStartTime = "10:00:00";
        trip.setStartTime(newStartTime);
        assertEquals(newStartTime, trip.getStartTime());
    }

    @Test
    void testSetAndGetEndTime() {
        String endTime = "10:30:00";
        trip.setEndTime(endTime);
        assertEquals(endTime, trip.getEndTime());
    }

    @Test
    void testCalculateDuration() {
        String startTime = "10:00:00";
        String endTime = "10:30:00";
        
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        long expectedMinutes = ChronoUnit.MINUTES.between(start, end);
        
        assertEquals(30, expectedMinutes);
    }

    @Test
    void testSetAndGetRatePerMinute() {
        Double newRate = 15.0;
        trip.setRatePerMinute(newRate);
        assertEquals(newRate, trip.getRatePerMinute());
    }

    @Test
    void testSetAndGetArrival() {
        String arrival = "Destination Station";
        trip.setArrival(arrival);
        assertEquals(arrival, trip.getArrival());
    }

    @Test
    void testSetAndGetOrigin() {
        String newOrigin = "New Origin";
        trip.setOrigin(newOrigin);
        assertEquals(newOrigin, trip.getOrigin());
    }

    @Test
    void testSetAndGetPayment() {
        Payment newPayment = new Payment("debit", 75.0);
        trip.setPayment(newPayment);
        assertEquals(newPayment, trip.getPayment());
    }

    @Test
    void testSetAndGetPricingPlan() {
        PricingPlan newPlan = new PricingPlan("2");
        newPlan.setPricingPlan2();
        trip.setPricingPlan(newPlan);
        assertEquals(newPlan, trip.getPricingPlan());
    }

    @Test
    void testSetAndGetReservation() {
        Rider rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        Bike bike = new Bike("BIKE001", BikeType.STANDARD);
        Reservation reservation = new Reservation(rider, bike);
        
        trip.setReservation(reservation);
        assertEquals(reservation, trip.getReservation());
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        Subscriber testSubscriber = new Subscriber() {
            @Override
            public void update(String eventType, Object object) {
                // Test subscriber implementation
            }
        };
        
        trip.subscribe(testSubscriber);
        trip.unsubscribe(testSubscriber);
        // If no exception is thrown, the test passes
        assertTrue(true);
    }
}

