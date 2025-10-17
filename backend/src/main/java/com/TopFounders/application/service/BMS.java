package com.TopFounders.application.service;

import com.TopFounders.domain.model.*;
import com.TopFounders.domain.observer.Subscriber;
import com.TopFounders.domain.state.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class BMS implements Subscriber {

    private static BMS instance;

    private final BikeService bikeService = new BikeService();
    private final DockService dockService = new DockService();
    private final StationService stationService  = new StationService();
    private final ReservationService reservationService  = new ReservationService();
    private final RiderService riderService  = new RiderService();
    private final TripService tripService = new TripService();

    private BMS(){}

    public static BMS getInstance(){
        if(instance == null){
            instance = new BMS();
        }
        return instance;
    }

    public void cancelReservation(String reservationID,String username) throws ExecutionException, InterruptedException {
        Reservation reservation = reservationService.getReservationDetails(reservationID);
        if(reservation.getRider().getUsername().equals(username)) {
            Bike bike1 = bikeService.getBikeDetails(reservation.getBike().getBikeID());
            bike1.setStateString("AVAILABLE");
            bike1.setState(new Available());
            Dock dock = dockService.getDockDetails(bike1.getDockID());
            dock.setBike(bike1);

            String input = bike1.getDockID();
            int lastDash = input.lastIndexOf("-");

            String result = input.substring(0, lastDash);
            reservation.setBike(bike1);
            reservation.setState(ReservationState.CANCELLED);
            reservationService.updateReservationDetails(reservation);
            Station station = stationService.getStationDetails(result);
            station.updateADock(dock);
            bikeService.updateBikeDetails(bike1);
            dockService.updateDockDetails(dock);
            stationService.updateStationDetails(station);
        }
    }


    public String reserveBike(String stationName,Rider rider, String bikeID, String username) throws ExecutionException, InterruptedException {
       System.out.println("reserveBike started");
        Bike bike = MapService.getInstance().getAvailableBike(stationName,bikeID);
        System.out.println(bike.getBikeID());
        Bike bike1 = bikeService.getBikeDetails(bike.getBikeID());
        bike1.setStateString("RESERVED");
        bike1.setState(new Reserved());
        Dock dock = dockService.getDockDetails(bike1.getDockID());
        dock.setBike(bike1);
        System.out.println("SOme thing");

        String input = bike1.getDockID() ;
        int lastDash = input.lastIndexOf("-");

        String result = input.substring(0, lastDash);
        System.out.println("reserveBike middle");
        Station station = stationService.getStationDetails(result);
        station.updateADock(dock);

        if(!dock.getState().equals(DockState.OUT_OF_SERVICE)){
        bikeService.updateBikeDetails(bike1);
        dockService.updateDockDetails(dock);
        stationService.updateStationDetails(station);


        Reservation reservation = new Reservation(rider,bike1);
        ReservationService reservationService = new ReservationService();
        reservationService.saveReservation(reservation);
        System.out.println("reserveBike finished");
        return reservation.getReservationID();}
        return null;
    }

    public String undockBike(String username, String reservationID) throws ExecutionException, InterruptedException {
        Reservation reservation = reservationService.getReservationDetails(reservationID);
        Bike bike = bikeService.getBikeDetails(reservation.getBike().getBikeID());

        String input = bike.getDockID() ;
        int lastDash = input.lastIndexOf("-");
        String result = input.substring(0, lastDash);

        Dock dock = dockService.getDockDetails(bike.getDockID());
        Station station = stationService.getStationDetails(result);
        Rider rider = riderService.getRiderDetails(reservation.getRider().getUsername());
        if(reservation.getState().equals(ReservationState.PENDING) & dock.getState().equals(DockState.OCCUPIED) & !station.getOccupancyStatus().equals(StationOccupancyState.EMPTY) ){
            Trip trip = reservation.createTrip(station.getAddress(),new Payment(),new PricingPlan());
            reservation.setState(ReservationState.CONFIRMED);
            bike.setState(new OnTrip());
            bike.setStateString("ONTRIP");
            bike.setDockID(null);
            dock.setBike(null);
            dock.setState(DockState.EMPTY);
            station.updateADock(dock);
            station.getOccupancyStatus();
            trip.setReservation(reservation);
            tripService.saveTrip(trip);
            stationService.updateStationDetails(station);
            dockService.updateDockDetails(dock);
            bikeService.updateBikeDetails(bike);
            reservationService.updateReservationDetails(reservation);
            return "Successful";
        }

            return "Unsuccessful";
    }

    public String dockBike(String username, String reservationID, String dockID) throws ExecutionException, InterruptedException {

        Reservation reservation = reservationService.getReservationDetails(reservationID);
        Trip trip = tripService.getTripDetails(reservation.getTripID());
        Bike bike = bikeService.getBikeDetails(reservation.getBike().getBikeID());

        String input = dockID;
        int lastDash = input.lastIndexOf("-");
        String result = input.substring(0, lastDash);

        Dock dock = dockService.getDockDetails(dockID);
        Station station = stationService.getStationDetails(result);
        Rider rider = riderService.getRiderDetails(reservation.getRider().getUsername());

        if(reservation.getState().equals(ReservationState.CONFIRMED) & dock.getState().equals(DockState.EMPTY) & !station.getOccupancyStatus().equals(StationOccupancyState.FULL) ){

            trip.setArrival(station.getAddress());
            trip.setEndTime(LocalTime.now().toString());

            bike.setState(new Available());
            bike.setStateString("AVAILABLE");
            bike.setDockID(dockID);
            dock.setBike(bike);
            dock.setState(DockState.OCCUPIED);
            station.updateADock(dock);
            station.getOccupancyStatus();
            stationService.updateStationDetails(station);
            dockService.updateDockDetails(dock);
            bikeService.updateBikeDetails(bike);
            tripService.saveTrip(trip);

            return "Successful";
        }

        return "Unsuccessful";
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
