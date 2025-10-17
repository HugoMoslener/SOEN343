package com.TopFounders.application.service;

import com.TopFounders.domain.model.BikeType;

public class Dashboard {

    private static Dashboard instance;

    private Dashboard(){}

    public static Dashboard getInstance(){
        if(instance == null){
            instance = new Dashboard();
        }
        return instance;
    }

    public String reserveBike(String name, String ID, String username){
        return BMS.getInstance().reserveBike(name, ID, username);
    }
}
