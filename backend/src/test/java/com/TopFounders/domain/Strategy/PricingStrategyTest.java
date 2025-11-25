package com.TopFounders.domain.Strategy;

import com.TopFounders.application.service.ReservationService;
import com.TopFounders.application.service.RiderService;
import com.TopFounders.application.service.TierService;
import com.TopFounders.application.service.TripService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PricingStrategyTest {

    @Test
    void testPricingStrategyInterfaceCanBeImplemented() {
        RiderService riderService = new RiderService();
        ReservationService reservationService = new ReservationService();
        TripService tripService = new TripService();
        TierService tierService = new TierService(riderService, reservationService, tripService);
        PricingStrategy strategy = new BasicPlanStrategy(tierService);
        assertNotNull(strategy);
        assertTrue(strategy instanceof PricingStrategy);
    }

    @Test
    void testBasicPlanStrategyIsPricingStrategy() {
        RiderService riderService = new RiderService();
        ReservationService reservationService = new ReservationService();
        TripService tripService = new TripService();
        TierService tierService = new TierService(riderService, reservationService, tripService);
        PricingStrategy strategy = new BasicPlanStrategy(tierService);
        assertNotNull(strategy);
    }

    @Test
    void testPremiumPlanStrategyIsPricingStrategy() {
        RiderService riderService = new RiderService();
        ReservationService reservationService = new ReservationService();
        TripService tripService = new TripService();
        TierService tierService = new TierService(riderService, reservationService, tripService);
        PricingStrategy strategy = new PremiumPlanStrategy(tierService);
        assertNotNull(strategy);
    }

    @Test
    void testBothStrategiesImplementPricingStrategy() {
        RiderService riderService = new RiderService();
        ReservationService reservationService = new ReservationService();
        TripService tripService = new TripService();
        TierService tierService = new TierService(riderService, reservationService, tripService);
        PricingStrategy basicStrategy = new BasicPlanStrategy(tierService);
        PricingStrategy premiumStrategy = new PremiumPlanStrategy(tierService);
        
        assertNotNull(basicStrategy);
        assertNotNull(premiumStrategy);
        assertTrue(basicStrategy instanceof PricingStrategy);
        assertTrue(premiumStrategy instanceof PricingStrategy);
    }
}

