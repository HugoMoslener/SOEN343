package com.TopFounders.application.service;

import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Rider;

public class Dashboard {

    private static Dashboard instance;

    private Dashboard(){}

    public static Dashboard getInstance(){
        if(instance == null){
            instance = new Dashboard();
        }
        return instance;
    }

    public String reserveBike(String name, Rider rider, String ID, String username){
        return BMS.getInstance().reserveBike(name,rider, ID, username);
    }
}
