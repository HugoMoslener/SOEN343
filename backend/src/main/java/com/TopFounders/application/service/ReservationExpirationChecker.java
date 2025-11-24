package com.TopFounders.application.service;
import com.TopFounders.domain.model.ReservationState;
import com.TopFounders.domain.model.Tier;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.TopFounders.domain.model.Reservation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
public class ReservationExpirationChecker {

    private final ReservationService reservationService;
    private final TierService tierService;
    private final BMS bms;

    public ReservationExpirationChecker(ReservationService reservationService, TierService tierService, BMS bms){
        this.reservationService = reservationService;
        this.tierService = tierService;
        this.bms = bms;
    }

    @Scheduled(fixedRate = 30000)
    public void checkExpiredReservations() throws ExecutionException, InterruptedException, IllegalStateException {
        ArrayList<Reservation> reservations = reservationService.getAllReservations();

        for (Reservation reservation : reservations) {
            if(reservation.getState().equals(ReservationState.PENDING)) {

                // Get rider's tier and calculate total hold time
                String username = reservation.getRider().getUsername();
                Tier riderTier = tierService.determineTier(username);
                int totalHoldMinutes = 5 + tierService.getReservationHoldExtensionMinutes(riderTier);

                LocalTime storedTime = LocalTime.parse(reservation.getTime());

                LocalTime now = LocalTime.now();

                Duration duration = Duration.between(storedTime, now);

                if (duration.isNegative()) {
                    duration = duration.plusHours(24);
                }

                if (duration.toMinutes() >= totalHoldMinutes) {
                    bms.cancelReservation(reservation.getReservationID(), username);
                }
            }
        }


    }
}
