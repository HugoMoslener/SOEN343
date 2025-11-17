package com.TopFounders.domain.Strategy;

import com.TopFounders.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicPlanStrategyTest {

    private BasicPlanStrategy strategy;
    private Trip trip;
    private Payment payment;
    private PricingPlan pricingPlan;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        strategy = new BasicPlanStrategy();
        payment = new Payment("credit", 0.0);
        pricingPlan = new PricingPlan("1");
        pricingPlan.setPricingPlan1(); // Base fee: 15.0, Rate per minute: 10.0
        trip = new Trip("Origin Station", payment, pricingPlan);
        
        // Set start and end times for the trip
        String startTime = "10:00:00";
        String endTime = "10:30:00"; // 30 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Create reservation with standard bike
        Rider rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        Bike bike = new Bike("BIKE001", BikeType.STANDARD);
        reservation = new Reservation(rider, bike);
        trip.setReservation(reservation);
    }

    @Test
    void testCalculateTotalWithStandardBike() {
        // Expected: baseFee (15.0) + (30 minutes * 10.0 per minute) = 15.0 + 300.0 = 315.0
        double total = strategy.calculateTotal(trip);
        assertEquals(315.0, total, 0.01);
    }

    @Test
    void testCalculateTotalWithEBike() {
        // Create reservation with E_BIKE
        Rider rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        EBike eBike = new EBike("EBIKE001");
        Reservation eBikeReservation = new Reservation(rider, eBike);
        trip.setReservation(eBikeReservation);
        
        // Expected: baseFee (15.0) + (30 minutes * 10.0 per minute) + 20.0 (E_BIKE surcharge) = 335.0
        double total = strategy.calculateTotal(trip);
        assertEquals(335.0, total, 0.01);
    }

    @Test
    void testCalculateTotalWithShortTrip() {
        String startTime = "10:00:00";
        String endTime = "10:05:00"; // 5 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Expected: baseFee (15.0) + (5 minutes * 10.0 per minute) = 15.0 + 50.0 = 65.0
        double total = strategy.calculateTotal(trip);
        assertEquals(65.0, total, 0.01);
    }

    @Test
    void testCalculateTotalWithLongTrip() {
        String startTime = "10:00:00";
        String endTime = "11:30:00"; // 90 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Expected: baseFee (15.0) + (90 minutes * 10.0 per minute) = 15.0 + 900.0 = 915.0
        double total = strategy.calculateTotal(trip);
        assertEquals(915.0, total, 0.01);
    }

    @Test
    void testCalculateTotalReturnsPositiveValue() {
        double total = strategy.calculateTotal(trip);
        assertTrue(total > 0, "Total should be positive");
    }

    @Test
    void testCalculateTotalWithDifferentPricingPlan() {
        PricingPlan plan2 = new PricingPlan("2");
        plan2.setPricingPlan2(); // Base fee: 30.0, Rate per minute: 4.0
        trip.setPricingPlan(plan2);
        trip.setRatePerMinute(plan2.getRatePerMinute());
        
        // Expected: baseFee (30.0) + (30 minutes * 4.0 per minute) = 30.0 + 120.0 = 150.0
        double total = strategy.calculateTotal(trip);
        assertEquals(150.0, total, 0.01);
    }
}

