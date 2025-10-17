package com.TopFounders.domain.model;

import com.TopFounders.application.service.Dashboard;

public class Rider extends User{

    private String paymentInformation;

    public Rider() {
    }
    public Rider(String username, String paymentInformation, String email, String fullName, String address, String role) {
        super(username,email, fullName,address,role);
        this.paymentInformation = paymentInformation;
    }
    public String getPaymentInformation(){
        return paymentInformation;
    }
    public void setPaymentInformation(String paymentInformation){
        this.paymentInformation= paymentInformation;
    }

    public String reserveBike(String name, Rider rider, String ID, String username){
        return Dashboard.getInstance().reserveBike(name,rider, ID, username);
    }

}
