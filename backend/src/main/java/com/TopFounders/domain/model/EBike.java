package com.TopFounders.domain.model;

import com.TopFounders.domain.state.BikeState;

public class EBike extends Bike {

    public EBike(String bikeID) {
        super(bikeID, BikeType.E_BIKE);
    }
}
