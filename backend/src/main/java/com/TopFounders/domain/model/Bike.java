package com.TopFounders.domain.model;

import com.TopFounders.domain.observer.Publisher;
import com.TopFounders.domain.observer.Subscriber;
import com.TopFounders.domain.state.Available;
import com.TopFounders.domain.state.BikeState;

import java.util.ArrayList;
import java.util.List;

public class Bike implements Publisher {

    // Attributes
    private final String bikeID;
    private final BikeType type;
    private BikeState state;
    private String dockID;
    private List<Subscriber> subscribers = new ArrayList<>();

    public Bike(){
        this.bikeID = "BikeID";
        this.type = BikeType.STANDARD;
    }

    // Constructors
    public Bike(String bikeID, BikeType type){
        this.bikeID = bikeID;
        this.type = type;
        this.state = new Available(); // Available by default
    }

    // Setters
    public void setState(BikeState state) { this.state = state; }
    public void setDock(String dock) { this.dockID = dock; }

    // Getters
    public String getBikeID() { return bikeID; }
    public BikeType getType() { return type; }
    public BikeState getState() { return state; }
    public String getDock() { return dockID; }

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
