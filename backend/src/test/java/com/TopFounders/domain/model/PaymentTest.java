package com.TopFounders.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment("credit", 50.0);
    }

    @Test
    void testPaymentCreation() {
        System.out.println("\n=== TEST: PaymentTest.testPaymentCreation ===");
        assertNotNull(payment);
        assertEquals("credit", payment.getPaymentMethod());
        assertEquals(50.0, payment.getAmount());
        assertNotNull(payment.getPaidDate());
        System.out.println("[STATE] Payment created: Method=" + payment.getPaymentMethod() + ", Amount=$" + payment.getAmount());
        System.out.println("[OK] Validated payment creation with method and amount.");
    }

    @Test
    void testPaymentDefaultConstructor() {
        System.out.println("\n=== TEST: PaymentTest.testPaymentDefaultConstructor ===");
        Payment newPayment = new Payment();
        assertNotNull(newPayment);
        System.out.println("[OK] Validated payment default constructor.");
    }

    @Test
    void testSetAndGetPaymentMethod() {
        System.out.println("\n=== TEST: PaymentTest.testSetAndGetPaymentMethod ===");
        payment.setPaymentMethod("debit");
        assertEquals("debit", payment.getPaymentMethod());
        System.out.println("[STATE] Payment method: " + payment.getPaymentMethod());
        System.out.println("[OK] Validated payment method getter and setter.");
    }

    @Test
    void testSetAndGetAmount() {
        System.out.println("\n=== TEST: PaymentTest.testSetAndGetAmount ===");
        payment.setAmount(100.0);
        assertEquals(100.0, payment.getAmount());
        System.out.println("[PRICE] Payment amount: $" + String.format("%.2f", payment.getAmount()));
        System.out.println("[OK] Validated payment amount getter and setter.");
    }

    @Test
    void testSetAndGetPaidDate() {
        System.out.println("\n=== TEST: PaymentTest.testSetAndGetPaidDate ===");
        String date = "2024-01-15";
        payment.setPaidDate(date);
        assertEquals(date, payment.getPaidDate());
        System.out.println("[STATE] Paid date: " + payment.getPaidDate());
        System.out.println("[OK] Validated payment date getter and setter.");
    }

    @Test
    void testSetAndGetTripID() {
        System.out.println("\n=== TEST: PaymentTest.testSetAndGetTripID ===");
        String tripID = "TRIP123";
        payment.setTripID(tripID);
        assertEquals(tripID, payment.getTripID());
        System.out.println("[STATE] Trip ID: " + payment.getTripID());
        System.out.println("[OK] Validated payment trip ID getter and setter.");
    }

    @Test
    void testPaymentWithZeroAmount() {
        System.out.println("\n=== TEST: PaymentTest.testPaymentWithZeroAmount ===");
        Payment zeroPayment = new Payment("credit", 0.0);
        assertEquals(0.0, zeroPayment.getAmount());
        System.out.println("[PRICE] Zero amount payment created.");
        System.out.println("[OK] Validated payment with zero amount.");
    }
}

