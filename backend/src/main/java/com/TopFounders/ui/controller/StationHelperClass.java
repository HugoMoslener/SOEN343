package com.TopFounders.ui.controller;

public class StationHelperClass {

    private String stationID;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private int capacity;

    public StationHelperClass(){}

    public String getStationID(){return stationID;}
    public void setStationID(String stationID){this.stationID = stationID;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public double getLatitude(){return latitude;}
    public void setLatitude(double latitude){this.latitude = latitude;}
    public double getLongitude(){return longitude;}
    public void setLongitude(double longitude){this.longitude = longitude;}
    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}
    public int getCapacity(){return capacity;}
    public void setCapacity(int capacity){this.capacity = capacity;}

}
