package com.TopFounders.domain.state;

import com.TopFounders.domain.model.Bike;

public class Reserved implements BikeState {

    @Override
    public void reserve(Bike bike) {
        throw new IllegalStateException("Bike already reserved");
    }

    @Override
    public void checkout(Bike bike) {
        bike.setState(new OnTrip());
        bike.notifySubscribers("BIKE_CHECKED_OUT");
    }

    @Override
    public void returnBike(Bike bike) {
        bike.setState(new Available());
        bike.notifySubscribers("BIKE_RETURNED");
    }

    @Override
    public void maintenance(Bike bike) {
        throw new IllegalStateException("Cannot put reserved bike in maintenance");
    }

    @Override
    public String getState() {
        return "RESERVED";
    }
}
