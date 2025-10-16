package com.TopFounders.domain.model;

import java.sql.Time;
import java.util.Date;

public class Reservation {
    private String reservationID;
    private Date  date;
    private Time time ;
    private Rider rider;
    private Bike bike;
    public Trip trip;

    public Reservation(){}

    public Reservation(String reservationID,Date date,Time time ,Rider rider,Bike bike, Trip trip){
        this.reservationID=reservationID;
        this.date=date;
        this.time=time;
        this.rider=rider;
        this.bike=bike;
        this.trip = trip;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Bike getBike() {return bike;}

    public void setBike(Bike bike) {this.bike = bike;}

    public Trip getTrip() {return trip;}

    public void setTrip(Trip trip) {this.trip = trip;}
}
