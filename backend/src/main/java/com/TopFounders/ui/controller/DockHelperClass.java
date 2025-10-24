package com.TopFounders.ui.controller;

import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.model.DockState;

public class DockHelperClass {
    private  String dockID;
    private  String stationID;
    public DockHelperClass() {}

    public String getDockID() {return dockID;}
    public void setDockID(String dockID) {this.dockID = dockID;}
    public String getStationID() {return stationID;}
    public void setStationID(String stationID) {this.stationID = stationID;}
}
