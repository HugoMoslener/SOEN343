package com.TopFounders.domain.model;

public class Operator extends User{
    private String LinkedRiderAccount;//username of the rider account


    public Operator(){}
    public Operator(String username,String email, String fullName, String address,String role) {
        super(username,email, fullName,address,role);

    }

    public String getLinkedRiderAccount() {return LinkedRiderAccount;}
    public void setLinkedRiderAccount(String linkedRiderAccount) {this.LinkedRiderAccount = linkedRiderAccount;}
}