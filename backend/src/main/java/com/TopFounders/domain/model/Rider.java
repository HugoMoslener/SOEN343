package com.TopFounders.domain.model;

import com.TopFounders.application.service.BMS;
import com.TopFounders.application.service.Dashboard;

import java.util.concurrent.ExecutionException;

public class Rider extends User{

    private String paymentInformation;
    private Tier tier;

    public Rider() {
        this.tier = Tier.ENTRY;
    }
    public Rider(String username, String paymentInformation, String email, String fullName, String address, String role) {
        super(username,email, fullName,address,role);
        this.paymentInformation = paymentInformation;
        this.tier = Tier.ENTRY;
    }
    public String getPaymentInformation(){
        return paymentInformation;
    }
    public void setPaymentInformation(String paymentInformation){
        this.paymentInformation= paymentInformation;
    }

    public String reserveBike(String stationName, Rider rider, String BikeID, String username) throws ExecutionException, InterruptedException, IllegalStateException {

        return Dashboard.getInstance().reserveBike(stationName,rider, BikeID, username);
    }

    public String cancelBikeReservation(String reservationID, String username) throws ExecutionException, InterruptedException {

        String message = BMS.getInstance().cancelReservation(reservationID, username);
        return message;
    }

    public String undockBike(String username, String reservationID) throws ExecutionException, InterruptedException, IllegalStateException {
        String message = Dashboard.getInstance().undockBike(username, reservationID);
        return message;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

}
