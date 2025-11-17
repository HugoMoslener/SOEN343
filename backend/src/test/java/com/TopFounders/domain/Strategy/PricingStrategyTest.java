package com.TopFounders.domain.Strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PricingStrategyTest {

    @Test
    void testPricingStrategyInterfaceCanBeImplemented() {
        PricingStrategy strategy = new BasicPlanStrategy();
        assertNotNull(strategy);
        assertTrue(strategy instanceof PricingStrategy);
    }

    @Test
    void testBasicPlanStrategyIsPricingStrategy() {
        PricingStrategy strategy = new BasicPlanStrategy();
        assertNotNull(strategy);
    }

    @Test
    void testPremiumPlanStrategyIsPricingStrategy() {
        PricingStrategy strategy = new PremiumPlanStrategy();
        assertNotNull(strategy);
    }

    @Test
    void testBothStrategiesImplementPricingStrategy() {
        PricingStrategy basicStrategy = new BasicPlanStrategy();
        PricingStrategy premiumStrategy = new PremiumPlanStrategy();
        
        assertNotNull(basicStrategy);
        assertNotNull(premiumStrategy);
        assertTrue(basicStrategy instanceof PricingStrategy);
        assertTrue(premiumStrategy instanceof PricingStrategy);
    }
}

