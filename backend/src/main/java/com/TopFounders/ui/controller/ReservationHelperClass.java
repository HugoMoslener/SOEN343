package com.TopFounders.ui.controller;

public class ReservationHelperClass {
    private String stationName;
    private String riderID;
    private String bikeID;

    public ReservationHelperClass(){}

    public String  getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {this.stationName = stationName;}
    public String getRiderID() {return riderID;}
    public void setRiderID(String riderID) {this.riderID = riderID;}
    public String getBikeID() {return bikeID;}
    public void setBikeID(String bikeID) {this.bikeID = bikeID;}

}
