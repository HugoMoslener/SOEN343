package com.TopFounders.ui.controller;

public class OperatorHelperClass {
    private String email;
    private String username;
    private String address;
    private String fullName;

    public OperatorHelperClass(){}

    public String getAddress(){return this.address;}
    public String getUsername(){return this.username;}
    public String getEmail(){return this.email;}
    public String getFullName(){return this.fullName;}
    public void setAddress(String address){ this.address=address;}
    public void setUsername(String username){this.username=username;}
    public void setEmail(String email){this.email = email;}
    public void setFullName(String fullName){this.fullName=fullName;}
}
