package com.TopFounders.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public class Trip {
    private String tripID;
    private String startTime;
    private String endTime;
    private Double ratePerMinute;
    private String arrival;
    private String origin;
    private Payment payment;
    private PricingPlan pricingPlan;
    private Reservation reservation;

    public Trip() {}

    public Trip(String origin,Payment payment, PricingPlan pricingPlan){
        this.tripID = UUID.randomUUID().toString();
        this.startTime = LocalTime.now().toString();
        this.origin = origin;
        this.payment = payment;
        this.pricingPlan = pricingPlan;
        this.ratePerMinute = pricingPlan.getRatePerMinute();
    }

    public String getTripID() {return tripID;}
    public String getStartTime() {return startTime;}
    public void setStartTime(String startTime) {this.startTime = startTime;}
    public String getEndTime() {return endTime;}
    public void setEndTime(String endTime) {this.endTime = endTime;}
    public Double getRatePerMinute() {return ratePerMinute;}
    public String getArrival() {return arrival;}
    public void setArrival(String arrival) {this.arrival = arrival;}
    public String getOrigin() {return origin;}
    public void setOrigin(String origin) {this.origin = origin;}
    public Payment getPayment() {return payment;}
    public void setPayment(Payment payment) {this.payment = payment;}
    public PricingPlan getPricingPlan() {return pricingPlan;}
    public void setPricingPlan(PricingPlan pricingPlan) {this.pricingPlan = pricingPlan;}
    public Reservation getReservation() {return reservation;}
    public void setReservation(Reservation reservation) {this.reservation = reservation;}
}
