package com.TopFounders.domain.model;

import javax.print.Doc;

public class Dock {

    // Attributes
    private final String dockID;
    private DockState state; // EMPTY, OCCUPIED, MAINTENANCE
    private Bike bike;
    private final String stationID;

    // Constructors
    public Dock(String dockID, String stationID){
        this.dockID = dockID;
        this.stationID = stationID;
        this.state = DockState.EMPTY; // Empty by default (no bike assigned yet)
        this.bike = null;
    }

    // Setters
    public void setState(DockState state) { this.state = state; }
    public void setBike(Bike bike) { this.bike = bike; }

    // Getters
    public String getDockID() { return dockID; }
    public DockState getState() { return state; }
    public Bike getBike() { return bike; }
    public String getStationID() { return stationID; }

    // Method to occupy a bike
    public void occupy(Bike bike){
        if (state != DockState.EMPTY) {
            throw new IllegalStateException("Dock is not empty");
        }
        this.bike = bike;
        this.state = DockState.OCCUPIED;
    }

    // Method to release a bike
    public Bike release(){
        if (state != DockState.OCCUPIED) {
            throw new IllegalStateException("Dock is not occupied");
        }
        Bike releasedBike = this.bike;
        this.bike = null;
        this.state = DockState.EMPTY;
        return releasedBike;
    }

    // Method to set dock out of service
    public void setOutOfService(){
        if (state == DockState.OCCUPIED){
            throw new IllegalStateException("Cannot put occupied dock out of service");
        }
        this.state = DockState.OUT_OF_SERVICE;
    }

    // Method to put dock back to service (for operators)
    public void repair(){
        this.state = DockState.EMPTY;
    }
}
