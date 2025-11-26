package com.TopFounders.domain.Strategy;

import com.TopFounders.application.service.ReservationService;
import com.TopFounders.application.service.RiderService;
import com.TopFounders.application.service.TierService;
import com.TopFounders.application.service.TripService;
import com.TopFounders.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class BasicPlanStrategyTest {

    private BasicPlanStrategy strategy;
    private Trip trip;
    private Payment payment;
    private PricingPlan pricingPlan;
    private Reservation reservation;
    private TierService tierService;

    @BeforeEach
    void setUp() {
        RiderService riderService = new RiderService();
        ReservationService reservationService = new ReservationService();
        TripService tripService = new TripService();
        tierService = new TierService(riderService, reservationService, tripService);
        strategy = new BasicPlanStrategy(tierService);
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
    void testCalculateTotalWithStandardBike() throws ExecutionException, InterruptedException {
        System.out.println("\n=== TEST: BasicPlanStrategyTest.testCalculateTotalWithStandardBike ===");
        // Expected: baseFee (15.0) + (30 minutes * 10.0 per minute) = 15.0 + 300.0 = 315.0
        // Note: New riders default to ENTRY tier (0% discount), so base price should match
        double total = strategy.calculateTotal(trip);
        assertEquals(315.0, total, 0.01);
        System.out.println("[PRICE] Base fee: $" + trip.getPricingPlan().getBaseFee() + ", Duration: 30 min, Rate: $" + trip.getRatePerMinute() + "/min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated pricing calculation for standard bike trip.");
    }

    @Test
    void testCalculateTotalWithEBike() throws ExecutionException, InterruptedException {
        System.out.println("\n=== TEST: BasicPlanStrategyTest.testCalculateTotalWithEBike ===");
        // Create reservation with E_BIKE
        Rider rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        EBike eBike = new EBike("EBIKE001");
        Reservation eBikeReservation = new Reservation(rider, eBike);
        trip.setReservation(eBikeReservation);
        
        // Expected: baseFee (15.0) + (30 minutes * 10.0 per minute) + 20.0 (E_BIKE surcharge) = 335.0
        // Note: New riders default to ENTRY tier (0% discount)
        double total = strategy.calculateTotal(trip);
        assertEquals(335.0, total, 0.01);
        System.out.println("[PRICE] Base fee: $" + trip.getPricingPlan().getBaseFee() + ", Duration: 30 min, Rate: $" + trip.getRatePerMinute() + "/min, E-bike surcharge: $20.00, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated pricing calculation for E-bike trip with surcharge.");
    }

    @Test
    void testCalculateTotalWithShortTrip() throws ExecutionException, InterruptedException {
        System.out.println("\n=== TEST: BasicPlanStrategyTest.testCalculateTotalWithShortTrip ===");
        String startTime = "10:00:00";
        String endTime = "10:05:00"; // 5 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Expected: baseFee (15.0) + (5 minutes * 10.0 per minute) = 15.0 + 50.0 = 65.0
        double total = strategy.calculateTotal(trip);
        assertEquals(65.0, total, 0.01);
        System.out.println("[PRICE] Duration: 5 min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated pricing calculation for short trip.");
    }

    @Test
    void testCalculateTotalWithLongTrip() throws ExecutionException, InterruptedException {
        System.out.println("\n=== TEST: BasicPlanStrategyTest.testCalculateTotalWithLongTrip ===");
        String startTime = "10:00:00";
        String endTime = "11:30:00"; // 90 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Expected: baseFee (15.0) + (90 minutes * 10.0 per minute) = 15.0 + 900.0 = 915.0
        double total = strategy.calculateTotal(trip);
        assertEquals(915.0, total, 0.01);
        System.out.println("[PRICE] Duration: 90 min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated pricing calculation for long trip.");
    }

    @Test
    void testCalculateTotalReturnsPositiveValue() throws ExecutionException, InterruptedException {
        System.out.println("\n=== TEST: BasicPlanStrategyTest.testCalculateTotalReturnsPositiveValue ===");
        double total = strategy.calculateTotal(trip);
        assertTrue(total > 0, "Total should be positive");
        System.out.println("[PRICE] Total: $" + String.format("%.2f", total) + " (positive)");
        System.out.println("[OK] Validated pricing returns positive value.");
    }

    @Test
    void testCalculateTotalWithDifferentPricingPlan() throws ExecutionException, InterruptedException {
        System.out.println("\n=== TEST: BasicPlanStrategyTest.testCalculateTotalWithDifferentPricingPlan ===");
        PricingPlan plan2 = new PricingPlan("2");
        plan2.setPricingPlan2(); // Base fee: 30.0, Rate per minute: 4.0
        trip.setPricingPlan(plan2);
        trip.setRatePerMinute(plan2.getRatePerMinute());
        
        // Expected: baseFee (30.0) + (30 minutes * 4.0 per minute) = 30.0 + 120.0 = 150.0
        double total = strategy.calculateTotal(trip);
        assertEquals(150.0, total, 0.01);
        System.out.println("[PRICE] Plan: " + plan2.getPlanName() + ", Base fee: $" + plan2.getBaseFee() + ", Rate: $" + plan2.getRatePerMinute() + "/min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated pricing calculation with different pricing plan.");
    }
}

