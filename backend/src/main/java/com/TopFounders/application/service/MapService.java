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

    public Bike getAvailableBike(String name, String bikeID){
        Station station = getStation(name);
        for (Dock dock : station.getDocks()) {
            if (dock.getBike().getBikeID().equals(bikeID)) {
                return dock.getBike();
            }
        }
        return null;
    }

    public Station getStation(String name){
        for (Station station : stations) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;

    }
}
