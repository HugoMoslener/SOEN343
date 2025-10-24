package com.TopFounders.ui.controller;

public class DockingHelperClass {
    private String dockID;
    private String reservationID;
    private String riderID;

    public DockingHelperClass() {}

    public String  getDockID() {
        return dockID;
    }
    public void setDockID(String dockID) {this.dockID = dockID;}
    public String getReservationID() {return reservationID;}
    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }
    public String getRiderID() {return riderID;}
    public void setRiderID(String riderID) {this.riderID = riderID;}
}
