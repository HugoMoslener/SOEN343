package com.TopFounders.application.service;

import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Dock;
import com.TopFounders.domain.model.Station;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
public class MapService {
    private static MapService instance;
    private ArrayList<Station> stations;

    private MapService(){}

    public static synchronized MapService getInstance(){
        if(instance == null){
            instance = new MapService();
        }
        return instance;
    }
    public ArrayList<Station> getStations(){return stations;}
    public void setStations(ArrayList<Station> stations){this.stations = stations;}

    public Bike getAvailableBike(String name, String bikeID) throws ExecutionException, InterruptedException {
        System.out.println("name"+name + ": " + bikeID);
        Station station = getStation(name);
        System.out.println("name"+name + ": " + station.getName());
        for (Dock dock : station.getDocks()) {
            if(dock.getBike() != null){
            if (dock.getBike().getBikeID().equals(bikeID)) {
                System.out.println("BikeID"+ dock.getBike().getBikeID());
                return dock.getBike();
            }}
        }
        return null;
    }

    public Station getStation(String name){
        System.out.println("getStation started "+ name);
        for (Station station : stations) {
            //System.out.println("getStation started "+ station.getName());
            if (station.getName().equals(name)) {
                System.out.println("getStation started "+ station.getName());
                return station;
            }
        }
        return null;

    }
}
