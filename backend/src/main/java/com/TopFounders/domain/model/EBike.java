package com.TopFounders.domain.model;

public class EBike extends Bike {

    public EBike() {
        super();
    }

    public EBike(String bikeID) {
        super(bikeID, BikeType.E_BIKE);
    }
}
