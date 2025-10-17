package com.TopFounders.application.service;

import com.TopFounders.domain.model.*;
import com.TopFounders.domain.observer.Subscriber;
import com.TopFounders.domain.state.BikeState;
import com.TopFounders.domain.state.Maintenance;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class BMS implements Subscriber {

    private static BMS instance;

    private BMS(){}

    public static BMS getInstance(){
        if(instance == null){
            instance = new BMS();
        }
        return instance;
    }

    public String reserveBike(String name,Rider rider, String bikeID, String username){
        Bike bike = MapService.getInstance().getAvailableBike(name,bikeID);
        Reservation reservation = new Reservation(rider,bike);
        return bike.getBikeID();
    }

    public void moveABikefromDockAToDockB(Dock dockA, Dock dockB,Bike bike){
        if(dockA.getStationID().equals(dockB.getStationID())){return;}
        if(dockA.getState() != DockState.OCCUPIED || dockB.getState() != DockState.EMPTY){return;}
        if(bike.getStateString().equals("RESERVED") || bike.getStateString().equals("ONTRIP")){return;}

        dockA.setState(DockState.EMPTY);
        dockB.setState(DockState.OCCUPIED);
        bike.setDockID(dockB.getDockID());
        dockB.setBike(bike);
        dockA.setBike(null);
    }

    public void setAStationAsOutOfService(Station station){
        station.setOperationalState(StationOperationalState.OUT_OF_SERVICE);
        station.getOccupancyStatus();
    }

    public void setABikeAsMaintenance(Bike bike){
        bike.setState(new Maintenance());
        bike.setStateString("MAINTENANCE");
    }





    @Override
    public void update(String eventType, Bike bike) {
        switch (eventType) {
            case "BIKE_RESERVED":
                System.out.println("BMS: Bike " + bike.getBikeID() + " reserved");
                // Update station occupancy, emit event, etc...
                break;
            case "BIKE_CHECKED_OUT":
                System.out.println("BMS: Bike " + bike.getBikeID() + " checked out");
                // Start trip timer
                break;
            case "BIKE_RETURNED":
                System.out.println("BMS: Bike " + bike.getBikeID() + " returned");
                // End trip and calculate pricing
                break;
            case "BIKE_MAINTENANCE":
                System.out.println("BMS: Bike " + bike.getBikeID() + " in maintenance");
                // Notify operators for balancing stations
                break;
        }
    }
}
