package com.TopFounders.domain.model;

public class Operator extends User{

    private String linkedRider; // added this var

    public Operator(){}
    public Operator(String username,String email, String fullName, String address,String role) {
        super(username,email, fullName,address,role);
        this.linkedRider = null; // added this var

    }
    public String getLinkedRider() {
        return linkedRider;
    }
    public void setLinkedRider(String linkedRider) {
        this.linkedRider = linkedRider;
    }
}