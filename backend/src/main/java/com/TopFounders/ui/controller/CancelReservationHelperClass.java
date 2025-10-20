package com.TopFounders.ui.controller;

public class CancelReservationHelperClass {

    private String reservationID;
    private String riderID;

    public CancelReservationHelperClass(){}

    public String  getReservationID() {
        return reservationID;
    }
    public void setReservationID(String reservationID) {this.reservationID = reservationID;}
    public String getRiderID() {return riderID;}
    public void setRiderID(String riderID) {this.riderID = riderID;}
}
