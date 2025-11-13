package com.TopFounders.domain.Strategy;

import com.TopFounders.application.service.LinkerDataService;
import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Trip;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PremiumPlanStrategy implements PricingStrategy {
    @Override
    public double calculateTotal(Trip trip) throws ExecutionException, InterruptedException {

        LinkerDataService linkerDataService = new LinkerDataService();
        Map<String, Object> data = linkerDataService.getFlexDollar(trip.getReservation().getRider().getUsername());
        double flexdollar = 0.0;

        if (data == null) {
            System.out.println("User not found");
        } else {
            Object value = data.get("flexdollars");
            if (value instanceof Number) {
                flexdollar = ((Number) value).doubleValue();
            }
        }

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

        if (flexdollar >= amount) {
            flexdollar -= amount;
            amount = 0.0;
        } else {
            amount -= flexdollar;
            flexdollar = 0.0;
        }

        linkerDataService.updateFlexDollar(trip.getReservation().getRider().getUsername(), flexdollar);
        return amount;
    }
}
