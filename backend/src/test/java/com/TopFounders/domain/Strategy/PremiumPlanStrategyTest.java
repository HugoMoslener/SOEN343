package com.TopFounders.domain.Strategy;

import com.TopFounders.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PremiumPlanStrategyTest {

    private PremiumPlanStrategy strategy;
    private Trip trip;
    private Payment payment;
    private PricingPlan pricingPlan;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        strategy = new PremiumPlanStrategy();
        payment = new Payment("credit", 0.0);
        pricingPlan = new PricingPlan("2");
        pricingPlan.setPricingPlan2(); // Base fee: 30.0, Rate per minute: 4.0
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
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testCalculateTotalWithStandardBike ===");
        // Expected: baseFee (30.0) + (30 minutes * 4.0 per minute) = 30.0 + 120.0 = 150.0
        // Note: Premium plan does NOT add E_BIKE surcharge
        double total = strategy.calculateTotal(trip);
        assertEquals(150.0, total, 0.01);
        System.out.println("[PRICE] Base fee: $" + trip.getPricingPlan().getBaseFee() + ", Duration: 30 min, Rate: $" + trip.getRatePerMinute() + "/min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated premium plan pricing calculation for standard bike.");
    }

    @Test
    void testCalculateTotalWithEBike() {
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testCalculateTotalWithEBike ===");
        // Create reservation with E_BIKE
        Rider rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        EBike eBike = new EBike("EBIKE001");
        Reservation eBikeReservation = new Reservation(rider, eBike);
        trip.setReservation(eBikeReservation);
        
        // Expected: baseFee (30.0) + (30 minutes * 4.0 per minute) = 150.0
        // Premium plan does NOT add E_BIKE surcharge (unlike BasicPlanStrategy)
        double total = strategy.calculateTotal(trip);
        assertEquals(150.0, total, 0.01);
        System.out.println("[PRICE] Base fee: $" + trip.getPricingPlan().getBaseFee() + ", Duration: 30 min, Rate: $" + trip.getRatePerMinute() + "/min, Total: $" + String.format("%.2f", total) + " (no E-bike surcharge)");
        System.out.println("[OK] Validated premium plan does not add E-bike surcharge.");
    }

    @Test
    void testCalculateTotalWithShortTrip() {
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testCalculateTotalWithShortTrip ===");
        String startTime = "10:00:00";
        String endTime = "10:05:00"; // 5 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Expected: baseFee (30.0) + (5 minutes * 4.0 per minute) = 30.0 + 20.0 = 50.0
        double total = strategy.calculateTotal(trip);
        assertEquals(50.0, total, 0.01);
        System.out.println("[PRICE] Duration: 5 min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated premium plan pricing for short trip.");
    }

    @Test
    void testCalculateTotalWithLongTrip() {
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testCalculateTotalWithLongTrip ===");
        String startTime = "10:00:00";
        String endTime = "11:30:00"; // 90 minutes
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
        
        // Expected: baseFee (30.0) + (90 minutes * 4.0 per minute) = 30.0 + 360.0 = 390.0
        double total = strategy.calculateTotal(trip);
        assertEquals(390.0, total, 0.01);
        System.out.println("[PRICE] Duration: 90 min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated premium plan pricing for long trip.");
    }

    @Test
    void testCalculateTotalReturnsPositiveValue() {
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testCalculateTotalReturnsPositiveValue ===");
        double total = strategy.calculateTotal(trip);
        assertTrue(total > 0, "Total should be positive");
        System.out.println("[PRICE] Total: $" + String.format("%.2f", total) + " (positive)");
        System.out.println("[OK] Validated premium plan returns positive value.");
    }

    @Test
    void testCalculateTotalWithDifferentPricingPlan() {
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testCalculateTotalWithDifferentPricingPlan ===");
        PricingPlan plan3 = new PricingPlan("3");
        plan3.setPricingPlan3(); // Base fee: 50.0, Rate per minute: 2.0
        trip.setPricingPlan(plan3);
        trip.setRatePerMinute(plan3.getRatePerMinute());
        
        // Expected: baseFee (50.0) + (30 minutes * 2.0 per minute) = 50.0 + 60.0 = 110.0
        double total = strategy.calculateTotal(trip);
        assertEquals(110.0, total, 0.01);
        System.out.println("[PRICE] Plan: " + plan3.getPlanName() + ", Base fee: $" + plan3.getBaseFee() + ", Rate: $" + plan3.getRatePerMinute() + "/min, Total: $" + String.format("%.2f", total));
        System.out.println("[OK] Validated premium plan with different pricing plan.");
    }

    @Test
    void testPremiumPlanNoEBikeSurcharge() {
        System.out.println("\n=== TEST: PremiumPlanStrategyTest.testPremiumPlanNoEBikeSurcharge ===");
        // Verify that Premium plan does not add E_BIKE surcharge
        Rider rider = new Rider("rider1", "payment123", "rider@test.com", "John Doe", "123 St", "rider");
        EBike eBike = new EBike("EBIKE001");
        Reservation eBikeReservation = new Reservation(rider, eBike);
        trip.setReservation(eBikeReservation);
        
        double totalWithEBike = strategy.calculateTotal(trip);
        
        // Change to standard bike
        Bike standardBike = new Bike("BIKE001", BikeType.STANDARD);
        Reservation standardReservation = new Reservation(rider, standardBike);
        trip.setReservation(standardReservation);
        
        double totalWithStandard = strategy.calculateTotal(trip);
        
        // Both should be the same (no surcharge)
        assertEquals(totalWithStandard, totalWithEBike, 0.01);
        System.out.println("[PRICE] E-bike total: $" + String.format("%.2f", totalWithEBike) + ", Standard bike total: $" + String.format("%.2f", totalWithStandard) + " (same - no surcharge)");
        System.out.println("[OK] Validated premium plan does not apply E-bike surcharge.");
    }
}


