package com.TopFounders.application.service;

import com.TopFounders.domain.factory.RiderCreator;
import com.TopFounders.domain.model.*;
import com.TopFounders.domain.observer.Subscriber;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final UserService userService = new UserService();

    private BMS(){}

    public static BMS getInstance(){
        if(instance == null){
            instance = new BMS();
        }
        return instance;
    }

    public String saveRiderData(String username, String paymentInformation, String email, String fullName, String address, String role) throws ExecutionException, InterruptedException {
        RiderCreator factory = new RiderCreator();
        String message = riderService.saveRider(factory.createUser(username,paymentInformation,email,fullName,address, "rider"));

        return message;
    }

    public String cancelReservation(String reservationID,String username) throws ExecutionException, InterruptedException {
        Reservation reservation = reservationService.getReservationDetails(reservationID);
        if((reservation.getRider().getUsername().equals(username))) {
            Bike bike1 = bikeService.getBikeDetails(reservation.getBike().getBikeID());
            //bike1.setStateString("AVAILABLE");
            bike1.returnBike(); // return it from reservation
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
            reservation.notifySubscribers("RESERVATION_EXPIRED");
            return "Successful";
        }
        return "Unsuccessful";
    }


    public String reserveBike(String stationName,Rider rider, String bikeID, String username) throws ExecutionException, InterruptedException, IllegalStateException {
       System.out.println("reserveBike started");

       for(Reservation reservation: reservationService.getAllReservations()){ // prevents a user from having more than one reservation
           if(reservation.getRider().getUsername().equals(rider.getUsername())){
               if(reservation.getState() == ReservationState.PENDING){
                   return null;
               }
           }
       }

       // Create two bike objects
        Bike bike = MapService.getInstance().getAvailableBike(stationName,bikeID); // gets the bike instance from the map service (not the database ig)
        Bike localBikeInstance = bikeService.getBikeDetails(bike.getBikeID()); // get the bike object from the database

        localBikeInstance.setBikeStateByString(localBikeInstance.getStateString());
        // Try to reserve bike, need to throw an exception here if the bike is not reservable
        localBikeInstance.reserve();

        // Get current dock information for updating
        Dock dock = dockService.getDockDetails(localBikeInstance.getDockID());
        dock.setBike(localBikeInstance); // update what the bike looks like in the dock

        String input = localBikeInstance.getDockID() ;
        int lastDash = input.lastIndexOf("-");

        String result = input.substring(0, lastDash);
        Station station = stationService.getStationDetails(result);
        station.updateADock(dock);

        // If the dock is not out of service, then make sure the backend is up-to-date with this new change
        if(dock.getState() != DockState.OUT_OF_SERVICE){
            bikeService.updateBikeDetails(localBikeInstance);
            dockService.updateDockDetails(dock);
            stationService.updateStationDetails(station);

            Reservation reservation = new Reservation(rider,localBikeInstance);
            reservationService.saveReservation(reservation);
            return reservation.getReservationID();
        }

        // If the dock was out of service
        return null;
    }

    public String undockBike(String username, String reservationID) throws ExecutionException, InterruptedException, IllegalStateException {
        Reservation reservation = reservationService.getReservationDetails(reservationID);
        Bike bike = bikeService.getBikeDetails(reservation.getBike().getBikeID());

        String input = bike.getDockID() ;
        int lastDash = input.lastIndexOf("-");
        String result = input.substring(0, lastDash);

        Dock dock = dockService.getDockDetails(bike.getDockID());
        Station station = stationService.getStationDetails(result);
        Rider rider = riderService.getRiderDetails(reservation.getRider().getUsername());
        if((reservation.getRider().getUsername().equals(username)) & (reservation.getState() == ReservationState.PENDING) & (dock.getState() == DockState.OCCUPIED) & (station.getOccupancyStatus() != StationOccupancyState.EMPTY) ){

            Trip trip = reservation.createTrip(station.getAddress(),new Payment(),new PricingPlan());
            reservation.setState(ReservationState.CONFIRMED);

            bike.setBikeState(bike.getState());
            bike.checkout();

            bike.setDockID(null);
            dock.setBike(null);
            dock.setState(DockState.EMPTY);
            station.updateADock(dock);
            station.getOccupancyStatus();
            trip.setReservation(reservation);
            Payment payment = new  Payment();
            payment.setTripID(trip.getTripID());
            trip.setPayment(payment);
            tripService.saveTrip(trip);
            stationService.updateStationDetails(station);
            dockService.updateDockDetails(dock);
            bikeService.updateBikeDetails(bike);
            reservationService.updateReservationDetails(reservation);
            trip.notifySubscribers("TRIP_STARTED");
            return "Successful";
        }

            return "Unsuccessful";
    }

    public Trip dockBike(String username, String reservationID, String dockID, String planID) throws ExecutionException, InterruptedException {

        Reservation reservation = reservationService.getReservationDetails(reservationID);
        Trip trip = tripService.getTripDetails(reservation.getTripID());
        Bike bike = bikeService.getBikeDetails(reservation.getBike().getBikeID());

        String input = dockID;
        int lastDash = input.lastIndexOf("-");
        String result = input.substring(0, lastDash);

        Dock dock = dockService.getDockDetails(dockID);
        Station station = stationService.getStationDetails(result);
        Rider rider = riderService.getRiderDetails(reservation.getRider().getUsername());

        if((reservation.getRider().getUsername().equals(username)) & (reservation.getState() == ReservationState.CONFIRMED) & (dock.getState() == DockState.EMPTY) & (station.getOccupancyStatus() != StationOccupancyState.FULL) ){
            Payment payment = trip.getPayment();

            payment.setPaymentMethod(rider.getPaymentInformation());
            trip.setPayment(payment);
            trip.setArrival(station.getAddress());
            trip.setEndTime(LocalTime.now().toString());
             PricingPlan pricingPlan = trip.getPricingPlan();

            pricingPlan.setPlanInfo(planID);

           System.out.println(pricingPlan.getPlanID());
           System.out.println(pricingPlan.getPlanName());


            if(planID.equals("1")){
                System.out.println(calculateTotalAmount(trip.getStartTime(), trip.getEndTime(), pricingPlan.getRatePerMinute()));
                if(trip.getReservation().getBike().getType() == BikeType.E_BIKE){
                    payment.setAmount((Double) ( 20 +pricingPlan.getBaseFee() + calculateTotalAmount(trip.getStartTime(), trip.getEndTime(), pricingPlan.getRatePerMinute())));

                }
                else {
                    payment.setAmount((Double) (pricingPlan.getBaseFee() + calculateTotalAmount(trip.getStartTime(), trip.getEndTime(), pricingPlan.getRatePerMinute())));
                }}
            else if (planID.equals("2") ||planID.equals("3")){
                payment.setAmount((Double)(pricingPlan.getBaseFee() + calculateTotalAmount(trip.getStartTime(),trip.getEndTime(),pricingPlan.getRatePerMinute())));
            }

            trip.setRatePerMinute((Double)(pricingPlan.getRatePerMinute()));


            trip.setPricingPlan(pricingPlan);


            if(planID.equals("1")){
                System.out.println(calculateTotalAmount(trip.getStartTime(), trip.getEndTime(), pricingPlan.getRatePerMinute()));
                if(trip.getReservation().getBike().getType() == BikeType.E_BIKE){
                    payment.setAmount((Double) ( 20 +pricingPlan.getBaseFee() + calculateTotalAmount(trip.getStartTime(), trip.getEndTime(), pricingPlan.getRatePerMinute())));

                }
                else {
                    payment.setAmount((Double) (pricingPlan.getBaseFee() + calculateTotalAmount(trip.getStartTime(), trip.getEndTime(), pricingPlan.getRatePerMinute())));
                }}
            else if (planID.equals("2") ||planID.equals("3")){
                payment.setAmount((Double)(pricingPlan.getBaseFee() + calculateTotalAmount(trip.getStartTime(),trip.getEndTime(),pricingPlan.getRatePerMinute())));
            }

            trip.setRatePerMinute((Double)(pricingPlan.getRatePerMinute()));


            trip.setPricingPlan(pricingPlan);

            // update local bike
            bike.setBikeState(bike.getState());
            bike.returnBike();

            bike.setDockID(dockID);

            // update local dock
            dock.setBike(bike);
            dock.setState(DockState.OCCUPIED);

            // update local station
            station.updateADock(dock);
            station.getOccupancyStatus();

            // update backend
            stationService.updateStationDetails(station);
            dockService.updateDockDetails(dock);
            bikeService.updateBikeDetails(bike);
            tripService.updateTripDetails(trip);
            trip.notifySubscribers("TRIP_ENDED");
            return trip;
        }

        return null;
    }
    public  double calculateTotalAmount(String startTime, String endTime, Double ratePerMinute) {
        if (ratePerMinute == null) {
            System.out.println("Rate per minute not available.");
            return 0.0;
        }

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        if (end.isBefore(start)) {
            end = end.plusHours(24);
        }

        Duration duration = Duration.between(start, end);
        double minutes = duration.toMillis() / (1000.0 * 60);

        return minutes * ratePerMinute;
    }

    public String paymentInterface(String tripID) throws ExecutionException, InterruptedException{
        System.out.println("paymentInterface");
        Trip trip = tripService.getTripDetails(tripID);
        Payment payment = trip.getPayment();
        payment.setPaidDate(LocalDate.now().toString());
        trip.setPayment(payment);
        System.out.println("paymentInterface");
        tripService.updateTripDetails(trip);

        return "Successful";
    }

    public String moveABikefromDockAToDockB(Dock dockA, Dock dockB,Bike bike) throws ExecutionException, InterruptedException {
       // if(dockA.getStationID().equals(dockB.getStationID())){return "Unsuccessful";}
        if(dockA.getState() != DockState.OCCUPIED || dockB.getState() != DockState.EMPTY){return "Unsuccessful";}
        if(bike.getStateString().equals("RESERVED") || bike.getStateString().equals("ONTRIP")){return "Unsuccessful";}

        System.out.println("Moving bike " + bike.getBikeID());

        // updating the dock's statuses
        dockA.setState(DockState.EMPTY);
        dockB.setState(DockState.OCCUPIED);

        // changing the dock associated with the bike is set to
        bike.setDockID(dockB.getDockID());

        // updating local dock's Bike instance
        dockB.setBike(bike);
        dockA.setBike(null);

        // update the backend for station 1 (dock A)
        Station station1 =  stationService.getStationDetails(dockA.getStationID());
        station1.updateADock(dockA);
        stationService.updateStationDetails(station1);

        // update the backend for station 2 (dock B)
        Station station2 =  stationService.getStationDetails(dockB.getStationID());
        station2.updateADock(dockB);
        stationService.updateStationDetails(station2);

        // update the backend for the docks
        dockService.updateDockDetails(dockA);
        dockService.updateDockDetails(dockB);

        // update the backend for the bike
        bikeService.updateBikeDetails(bike);

        System.out.println("Bike moved successfully");

        return "Successful";
    }

    public String setAStationAsOutOfService(Station station) throws ExecutionException, InterruptedException {
        station.setOperationalState(StationOperationalState.OUT_OF_SERVICE);
        station.getOccupancyStatus();
        stationService.updateStationDetails(station);
        return "Successful";
    }

    public String setABikeAsMaintenance(Bike bike) throws ExecutionException, InterruptedException, IllegalStateException {
        bike.setBikeStateByString(bike.getStateString());
        bike.maintenance(); // sets bike to maintenance
        String id = bike.getDockID(); // update the dock and station as well
        bike.setDockID(id);
        Dock dock = dockService.getDockDetails(bike.getDockID());
        Station station = stationService.getStationDetails(dock.getStationID());
        dock.setBike(bike);
        dock.setState(DockState.OUT_OF_SERVICE);
        station.updateADock(dock);
        stationService.updateStationDetails(station);
        dockService.updateDockDetails(dock);
        bikeService.updateBikeDetails(bike);
        return "Successful";
    }

    public String setAStationAsActive(Station station) throws ExecutionException, InterruptedException {
        station.setOperationalState(StationOperationalState.ACTIVE);
        station.getOccupancyStatus();
        stationService.updateStationDetails(station);
        return "Successful";
    }

    public String setABikeAsAvailable(Bike bike) throws ExecutionException, InterruptedException {
        bike.returnBike(); // sets bike back to Available
        Dock dock = dockService.getDockDetails(bike.getDockID());
        Station station = stationService.getStationDetails(dock.getStationID());
        dock.setBike(bike);
        dock.setState(DockState.OCCUPIED);
        station.updateADock(dock);
        stationService.updateStationDetails(station);
        dockService.updateDockDetails(dock);
        bikeService.updateBikeDetails(bike);
        return "Successful";
    }

    public String resetInitialSystemState() throws ExecutionException, InterruptedException {
        System.out.println("some");
        ArrayList<Dock> dockArrayList = dockService.getAllDocks();
        System.out.println(dockArrayList);
        System.out.println("reachedbike");
        ArrayList<Bike> bikeArrayList = bikeService.getAllBikes();
        System.out.println(bikeArrayList);
         System.out.println("hello");
        for(Dock dock : dockArrayList){
            dock.setState(DockState.EMPTY);
            dock.setBike(null);
            Station station1 =  stationService.getStationDetails(dock.getStationID());
            station1.updateADock(dock);
            stationService.updateStationDetails(station1);
        }

        for(Bike bike : bikeArrayList){
            bike.returnBike(); // sets the state to available
            bike.setDockID(bike.getBikeID());

            Dock dock = dockService.getDockDetails(bike.getDockID());
            Station station = stationService.getStationDetails(dock.getStationID());
            station.setOperationalState(StationOperationalState.ACTIVE);
            station.getOccupancyStatus();
            dock.setBike(bike);
            dock.setState(DockState.OCCUPIED);
            station.updateADock(dock);
            stationService.updateStationDetails(station);
            dockService.updateDockDetails(dock);
            bikeService.updateBikeDetails(bike);
        }
        return "Successful";
    }

    public ArrayList<Trip> getAllTripsForRiderOrOperator(String username) throws ExecutionException, InterruptedException {
        User user = userService.getUserDetails(username);
        ArrayList<Trip> trips = tripService.getAllTrip();
        if(user.getRole().equals("rider")){
            System.out.println("rider");
            ArrayList<Trip> trips1 = new ArrayList<>();
            for(Trip trip : trips){
                if(trip != null  && trip.getReservation() != null &&
                        trip.getReservation().getRider() != null){
                    System.out.println(trip.getReservation().getRider().getUsername());
                    if(trip.getReservation().getRider().getUsername().equals(username)){
                        trips1.add(trip);
                    }
                }
            }
            return trips1;
        }
        else if (user.getRole().equals("operator")){
            return trips;
        }
        return null;
    }




    @Override
    public void update(String eventType, Object object) {
        switch (eventType) {
            case "BIKE_RESERVED":
                System.out.println("BMS: Bike " + ((Bike)object).getBikeID() + " reserved");
                // Update station occupancy, emit event, etc...
                break;
            case "BIKE_CHECKED_OUT":
                System.out.println("BMS: Bike " + ((Bike)object).getBikeID() + " checked out");
                // Start trip timer
                break;
            case "BIKE_RETURNED":
                System.out.println("BMS: Bike " + ((Bike)object).getBikeID() + " returned");
                // End trip and calculate pricing
                break;
            case "BIKE_MAINTENANCE":
                System.out.println("BMS: Bike " + ((Bike)object).getBikeID() + " in maintenance");
                // Notify operators for balancing stations
                break;

            case "DOCK_FULL":
                System.out.println("BMS: Dock " + ((Dock)object).getDockID() + " is full");
                // Notify when a dock is full
                break;
            case "DOCK_EMPTY":
                System.out.println("BMS: Dock " + ((Dock)object).getDockID() + " is empty");
                // Notifies when a dock is empty
                break;
            case "DOCK_OUT_OF_SERVICE":
                System.out.println("BMS: Dock " + ((Dock)object).getDockID() + " is out of service");
                // Notifies when a dock is out of service
                break;

            case "TRIP_STARTED":
                System.out.println("BMS: Trip " + ((Trip)object).getTripID() + " has started.");
                // Notifies when a trip has started
                break;
            case "TRIP_ENDED":
                System.out.println("BMS: Trip " + ((Trip)object).getTripID() + " has ended.");
                // Notifies when a trip has ended
                break;

            case "RESERVATION_EXPIRED":
                System.out.println("BMS: Reservation " + ((Reservation)object).getReservationID() + " has expired.");
                // Notifes when a reservation has expired
                break;

        }
    }
}
