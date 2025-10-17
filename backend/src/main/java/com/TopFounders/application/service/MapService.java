package com.TopFounders.application.service;

import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Dock;
import com.TopFounders.domain.model.Station;

import java.util.ArrayList;

public class MapService {
    private static MapService instance;
    private ArrayList<Station> stations;

    private MapService(){}

    public static MapService getInstance(){
        if(instance == null){
            instance = new MapService();
        }
        return instance;
    }
    public ArrayList<Station> getStations(){return stations;}
    public void setStations(ArrayList<Station> stations){this.stations = stations;}

    public Bike getAvailableBike(String name, String bikeID){
        System.out.println("name"+name + ": " + bikeID);
        Station station = getStation(name);
        for (Dock dock : station.getDocks()) {
            if (dock.getBike().getBikeID().equals(bikeID)) {
                System.out.println("BikeID"+ dock.getBike().getBikeID());
                return dock.getBike();
            }
        }
        return null;
    }

    public Station getStation(String name){
        System.out.println("getStation started "+ name);
        for (Station station : stations) {
            if (station.getName().equals(name)) {

                return station;
            }
        }
        return null;

    }
}
