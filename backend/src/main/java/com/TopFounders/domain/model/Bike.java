package com.TopFounders.domain.model;

import com.TopFounders.domain.observer.Publisher;
import com.TopFounders.domain.observer.Subscriber;
import com.TopFounders.domain.state.*;
import com.google.cloud.firestore.annotation.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Bike implements Publisher {

    // Attributes

    private  String bikeID;
    private  BikeType type;

    @Exclude
    private transient BikeState state;
    private String dockID;
    private List<Subscriber> subscribers = new ArrayList<>();
    private String stateString;

    public Bike(){
    }

    // Constructors
    public Bike(String bikeID, BikeType type){
        this.bikeID = bikeID;
        this.type = type;
        this.state = new Available(); // Available by default
        this.stateString = "AVAILABLE";
    }

    // Setters
    @Exclude
    public void setState(BikeState state) { this.state = state;
        this.stateString = state.getClass().getSimpleName().toUpperCase();}
    public void setDockID(String dockID) { this.dockID = dockID; }
    public String getStateString() { return stateString; }
    public void setStateString(String stateString) { this.stateString = stateString; }
    // Getters
    public String getBikeID() { return bikeID; }
    public BikeType getType() { return type; }
    @Exclude
    public BikeState getState() {
        if (state == null && stateString != null) {
            // Convert back from string
            this.state = switch (stateString.toUpperCase()) {
                case "AVAILABLE" -> new Available();
                case "RESERVED" -> new Reserved();
                case "MAINTENANCE" -> new Maintenance();
                case "ONTRIP" -> new OnTrip();
                default -> new Available();
            };
        }
        return state;
    }
    public String getDockID() { return dockID; }

    // Methods for Observer design pattern
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
        for (Subscriber subscriber : subscribers){
            subscriber.update(eventType, this);
        }
    }

    // Methods to change state
    public void reserve() {state.reserve(this);}
    public void checkout() {state.checkout(this);}
    public void returnBike() {state.returnBike(this);}
    public void maintenance() {state.maintenance(this);}

}
