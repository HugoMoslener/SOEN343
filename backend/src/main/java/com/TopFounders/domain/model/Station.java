package com.TopFounders.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Station {

    // Attributes
    private final String stationID;
    private final String name;
    private StationOperationalState operationalState;
    private final double latitude;
    private final double longitude;
    private final String address;
    private final int capacity;
    private ArrayList<Dock> docks;
    /* Note: I removed the freeDocks because it will constantly change. It is safer to call a
    method only than to an attribute and the method inside the getter. It overcomplicates
    the constructor.*/

    // Constructors

    public Station(){
        this.stationID = "";
        this.name = "";
        this.latitude = 0;
        this.longitude = 0;
        this.address = "";
        this.capacity = 0;
    }
    public Station(String stationID, String name,
                   double latitude, double longitude, String address, int capacity){
        this.stationID = stationID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.capacity = capacity;
        this.operationalState = StationOperationalState.ACTIVE; // Active by default
        this.docks = new ArrayList<>();
        initializeDocks();
    }

    // Setters
    public void setOperationalState(StationOperationalState operationalState) { this.operationalState = operationalState; }
    public void setDocks(ArrayList<Dock> docks) { this.docks = docks; }

    // Getters
    public String getStationID() { return stationID; }
    public String getName() { return name; }
    public StationOperationalState getOperationalState() { return operationalState; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public int getCapacity() { return capacity; }
    public ArrayList<Dock> getDocks() { return docks; }

    // Method to creates docks automatically when Station is instantiated
    private void initializeDocks(){
        for (int i = 1; i <= capacity; i++){
            Dock dock = new Dock(stationID + "-" + i, stationID);
            // Assuming that the dockID follows this format (e.g. for station 1543, Dock is 1542-1)
            docks.add(dock);
        }
    }

    // Method to get number of available bikes
    public int getBikesAvailable() {
        return (int) docks.stream().filter(dock -> dock.getState() == DockState.OCCUPIED).count();
    }

    // Method to get number of free docks
    public int getFreeDocks(){
        return (int) docks.stream().filter(dock -> dock.getState() == DockState.EMPTY).count();
    }

    // Method for occupancy state
    public StationOccupancyState getOccupancyStatus() {
        if (operationalState == StationOperationalState.OUT_OF_SERVICE){
            return StationOccupancyState.OUT_OF_SERVICE;
        }

        int availableBikes = getBikesAvailable();
        int freeDocks = getFreeDocks();

        if (availableBikes == 0) return StationOccupancyState.EMPTY;
        if (freeDocks == 0) return StationOccupancyState.FULL;
        return StationOccupancyState.OCCUPIED;
    }

}


