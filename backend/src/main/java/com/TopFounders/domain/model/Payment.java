package com.TopFounders.domain.model;

import java.time.LocalDate;

public class Payment {
    private String paymentMethod;
    private Double amount;
    private String paidDate;
    private String tripID;

    public Payment() {}
    public Payment(String paymentMethod, Double amount) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paidDate = LocalDate.now().toString();
    }

    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}
    public Double getAmount() {return amount;}
    public void setAmount(Double amount) {this.amount = amount;}
    public String getPaidDate() {return paidDate;}
    public void setPaidDate(String paidDate) {this.paidDate = paidDate;}
    public String getTripID() {return tripID;}
    public void setTripID(String tripID) {this.tripID = tripID;}
}
