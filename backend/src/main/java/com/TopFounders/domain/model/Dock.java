package com.TopFounders.domain.model;

import com.TopFounders.application.service.BMS;
import com.TopFounders.domain.observer.Publisher;
import com.TopFounders.domain.observer.Subscriber;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class Dock implements Publisher {

    // Attributes
    private  String dockID;
    private DockState state; // EMPTY, OCCUPIED, MAINTENANCE
    private Bike bike;
    private String stationID;
    private List<Subscriber> subscribers = new ArrayList<>();

    public Dock(){
    }

    // Constructors
    public Dock(String dockID, String stationID){
        this.dockID = dockID;
        this.stationID = stationID;
        this.state = DockState.EMPTY; // Empty by default (no bike assigned yet)
        this.bike = null;
    }

    // Setters
    public void setState(DockState state) {
        this.state = state;
        updateState();
        }

    private void updateState(){
        if(getState().equals(DockState.EMPTY)){ notifySubscribers("DOCK_EMPTY");}
        if(getState().equals(DockState.OCCUPIED)){ notifySubscribers("DOCK_FULL"); }
        if(getState().equals(DockState.OUT_OF_SERVICE)){ notifySubscribers("DOCK_OUT_OF_SERVICE");}
    }

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

    @Override
    public void subscribe(Subscriber subscriber){
        subscribers.add(subscriber);
    }
    @Override
    public void unsubscribe(Subscriber subscriber){
        subscribers.remove(subscriber);
    }
    @Override
    public void notifySubscribers(String eventType){
        subscribe(BMS.getInstance());
        for (Subscriber subscriber : subscribers){
            subscriber.update(eventType, this);
        }
    }
}
