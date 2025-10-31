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
        else if ( planID.equals("2")){
            setPrincingPlan2();
        }
        else if ( planID.equals("3")){
            setPrincingPlan3();
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
        this.ratePerMinute = 10.0;
        this.baseFee = 15.0;
    }
    public void setPrincingPlan2(){
        this.planID = "2";
        this.planName = "Premium plan";
        this.ratePerMinute = 4.0;
        this.baseFee = 30.0;
    }
    public void setPrincingPlan3(){
        this.planID = "2";
        this.planName = "Premium plan pro";
        this.ratePerMinute = 2.0;
        this.baseFee = 50.0;
    }
}
