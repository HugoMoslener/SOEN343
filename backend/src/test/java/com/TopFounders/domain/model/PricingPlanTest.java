package com.TopFounders.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PricingPlanTest {

    private PricingPlan pricingPlan;

    @BeforeEach
    void setUp() {
        pricingPlan = new PricingPlan("1");
    }

    @Test
    void testPricingPlanCreation() {
        System.out.println("\n=== TEST: PricingPlanTest.testPricingPlanCreation ===");
        assertNotNull(pricingPlan);
        System.out.println("[OK] Validated pricing plan creation.");
    }

    @Test
    void testPricingPlanDefaultConstructor() {
        System.out.println("\n=== TEST: PricingPlanTest.testPricingPlanDefaultConstructor ===");
        PricingPlan newPlan = new PricingPlan();
        assertNotNull(newPlan);
        System.out.println("[OK] Validated pricing plan default constructor.");
    }

    @Test
    void testSetPricingPlan1() {
        System.out.println("\n=== TEST: PricingPlanTest.testSetPricingPlan1 ===");
        pricingPlan.setPricingPlan1();
        assertEquals("1", pricingPlan.getPlanID());
        assertEquals("Base plan", pricingPlan.getPlanName());
        assertEquals(10.0, pricingPlan.getRatePerMinute());
        assertEquals(15.0, pricingPlan.getBaseFee());
        System.out.println("[PRICE] Plan 1: ID=" + pricingPlan.getPlanID() + ", Name=" + pricingPlan.getPlanName() + 
                          ", Base Fee=$" + pricingPlan.getBaseFee() + ", Rate=$" + pricingPlan.getRatePerMinute() + "/min");
        System.out.println("[OK] Validated pricing plan 1 configuration.");
    }

    @Test
    void testSetPricingPlan2() {
        System.out.println("\n=== TEST: PricingPlanTest.testSetPricingPlan2 ===");
        pricingPlan.setPricingPlan2();
        assertEquals("2", pricingPlan.getPlanID());
        assertEquals("Premium plan", pricingPlan.getPlanName());
        assertEquals(4.0, pricingPlan.getRatePerMinute());
        assertEquals(30.0, pricingPlan.getBaseFee());
        System.out.println("[PRICE] Plan 2: ID=" + pricingPlan.getPlanID() + ", Name=" + pricingPlan.getPlanName() + 
                          ", Base Fee=$" + pricingPlan.getBaseFee() + ", Rate=$" + pricingPlan.getRatePerMinute() + "/min");
        System.out.println("[OK] Validated pricing plan 2 configuration.");
    }

    @Test
    void testSetPricingPlan3() {
        System.out.println("\n=== TEST: PricingPlanTest.testSetPricingPlan3 ===");
        pricingPlan.setPricingPlan3();
        assertEquals("2", pricingPlan.getPlanID()); // Note: Plan 3 uses ID "2" in current implementation
        assertEquals("Premium plan pro", pricingPlan.getPlanName());
        assertEquals(2.0, pricingPlan.getRatePerMinute());
        assertEquals(50.0, pricingPlan.getBaseFee());
        System.out.println("[PRICE] Plan 3: ID=" + pricingPlan.getPlanID() + ", Name=" + pricingPlan.getPlanName() + 
                          ", Base Fee=$" + pricingPlan.getBaseFee() + ", Rate=$" + pricingPlan.getRatePerMinute() + "/min");
        System.out.println("[OK] Validated pricing plan 3 configuration.");
    }

    @Test
    void testSetPlanInfo() {
        System.out.println("\n=== TEST: PricingPlanTest.testSetPlanInfo ===");
        PricingPlan plan = new PricingPlan();
        plan.setPlanInfo("1");
        assertEquals("1", plan.getPlanID());
        assertEquals("Base plan", plan.getPlanName());
        
        plan.setPlanInfo("2");
        assertEquals("2", plan.getPlanID());
        assertEquals("Premium plan", plan.getPlanName());
        
        plan.setPlanInfo("3");
        assertEquals("2", plan.getPlanID()); // Plan 3 uses ID "2"
        assertEquals("Premium plan pro", plan.getPlanName());
        
        System.out.println("[PRICE] Plan info setter tested for plans 1, 2, and 3.");
        System.out.println("[OK] Validated setPlanInfo method for all plans.");
    }

    @Test
    void testGettersAndSetters() {
        System.out.println("\n=== TEST: PricingPlanTest.testGettersAndSetters ===");
        pricingPlan.setPlanID("TEST");
        pricingPlan.setPlanName("Test Plan");
        pricingPlan.setRatePerMinute(5.0);
        pricingPlan.setBaseFee(20.0);
        
        assertEquals("TEST", pricingPlan.getPlanID());
        assertEquals("Test Plan", pricingPlan.getPlanName());
        assertEquals(5.0, pricingPlan.getRatePerMinute());
        assertEquals(20.0, pricingPlan.getBaseFee());
        
        System.out.println("[STATE] Plan ID: " + pricingPlan.getPlanID() + ", Name: " + pricingPlan.getPlanName());
        System.out.println("[PRICE] Base Fee: $" + pricingPlan.getBaseFee() + ", Rate: $" + pricingPlan.getRatePerMinute() + "/min");
        System.out.println("[OK] Validated all getters and setters.");
    }
}

