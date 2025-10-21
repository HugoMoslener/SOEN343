package com.TopFounders.domain.model;

public class PricingPlan {
    private String planID;
    private String planName;
    private Double ratePerMinute;
    private Double baseFee;

    public PricingPlan() {}
    public PricingPlan(String planID) {
        if(planID.equals("1")){
            setPrincingPlan1();
        }
        else{
            setPrincingPlan2();
        }
    }

    public Double getBaseFee() {
        return baseFee;
    }
    public void setBaseFee(Double baseFee) {this.baseFee = baseFee;}
    public String getPlanID() {return planID;}
    public void setPlanID(String planID) {this.planID = planID;}
    public String getPlanName() {return planName;}
    public void setPlanName(String planName) {this.planName = planName;}
    public Double getRatePerMinute() {return ratePerMinute;}
    public void setRatePerMinute(Double ratePerMinute) {this.ratePerMinute = ratePerMinute;}

    public void setPrincingPlan1(){
        this.planID = "1";
        this.planName = "Base plan";
        this.ratePerMinute = 5.0;
        this.baseFee = 90.0;
    }
    public void setPrincingPlan2(){
        this.planID = "2";
        this.planName = "Premium plan";
        this.ratePerMinute = 10.0;
        this.baseFee = 180.0;
    }
}
