package com.TopFounders.application.service;
import com.TopFounders.domain.model.ReservationState;
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

    private static final Double EXPIRATION_MINUTES = 5.0;
    private final ReservationService reservationService;
    public ReservationExpirationChecker(){
        this.reservationService = new ReservationService();
    }

    @Scheduled(fixedRate = 30000)
    public void checkExpiredReservations() throws ExecutionException, InterruptedException, IllegalStateException {
        ArrayList<Reservation> reservations = reservationService.getAllReservations();

        for (Reservation reservation : reservations) {
            if(reservation.getState().equals(ReservationState.PENDING)) {

                LocalTime storedTime = LocalTime.parse(reservation.getTime());

                LocalTime now = LocalTime.now();

                Duration duration = Duration.between(storedTime, now);

                if (duration.isNegative()) {
                    duration = duration.plusHours(24);
                }

                if (duration.toMinutes() >= 5) {
                    BMS.getInstance().cancelReservation(reservation.getReservationID(), reservation.getRider().getUsername());
                } else {
                }
            }
        }


    }
}
