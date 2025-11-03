package com.TopFounders.domain.Strategy;

import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Trip;

import java.time.Duration;
import java.time.LocalTime;

public class BasicPlanStrategy implements PricingStrategy {
    @Override
    public double calculateTotal(Trip trip) {

        if (trip.getRatePerMinute() == null) {
            System.out.println("Rate per minute not available.");
            return 0.0;
        }

        LocalTime start = LocalTime.parse(trip.getStartTime());
        LocalTime end = LocalTime.parse(trip.getEndTime());

        if (end.isBefore(start)) {
            end = end.plusHours(24);
        }

        Duration duration = Duration.between(start, end);
        double minutes = duration.toMillis() / (1000.0 * 60);

        double amount = trip.getPricingPlan().getBaseFee() + (minutes * trip.getRatePerMinute()) ;
        if (trip.getReservation().getBike().getType() == BikeType.E_BIKE) {
            amount += 20.0;
        }
        return amount;
    }

}
