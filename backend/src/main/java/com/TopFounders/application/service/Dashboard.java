package com.TopFounders.application.service;

import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Rider;

import java.util.concurrent.ExecutionException;

public class Dashboard {

    private static Dashboard instance;

    private Dashboard(){}

    public static Dashboard getInstance(){
        if(instance == null){
            instance = new Dashboard();
        }
        return instance;
    }

    public String reserveBike(String stationName, Rider rider, String BikeID, String username) throws ExecutionException, InterruptedException {
        return BMS.getInstance().reserveBike(stationName,rider, BikeID, username);
    }

    public String undockBike(String username, String reservationID) throws ExecutionException, InterruptedException {
        BMS.getInstance().undockBike(username, reservationID);

        return "Successful";
    }
}
