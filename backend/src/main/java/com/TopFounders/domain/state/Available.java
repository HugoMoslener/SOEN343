package com.TopFounders.domain.state;

import com.TopFounders.domain.model.Bike;

public class Available implements BikeState {

    // Implemented methods
    @Override
    public void reserve(Bike bike) {
        bike.setState(new Reserved());
        bike.notifySubscribers("BIKE_RESERVED");
    }

    @Override
    public void checkout(Bike bike) {
        throw new IllegalStateException("Bike must be reserved before checkout");
    }

    @Override
    public void returnBike(Bike bike) {
        throw new IllegalStateException("Bike must be in use before returning");
    }

    @Override
    public void maintenance(Bike bike) {
        bike.setState(new Maintenance());
        bike.notifySubscribers("BIKE_MAINTENANCE");
    }

    @Override
    public String getState() {
        return "AVAILABLE";
    }

}
