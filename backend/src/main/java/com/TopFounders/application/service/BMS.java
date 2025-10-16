package com.TopFounders.application.service;

import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.observer.Subscriber;

public class BMS implements Subscriber {

    // We could make BMS a singleton

    @Override
    public void update(String eventType, Bike bike) {
        switch (eventType) {
            case "BIKE_RESERVED":
                System.out.println("BMS: Bike " + bike.getBikeID() + " reserved");
                // Update station occupancy, emit event, etc...
                break;
            case "BIKE_CHECKED_OUT":
                System.out.println("BMS: Bike " + bike.getBikeID() + " checked out");
                // Start trip timer
                break;
            case "BIKE_RETURNED":
                System.out.println("BMS: Bike " + bike.getBikeID() + " returned");
                // End trip and calculate pricing
                break;
            case "BIKE_MAINTENANCE":
                System.out.println("BMS: Bike " + bike.getBikeID() + " in maintenance");
                // Notify operators for balancing stations
                break;
        }
    }
}
