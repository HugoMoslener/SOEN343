package com.TopFounders.domain.model;

import com.TopFounders.application.service.BMS;
import com.TopFounders.domain.observer.Publisher;
import com.TopFounders.domain.observer.Subscriber;
import com.google.cloud.firestore.annotation.Exclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Bike implements Publisher {

    // Getters
    // Attributes
    @Getter
    private  String bikeID;
    @Getter
    @Setter
    private String dockID;
    // Setters
    @Getter
    private String stateString; // this is just a variable that copies the BikeState enum but in a string format
    private  BikeType type;

    @Exclude
    private BikeState state;
    @Exclude
    private List<Subscriber> subscribers = new ArrayList<>();
    @Exclude
    private transient boolean loadingFromFirestore = true;
    //private transient BikeState state;
    private enum BikeState {
        AVAILABLE,
        MAINTENANCE,
        RESERVED,
        ON_TRIP
    }

    // Bike Ctors
    public Bike(){}
    public Bike(String bikeID, BikeType type){
        this.bikeID = bikeID;
        this.type = type;
        setBikeState(BikeState.AVAILABLE); // Availablù0e by default
    }

    // To ensure the stateString always follows the BikeState, the bike state can only be changed through a method that always changes the string aswell
    public void setBikeState(BikeState bs){
        state = bs;
        switch (state){
            case AVAILABLE -> {
                stateString = "AVAILABLE";
            }
            case RESERVED -> {
                stateString = "RESERVED";
            }
            case ON_TRIP -> {
                stateString = "ONTRIP";
            }
            case MAINTENANCE -> {
                stateString = "MAINTENANCE";
            }
        }
    }
    public void setBikeStateByString(String stateString){
        if(stateString.equalsIgnoreCase("AVAILABLE")){
            state = BikeState.AVAILABLE;
            this.stateString = "AVAILABLE";
        }
        else if(stateString.equalsIgnoreCase("RESERVED")){
            state = BikeState.RESERVED;
            this.stateString = "RESERVED";
        }else if(stateString.equalsIgnoreCase("ONTRIP")){
            state = BikeState.ON_TRIP;
            this.stateString = "ONTRIP";
        }else if(stateString.equalsIgnoreCase("MAINTENANCE")){
            state = BikeState.MAINTENANCE;
            this.stateString = "MAINTENANCE";
        }else{
            state = BikeState.AVAILABLE;
            this.stateString = "AVAILABLE";
        }
    }

    public void setStateString(String stateString) {
        this.stateString = stateString;
        setBikeStateByString(stateString); // keeps enum in sync after deserialization
    }

    @Exclude
    public BikeState getState() {
        return state;
    }

    public BikeType getType() { return type; }

    public void markAsLoadingFromFirestore(boolean loading) {
        this.loadingFromFirestore = loading;
    }

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
        subscribe(BMS.getInstance());
        for (Subscriber subscriber : subscribers){
            subscriber.update(eventType, this);
        }
    }

    // Methods to change state
    // 1. Check what state the bike is in
    // 2. If it's in a state that is acceptable to receive the change, then change the state and update whoever needs to be updated and save the new state to the database
    // 3. If it's NOT in a state that is acceptable to receive the change, then return an error or something explaining why the change can't be applied to the bike at the moment

    public void reserve() throws IllegalStateException{
        // if the bike is in the available state, then set it to reserve
        if(state == BikeState.AVAILABLE){
            setBikeState(BikeState.RESERVED);
            notifySubscribers("BIKE_RESERVED");
        }
        else {
            throw new IllegalStateException("Bike is not available!");
        }
        // save this new state to firebase
    }
    public void checkout() throws IllegalStateException {
        if(state == BikeState.RESERVED){
            setBikeState(BikeState.ON_TRIP);
            notifySubscribers("BIKE_CHECKED_OUT");
        }
        else {
            throw new IllegalStateException("Bike is not reserved!");
        }
    }
    public void returnBike() {
        setBikeState(BikeState.AVAILABLE);
        notifySubscribers("BIKE_RETURNED");
    }
    public void maintenance() throws IllegalStateException {
        if(state == BikeState.AVAILABLE){
            setBikeState(BikeState.MAINTENANCE);
            notifySubscribers("BIKE_MAINTENANCE");
        }
        else {
            throw new IllegalStateException("Bike is not available!");
        }
    }

}
