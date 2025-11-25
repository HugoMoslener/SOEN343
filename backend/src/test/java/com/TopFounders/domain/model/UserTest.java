package com.TopFounders.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user1", "user@test.com", "John Doe", "123 Main St", "rider");
    }

    @Test
    void testUserCreation() {
        System.out.println("\n=== TEST: UserTest.testUserCreation ===");
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("John Doe", user.getFullName());
        assertEquals("123 Main St", user.getAddress());
        assertEquals("rider", user.getRole());
        System.out.println("[STATE] User created: Username=" + user.getUsername() + ", Role=" + user.getRole());
        System.out.println("[OK] Validated user creation with all fields.");
    }

    @Test
    void testUserDefaultConstructor() {
        System.out.println("\n=== TEST: UserTest.testUserDefaultConstructor ===");
        User newUser = new User();
        assertNotNull(newUser);
        System.out.println("[OK] Validated user default constructor.");
    }

    @Test
    void testSetAndGetUsername() {
        System.out.println("\n=== TEST: UserTest.testSetAndGetUsername ===");
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
        System.out.println("[STATE] Username: " + user.getUsername());
        System.out.println("[OK] Validated username getter and setter.");
    }

    @Test
    void testSetAndGetEmail() {
        System.out.println("\n=== TEST: UserTest.testSetAndGetEmail ===");
        user.setEmail("newemail@test.com");
        assertEquals("newemail@test.com", user.getEmail());
        System.out.println("[STATE] Email: " + user.getEmail());
        System.out.println("[OK] Validated email getter and setter.");
    }

    @Test
    void testSetAndGetFullName() {
        System.out.println("\n=== TEST: UserTest.testSetAndGetFullName ===");
        user.setFullName("Jane Smith");
        assertEquals("Jane Smith", user.getFullName());
        System.out.println("[STATE] Full name: " + user.getFullName());
        System.out.println("[OK] Validated full name getter and setter.");
    }

    @Test
    void testSetAndGetAddress() {
        System.out.println("\n=== TEST: UserTest.testSetAndGetAddress ===");
        user.setAddress("456 Oak Ave");
        assertEquals("456 Oak Ave", user.getAddress());
        System.out.println("[STATE] Address: " + user.getAddress());
        System.out.println("[OK] Validated address getter and setter.");
    }

    @Test
    void testSetAndGetRole() {
        System.out.println("\n=== TEST: UserTest.testSetAndGetRole ===");
        user.setRole("operator");
        assertEquals("operator", user.getRole());
        System.out.println("[STATE] Role: " + user.getRole());
        System.out.println("[OK] Validated role getter and setter.");
    }

    @Test
    void testUserWithDifferentRoles() {
        System.out.println("\n=== TEST: UserTest.testUserWithDifferentRoles ===");
        User rider = new User("rider1", "rider@test.com", "Rider Name", "123 St", "rider");
        User operator = new User("op1", "op@test.com", "Operator Name", "456 St", "operator");
        
        assertEquals("rider", rider.getRole());
        assertEquals("operator", operator.getRole());
        System.out.println("[STATE] Rider role: " + rider.getRole() + ", Operator role: " + operator.getRole());
        System.out.println("[OK] Validated users with different roles.");
    }
}

