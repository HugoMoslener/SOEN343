package com.TopFounders.ui.controller;

public class RiderController {
    private String email;
    private String username;
    private String paymentInformation;
    private String address;
    private String fullName;
    
    public RiderController(){}
    
    public String getAddress(){return this.address;}
    public String getUsername(){return this.username;}
    public String getPaymentInformation(){return this.paymentInformation;}
    public String getEmail(){return this.email;}
    public String getFullName(){return this.fullName;}
    public void setAddress(String address){ this.address=address;}
    public void setUsername(String username){this.username=username;}
    public void setPaymentInformation(String paymentInformation){this.paymentInformation=paymentInformation;}
    public void setEmail(String email){this.email = email;}
    public void setFullName(String fullName){this.fullName=fullName;}

}
