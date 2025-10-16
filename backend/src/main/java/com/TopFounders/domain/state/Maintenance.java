package com.TopFounders.domain.state;

import com.TopFounders.domain.model.Bike;

public class Maintenance implements BikeState{
    @Override
    public void reserve(Bike bike) {
        throw new IllegalStateException("Bike in maintenance");
    }

    @Override
    public void checkout(Bike bike) {
        throw new IllegalStateException("Bike in maintenance");
    }

    @Override
    public void returnBike(Bike bike) {
        throw new IllegalStateException("Bike in maintenance");
    }

    @Override
    public void maintenance(Bike bike) {    }

    @Override
    public String getState() {
        return "MAINTENANCE";
    }
}
