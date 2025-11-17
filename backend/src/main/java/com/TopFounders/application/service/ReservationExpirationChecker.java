package com.TopFounders.application.service;
import com.TopFounders.domain.model.ReservationState;
import com.TopFounders.domain.model.Reservation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
public class ReservationExpirationChecker {

    private static final Double BASE_EXPIRATION_MINUTES = 5.0;
    private final ReservationService reservationService;
    private final TierService tierService;
    private final RiderService riderService;
    
    public ReservationExpirationChecker(){
        this.reservationService = new ReservationService();
        this.tierService = new TierService();
        this.riderService = new RiderService();
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

                // Get tier-based expiration time
                double expirationMinutes = BASE_EXPIRATION_MINUTES;
                try {
                    if (reservation.getRider() != null && reservation.getRider().getUsername() != null) {
                        com.TopFounders.domain.model.Rider rider = riderService.getRiderDetails(reservation.getRider().getUsername());
                        if (rider != null && rider.getTier() != null) {
                            int extensionMinutes = tierService.getReservationHoldExtensionMinutes(rider.getTier());
                            expirationMinutes = BASE_EXPIRATION_MINUTES + extensionMinutes;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error getting tier for reservation expiration: " + e.getMessage());
                }

                if (duration.toMinutes() >= expirationMinutes) {
                    BMS.getInstance().cancelReservation(reservation.getReservationID(), reservation.getRider().getUsername());
                } else {
                }
            }
        }


    }
}
