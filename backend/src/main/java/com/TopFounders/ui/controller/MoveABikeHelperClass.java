package com.TopFounders.ui.controller;

public class MoveABikeHelperClass {
    private String Dock1ID;
    private String Dock2ID;
    private String bikeID;
    public MoveABikeHelperClass() {}

    public String getDock1ID() {
        return Dock1ID;
    }
    public void setDock1ID(String Dock1ID) {this.Dock1ID = Dock1ID;}
    public String getDock2ID() {return Dock2ID;}
    public void setDock2ID(String Dock2ID) {this.Dock2ID = Dock2ID;}
    public String getBikeID() {return bikeID;}
    public void setBikeID(String bikeID) {this.bikeID = bikeID;}
}
