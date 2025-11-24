package com.TopFounders.domain.model;

import com.TopFounders.application.service.BMS;
import com.TopFounders.domain.observer.Publisher;
import com.TopFounders.domain.observer.Subscriber;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trip implements Publisher {
    private String tripID;
    private String startTime;
    private String endTime;
    private Double ratePerMinute;
    private String arrival;
    private String origin;
    private Payment payment;
    private PricingPlan pricingPlan;
    private Reservation reservation;
    private double flexdollarApplied = 0.0;
    private List<Subscriber> subscribers = new ArrayList<>();

    public Trip() {}

    public Trip(String origin,Payment payment, PricingPlan pricingPlan){
        this.tripID = UUID.randomUUID().toString();
        this.startTime = LocalTime.now().toString();
        this.origin = origin;
        this.payment = payment;
        this.pricingPlan = pricingPlan;
        this.ratePerMinute = pricingPlan.getRatePerMinute();
    }
    public double getFlexdollarApplied() {return flexdollarApplied;}
    public void setFlexdollarApplied(double flexdollarApplied) {this.flexdollarApplied = flexdollarApplied;}
    public String getTripID() {return tripID;}
    public String getStartTime() {return startTime;}
    public void setStartTime(String startTime) {this.startTime = startTime;}
    public String getEndTime() {return endTime;}
    public void setEndTime(String endTime) {this.endTime = endTime;}
    public Double getRatePerMinute() {return ratePerMinute;}
    public void setRatePerMinute(Double ratePerMinute) {this.ratePerMinute = ratePerMinute;}
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
    @Override
    public void subscribe(Subscriber subscriber){
        subscribers.add(subscriber);
    }
    @Override
    public void unsubscribe(Subscriber subscriber){
        subscribers.remove(subscriber);
    }
    @Override
    public void notifySubscribers(String eventType){
        subscribe(BMS.getInstance());
        for (Subscriber subscriber : subscribers){
            subscriber.update(eventType, this);
        }
    }
}
