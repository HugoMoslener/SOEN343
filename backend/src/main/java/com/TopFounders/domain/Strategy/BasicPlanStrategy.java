package com.TopFounders.domain.Strategy;

import com.TopFounders.domain.model.BikeType;
import com.TopFounders.domain.model.Trip;
import java.util.HashMap;
import java.util.Map;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ExecutionException;

import com.TopFounders.application.service.LinkerDataService;

public class BasicPlanStrategy implements PricingStrategy {
    @Override
    public double calculateTotal(Trip trip) throws ExecutionException, InterruptedException {
        LinkerDataService linkerDataService = new LinkerDataService();
        Map<String, Object> data = linkerDataService.getFlexDollar(trip.getReservation().getRider().getUsername());
        double flexdollar = 0.0;

        if (data == null) {
            System.out.println("User not found");
        } else {
            System.out.println("flexdollar not yet reached");
            Object value = data.get("flexdollars");
            if (value instanceof Number) {
                flexdollar = ((Number) value).doubleValue();
            }
            System.out.println("flexdolalr" + flexdollar);
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
        if (trip.getReservation().getBike().getType() == BikeType.E_BIKE) {
            amount += 20.0;
        }

        if (flexdollar >= amount) {
            flexdollar = flexdollar - amount;
            amount = 0.0;
        } else {
            amount = amount - flexdollar;
            flexdollar = 0.0;
        }

        linkerDataService.updateFlexDollar(trip.getReservation().getRider().getUsername(), flexdollar);
        return amount;
    }

}
