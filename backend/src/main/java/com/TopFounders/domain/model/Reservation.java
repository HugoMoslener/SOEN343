package com.TopFounders.domain.model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Reservation {
    private String reservationID;
    private LocalDate  date;
    private LocalTime time ;
    private Rider rider;
    private Bike bike;
    public Trip trip;

    public Reservation(){}

    public Reservation(Rider rider,Bike bike){
        this.reservationID=rider.getUsername() + LocalDate.now();
        this.date= LocalDate.now();
        this.time= LocalTime.now();
        this.rider=rider;
        this.bike=bike;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Bike getBike() {return bike;}

    public void setBike(Bike bike) {this.bike = bike;}

    public Trip getTrip() {return trip;}

    public void setTrip(Trip trip) {this.trip = trip;}
}
