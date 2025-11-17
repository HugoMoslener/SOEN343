package com.TopFounders.application.service;

import com.TopFounders.domain.model.Reservation;
import com.TopFounders.domain.model.ReservationState;
import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.model.Tier;
import com.TopFounders.domain.model.Trip;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class TierService {
    private final ReservationService reservationService;
    private final TripService tripService;
    private final RiderService riderService;

    public TierService() {
        this.reservationService = new ReservationService();
        this.tripService = new TripService();
        this.riderService = new RiderService();
    }

    public String evaluateAndUpdateTier(String riderUsername) throws InterruptedException, ExecutionException {
        Rider rider = riderService.getRiderDetails(riderUsername);
        if (rider == null) {
            return null;
        }

        Tier currentTier = rider.getTier() != null ? rider.getTier() : Tier.ENTRY;
        Tier newTier = determineTier(riderUsername);

        // Returns a notification message if tier changed
        if (!currentTier.equals(newTier)) {
            rider.setTier(newTier);
            riderService.updateRiderDetails(rider);

            if (newTier.ordinal() > currentTier.ordinal()) {
                return "Congratulations! You have been upgraded to " + newTier.name() + " tier!";
            } else {
                return "Your tier has been downgraded to " + newTier.name() + " tier.";
            }
        }

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
    private boolean meetsBronzeTierCriteria(String riderUsername) throws InterruptedException, ExecutionException {
        ArrayList<Reservation> allReservations = reservationService.getReservationsByRider(riderUsername);
        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        // BR-001
        boolean noMissedReservations = true;
        int tripsLastYear = 0;

        for (Reservation reservation : allReservations) {
            if (reservation.getDate() != null) {
                LocalDate reservationDate = LocalDate.parse(reservation.getDate());
                if (reservationDate.isAfter(oneYearAgo) || reservationDate.isEqual(oneYearAgo)) {
                    // Check if reservation was cancelled (missed)
                    if (reservation.getState() == ReservationState.CANCELLED) {
                        noMissedReservations = false;
                        break;
                    }
                }
            }
        }

        if (!noMissedReservations) {
            return false;
        }

        // BR-002
        for (Trip trip : allTrips) {
            if (trip.getEndTime() == null || trip.getEndTime().isEmpty()) {
                return false; // At least one trip not completed
            }
        }

        // BR-003
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
    private boolean meetsSilverTierCriteria(String riderUsername) throws InterruptedException, ExecutionException {
        // SL-001
        if (!meetsBronzeTierCriteria(riderUsername)) {
            return false;
        }

        ArrayList<Reservation> allReservations = reservationService.getReservationsByRider(riderUsername);
        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        // SL-002
        int claimedReservations = 0;
        for (Reservation reservation : allReservations) {
            if (reservation.getDate() != null) {
                LocalDate reservationDate = LocalDate.parse(reservation.getDate());
                if ((reservationDate.isAfter(oneYearAgo) || reservationDate.isEqual(oneYearAgo)) &&
                    reservation.getState() == ReservationState.CONFIRMED &&
                    reservation.getTripID() != null && !reservation.getTripID().isEmpty()) {
                    claimedReservations++;
                }
            }
        }

        if (claimedReservations < 5) {
            return false;
        }

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
        switch (tier) {
            case BRONZE:
                return 0.05;
            case SILVER:
                return 0.10;
            case GOLD:
                return 0.15;
            default:
                return 0.0;
        }
    }

    // Returns the reservation hold extension in minutes based on tier
    public int getReservationHoldExtensionMinutes(Tier tier) {
        switch (tier) {
            case SILVER:
                return 2;
            case GOLD:
                return 5;
            default:
                return 0;
        }
    }

    // Applies tier discount to a price
    public double applyDiscount(double originalPrice, Tier tier) {
        double discount = getDiscountPercentage(tier);
        return originalPrice * (1 - discount);
    }
}
