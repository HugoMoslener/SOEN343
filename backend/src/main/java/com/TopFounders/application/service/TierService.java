package com.TopFounders.application.service;

import com.TopFounders.domain.model.Reservation;
import com.TopFounders.domain.model.ReservationState;
import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.model.Tier;
import com.TopFounders.domain.model.Trip;
import com.TopFounders.application.service.RiderService;
import com.TopFounders.application.service.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TierService {
    private final RiderService riderService;
    private final ReservationService reservationService;
    private final TripService tripService;

    public TierService(RiderService riderService, ReservationService reservationService, TripService tripService) {
        this.riderService = riderService;
        this.reservationService = reservationService;
        this.tripService = tripService;
    }


    public Tier evalutateAndUpdateTier(String riderUsername) throws ExecutionException, InterruptedException {
        Rider rider = riderService.getRiderDetails(riderUsername);
        if (rider == null){
            return null;
        }

        Tier currentTier = rider.getTier();


        return null;
    }

    public Tier determineTier(String riderUsername) throws InterruptedException, ExecutionException {
        // Check from highest to lowest tier
        if (meetsGoldTierCriteria(riderUsername)) {
            return Tier.GOLD;
        } else if (meetsSilverTierCriteria(riderUsername)) {
            return Tier.SILVER;
        } else if (meetsBronzeTierCriteria(riderUsername)) {
            return Tier.BRONZE;
        } else {
            return Tier.ENTRY;
        }
    }

    // BR-001: No missed reservations within the last year
    // BR-002: Returned all bikes successfully
    // BR-003: Surpassed 10 trips in the last year
    private boolean meetsBronzeTierCriteria(String riderUsername) throws ExecutionException, InterruptedException {
        ArrayList<Reservation> allReservations = reservationService.getReservationsByRider(riderUsername);
        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        int tripsLastYear = 0;

        // BR-001: No missed reservations within the last year (must have state = "CONFIRMED")
        boolean noMissedReservations = true;
        for (Reservation reservation : allReservations){
            LocalDate reservationDate = LocalDate.parse(reservation.getDate());
            if (reservationDate.isAfter(oneYearAgo) || reservationDate.isEqual(oneYearAgo)){
                if (reservation.getState() == ReservationState.CANCELLED){
                    noMissedReservations = false;
                    break;
                }
            }
        }
        if (!noMissedReservations) { return false; }

        // BR-002: Returned all bikes successfully (trip must have an endTime)
        for (Trip trip : allTrips) {
            if (trip.getEndTime() == null || trip.getEndTime().isEmpty()) {
                return false;
            }
        }

        // BR-003: Surpassed 10 trips in the last year
        for (Trip trip : allTrips) {
            if (trip.getReservation() != null && trip.getReservation().getDate() != null) {
                LocalDate tripDate = LocalDate.parse(trip.getReservation().getDate());
                if (tripDate.isAfter(oneYearAgo) || tripDate.isEqual(oneYearAgo)) {
                    tripsLastYear++;
                }
            }
        }

        return tripsLastYear > 10;
    }

    // SL-001: Covers Bronze tier eligibility
    // SL-002: At least 5 reservations successfully claimed within the last year
    // SL-003: Surpassed 5 trips per month for the last three months
    private boolean meetsSilverTierCriteria(String riderUsername) throws ExecutionException, InterruptedException {
        // SL-001: Covers Bronze tier eligibility
        if (!meetsBronzeTierCriteria(riderUsername)){ return false; }

        ArrayList<Reservation> allReservations = reservationService.getReservationsByRider(riderUsername);
        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        // SL-002
        int claimedReservations = 0;
        for (Reservation reservation : allReservations){
            LocalDate reservationDate = LocalDate.parse(reservation.getDate());
            if ((reservationDate.isAfter(oneYearAgo) || reservationDate.isEqual(oneYearAgo)) && reservation.getState() == ReservationState.CONFIRMED){
                claimedReservations++;
            }
        }

        if (claimedReservations < 5) { return false; }

        // SL-003
        Map<String, Integer> tripsPerMonth = new HashMap<>();
        for (Trip trip : allTrips) {
            if (trip.getReservation() != null && trip.getReservation().getDate() != null) {
                LocalDate tripDate = LocalDate.parse(trip.getReservation().getDate());
                if (tripDate.isAfter(threeMonthsAgo) || tripDate.isEqual(threeMonthsAgo)) {
                    String monthKey = tripDate.getYear() + "-" + tripDate.getMonthValue();
                    tripsPerMonth.put(monthKey, tripsPerMonth.getOrDefault(monthKey, 0) + 1);
                }
            }
        }

        // Check if all months in the last 3 months have at least 5 trips
        LocalDate current = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            LocalDate monthDate = current.minusMonths(i);
            String monthKey = monthDate.getYear() + "-" + monthDate.getMonthValue();
            int trips = tripsPerMonth.getOrDefault(monthKey, 0);
            if (trips <= 5) {
                return false;
            }
        }

        return true;


    }

    // GL-001: Covers Silver tier eligibility
    // GL-002: Surpasses 5 trips every week for the last 3 months
    private boolean meetsGoldTierCriteria(String riderUsername) throws InterruptedException, ExecutionException {
        // GL-001
        if (!meetsSilverTierCriteria(riderUsername)) {
            return false;
        }

        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        // GL-002
        Map<String, Integer> tripsPerWeek = new HashMap<>();
        for (Trip trip : allTrips) {
            if (trip.getReservation() != null && trip.getReservation().getDate() != null) {
                LocalDate tripDate = LocalDate.parse(trip.getReservation().getDate());
                if (tripDate.isAfter(threeMonthsAgo) || tripDate.isEqual(threeMonthsAgo)) {
                    // Calculate week key (year-week)
                    int weekOfYear = tripDate.get(java.time.temporal.WeekFields.ISO.weekOfYear());
                    String weekKey = tripDate.getYear() + "-W" + weekOfYear;
                    tripsPerWeek.put(weekKey, tripsPerWeek.getOrDefault(weekKey, 0) + 1);
                }
            }
        }

        // Check all weeks in the last 3 months
        LocalDate current = LocalDate.now();
        LocalDate checkDate = threeMonthsAgo;
        while (!checkDate.isAfter(current)) {
            int weekOfYear = checkDate.get(java.time.temporal.WeekFields.ISO.weekOfYear());
            String weekKey = checkDate.getYear() + "-W" + weekOfYear;
            int trips = tripsPerWeek.getOrDefault(weekKey, 0);
            if (trips <= 5) {
                return false;
            }
            checkDate = checkDate.plusWeeks(1);
        }

        return true;
    }

    // Returns the discount percentage based on tier
    public double getDiscountPercentage(Tier tier) {
        return switch (tier) {
            case BRONZE -> 0.05;
            case SILVER -> 0.10;
            case GOLD -> 0.15;
            default -> 0.0;
        };
    }

    // Returns the reservation hold extension in minutes based on tier
    public int getReservationHoldExtensionMinutes(Tier tier) {
        return switch (tier) {
            case SILVER -> 2;
            case GOLD -> 5;
            default -> 0;
        };
    }

    // Applies tier discount to a price
    public double applyDiscount(double originalPrice, Tier tier) {
        double discount = getDiscountPercentage(tier);
        return originalPrice * (1 - discount);
    }


}
