package com.TopFounders.domain.state;

import com.TopFounders.domain.model.Bike;

public interface BikeState {

    // Methods to implement in each concrete state
    void reserve(Bike bike);
    void checkout(Bike bike);
    void returnBike(Bike bike);
    void maintenance(Bike bike);
    String getState();

}
