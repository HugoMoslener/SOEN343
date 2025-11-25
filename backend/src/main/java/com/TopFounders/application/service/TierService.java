package com.TopFounders.application.service;

import com.TopFounders.domain.model.Reservation;
import com.TopFounders.domain.model.ReservationState;
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

    public TierService(RiderService riderService, ReservationService reservationService, TripService tripService) {
        this.reservationService = reservationService;
        this.tripService = tripService;
    }

    // Determines the highest tier a rider qualifies for
    // Checks from highest to lowest tier
    public Tier determineTier(String riderUsername) throws InterruptedException, ExecutionException {
        try {
            if (meetsGoldTierCriteria(riderUsername)) {
                return Tier.GOLD;
            } else if (meetsSilverTierCriteria(riderUsername)) {
                return Tier.SILVER;
            } else if (meetsBronzeTierCriteria(riderUsername)) {
                return Tier.BRONZE;
            } else {
                return Tier.ENTRY;
            }
        } catch (IllegalStateException e) {
            // Firebase not initialized (e.g., in unit tests) - return ENTRY tier
            return Tier.ENTRY;
        } catch (Exception e) {
            // Any other exception (e.g., Firebase connection issues) - return ENTRY tier
            return Tier.ENTRY;
        }
    }


    // BR-001: No missed (cancelled) reservations within last year
    // BR-002: Returned all bikes successfully (all trips have endTime)
    // BR-003: At least 10 trips in the last year
    private boolean meetsBronzeTierCriteria(String riderUsername) throws ExecutionException, InterruptedException {
        ArrayList<Reservation> allReservations = reservationService.getReservationsByRider(riderUsername);
        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        int tripsLastYear = 0;

        // BR-001: No cancelled reservations within last year
        boolean noMissedReservations = true;
        for (Reservation reservation : allReservations) {
            if (reservation.getDate() == null) continue;
            LocalDate reservationDate = LocalDate.parse(reservation.getDate());
            if (reservationDate.isAfter(oneYearAgo) || reservationDate.isEqual(oneYearAgo)) {
                if (reservation.getState() == ReservationState.CANCELLED) {
                    noMissedReservations = false;
                    break;
                }
            }
        }
        System.out.println("No missed reservations: " + noMissedReservations);
        if (!noMissedReservations) return false;

        // BR-002: All bikes returned successfully (all trips have endTime)
        for (Trip trip : allTrips) {
            if (trip.getEndTime() == null || trip.getEndTime().isEmpty()) {
                System.out.println("Trip has no endTime");
                return false;
            }
        }

        // BR-003: At least 10 trips in last year
        System.out.println("=== BR-003 DEBUG ===");
        System.out.println("Total trips retrieved: " + allTrips.size());
        System.out.println("Total reservations retrieved: " + allReservations.size());
        System.out.println("One year ago date: " + oneYearAgo);

        for (Trip trip : allTrips) {
            System.out.println("\n--- Checking Trip ---");
            System.out.println("Trip ID: " + trip.getTripID());
            System.out.println("Trip startTime: " + trip.getStartTime());
            System.out.println("Trip endTime: " + trip.getEndTime());

            // Find the corresponding reservation by tripID
            Reservation matchingReservation = null;
            for (Reservation res : allReservations) {
                System.out.println("  Comparing with Reservation ID: " + res.getReservationID() + ", TripID: " + res.getTripID());
                if (res.getTripID() != null && res.getTripID().equals(trip.getTripID())) {
                    matchingReservation = res;
                    System.out.println("  ✅ MATCH FOUND!");
                    break;
                }
            }

            if (matchingReservation != null) {
                System.out.println("Matching reservation found!");
                System.out.println("Reservation date: " + matchingReservation.getDate());
                System.out.println("Reservation state: " + matchingReservation.getState());

                if (matchingReservation.getDate() != null) {
                    LocalDate tripDate = LocalDate.parse(matchingReservation.getDate());
                    System.out.println("Parsed trip date: " + tripDate);
                    System.out.println("Is after one year ago? " + tripDate.isAfter(oneYearAgo));
                    System.out.println("Is equal to one year ago? " + tripDate.isEqual(oneYearAgo));

                    if (tripDate.isAfter(oneYearAgo) || tripDate.isEqual(oneYearAgo)) {
                        tripsLastYear++;
                        System.out.println("✅ Trip counted! Total now: " + tripsLastYear);
                    } else {
                        System.out.println("❌ Trip too old, not counted");
                    }
                } else {
                    System.out.println("❌ Reservation date is NULL");
                }
            } else {
                System.out.println("❌ No matching reservation found for this trip");
            }
        }

        System.out.println("\n=== FINAL COUNT ===");
        System.out.println("Trips in the last year: " + tripsLastYear);
        return tripsLastYear >= 10;
    }


    // SL-001: Meets all Bronze tier requirements
    // SL-002: At least 5 successfully confirmed reservations in last year
    // SL-003: Average of 5+ trips per month for last 3 months
    private boolean meetsSilverTierCriteria(String riderUsername) throws ExecutionException, InterruptedException {
        // SL-001: Must meet Bronze tier first
        if (!meetsBronzeTierCriteria(riderUsername)) return false;

        ArrayList<Reservation> allReservations = reservationService.getReservationsByRider(riderUsername);
        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        // SL-002: Count confirmed reservations in last year
        int confirmedReservations = 0;
        for (Reservation reservation : allReservations) {
            if (reservation.getDate() == null) continue;
            LocalDate reservationDate = LocalDate.parse(reservation.getDate());
            if ((reservationDate.isAfter(oneYearAgo) || reservationDate.isEqual(oneYearAgo)) && reservation.getState() == ReservationState.CONFIRMED) {
                confirmedReservations++;
            }
        }

        System.out.println("Confirmed Reservations: " + confirmedReservations);
        if (confirmedReservations < 5) return false;

        // SL-003: Verify 5+ trips in each of last 3 months
        Map<String, Integer> tripsPerMonth = new HashMap<>();
        for (Trip trip : allTrips) {
            if (trip.getReservation() != null && trip.getReservation().getDate() != null) {
                LocalDate tripDate = LocalDate.parse(trip.getReservation().getDate());
                if (tripDate.isAfter(threeMonthsAgo) || tripDate.isEqual(threeMonthsAgo)) {
                    String monthKey = tripDate.getYear() + "-" + String.format("%02d", tripDate.getMonthValue());
                    tripsPerMonth.put(monthKey, tripsPerMonth.getOrDefault(monthKey, 0) + 1);
                }
            }
        }

        // Check each of the last 3 months has at least 5 trips
        LocalDate current = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            LocalDate monthDate = current.minusMonths(i);
            String monthKey = monthDate.getYear() + "-" + String.format("%02d", monthDate.getMonthValue());
            int trips = tripsPerMonth.getOrDefault(monthKey, 0);
            if (trips < 5) {
                return false;
            }
        }

        return true;
    }


    // GL-001: Meets all Silver tier requirements
    // GL-002: 5+ trips in each week for last 3 months
    private boolean meetsGoldTierCriteria(String riderUsername) throws InterruptedException, ExecutionException {
        // GL-001: Must meet Silver tier first
        if (!meetsSilverTierCriteria(riderUsername)) return false;

        ArrayList<Trip> allTrips = tripService.getTripsByRider(riderUsername);
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        // GL-002: Count trips per week
        Map<String, Integer> tripsPerWeek = new HashMap<>();
        for (Trip trip : allTrips) {
            if (trip.getReservation() != null && trip.getReservation().getDate() != null) {
                LocalDate tripDate = LocalDate.parse(trip.getReservation().getDate());
                if (tripDate.isAfter(threeMonthsAgo) || tripDate.isEqual(threeMonthsAgo)) {
                    int weekOfYear = tripDate.get(java.time.temporal.WeekFields.ISO.weekOfYear());
                    String weekKey = tripDate.getYear() + "-W" + String.format("%02d", weekOfYear);
                    tripsPerWeek.put(weekKey, tripsPerWeek.getOrDefault(weekKey, 0) + 1);
                }
            }
        }

        // Verify 5+ trips in each week of last 3 months
        LocalDate current = LocalDate.now();
        LocalDate checkDate = threeMonthsAgo;
        while (!checkDate.isAfter(current)) {
            int weekOfYear = checkDate.get(java.time.temporal.WeekFields.ISO.weekOfYear());
            String weekKey = checkDate.getYear() + "-W" + String.format("%02d", weekOfYear);
            int trips = tripsPerWeek.getOrDefault(weekKey, 0);
            if (trips < 5) {
                return false;
            }
            checkDate = checkDate.plusWeeks(1);
        }

        return true;
    }

    // ===== TIER BENEFITS =====

    public double getDiscountPercentage(Tier tier) {
        return switch (tier) {
            case BRONZE -> 0.05;
            case SILVER -> 0.10;
            case GOLD -> 0.15;
            default -> 0.0;
        };
    }

    public int getReservationHoldExtensionMinutes(Tier tier) {
        return switch (tier) {
            case SILVER -> 2;
            case GOLD -> 5;
            default -> 0;
        };
    }

    public double applyDiscount(double originalPrice, Tier tier) {
        double discount = getDiscountPercentage(tier);
        return originalPrice * (1 - discount);
    }
}