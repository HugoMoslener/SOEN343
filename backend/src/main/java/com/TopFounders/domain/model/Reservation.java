package com.TopFounders.domain.model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

public class Reservation {
    private String reservationID;
    private String  date;
    private String time ;
    private Rider rider;
    private Bike bike;
    public String tripID;
    public ReservationState state;

    public Reservation(){}

    public Reservation(Rider rider,Bike bike){
        this.reservationID= UUID.randomUUID().toString();
        this.date= LocalDate.now().toString();
        this.time= LocalTime.now().toString();
        this.rider=rider;
        this.bike=bike;
        this.state = ReservationState.PENDING;
    }
    public ReservationState getState() {
        return state;
    }
    public void setState(ReservationState state) {
        this.state = state;
    }
    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bike getBike() {return bike;}

    public void setBike(Bike bike) {this.bike = bike;}

    public String getTripID() {return tripID;}

    public void setTripID(String tripID) {this.tripID = tripID;}

        public Trip createTrip(String origin,Payment payment, PricingPlan pricingPlan){
        Trip trip = new Trip(origin,payment,pricingPlan);
        this.tripID = trip.getTripID();
        return trip;}
}
