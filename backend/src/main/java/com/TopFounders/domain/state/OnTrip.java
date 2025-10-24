package com.TopFounders.domain.state;

import com.TopFounders.domain.model.Bike;

public class OnTrip implements BikeState{
    @Override
    public void reserve(Bike bike) {
        throw new IllegalStateException("Bike currently on trip");
    }

    @Override
    public void checkout(Bike bike) {
        throw new IllegalStateException("Bike already checked out");
    }

    @Override
    public void returnBike(Bike bike) {
        bike.setState(new Available());
        bike.notifySubscribers("BIKE_RETURNED");
    }

    @Override
    public void maintenance(Bike bike) {
        throw new IllegalStateException("Bike currently on trip");
    }

    @Override
    public String getState() {
        return "ON_TRIP";
    }
}
