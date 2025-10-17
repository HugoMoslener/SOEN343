package com.TopFounders.domain.model;

public class PricingPlan {
    private String planID;
    private String planName;
    private Double ratePerMinute;

    public PricingPlan() {}
    public PricingPlan(String planID, String planName, Double ratePerMinute) {
        this.planID = planID;
        this.planName = planName;
        this.ratePerMinute = ratePerMinute;
    }

    public String getPlanID() {return planID;}
    public void setPlanID(String planID) {this.planID = planID;}
    public String getPlanName() {return planName;}
    public void setPlanName(String planName) {this.planName = planName;}
    public Double getRatePerMinute() {return ratePerMinute;}
    public void setRatePerMinute(Double ratePerMinute) {this.ratePerMinute = ratePerMinute;}
}
