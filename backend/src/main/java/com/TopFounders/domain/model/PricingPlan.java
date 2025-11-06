package com.TopFounders.domain.model;

public class PricingPlan {
    private String planID;
    private String planName;
    private Double ratePerMinute;
    private Double baseFee;

    public PricingPlan() {}
    public PricingPlan(String planID) {

    }

    public void setPlanInfo(String  plansID) {
        if(plansID.equals("1")){
            setPricingPlan1();
        }
        else if ( plansID.equals("2")){
            setPricingPlan2();
        }
        else if ( plansID.equals("3")){
            setPricingPlan3();
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

    public void setPricingPlan1(){
        this.planID = "1";
        this.planName = "Base plan";
        this.ratePerMinute = 10.0;
        this.baseFee = 15.0;
    }
    public void setPricingPlan2(){
        this.planID = "2";
        this.planName = "Premium plan";
        this.ratePerMinute = 4.0;
        this.baseFee = 30.0;
    }
    public void setPricingPlan3(){
        this.planID = "2";
        this.planName = "Premium plan pro";
        this.ratePerMinute = 2.0;
        this.baseFee = 50.0;
    }
}
