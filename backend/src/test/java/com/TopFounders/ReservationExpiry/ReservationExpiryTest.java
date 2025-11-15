package com.TopFounders.ReservationExpiry;

import com.TopFounders.application.service.*;
import com.TopFounders.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ReservationExpiryTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private BikeService bikeService;

    @Mock
    private DockService dockService;

    @Mock
    private StationService stationService;

    private BMS bms;

    @Test
    void cancelReservation_ShouldCancelExpiredReservation_AndMakeBikeAvailable() throws Exception {

        // Create BMS with mocks injected manually
        bms = BMS.getInstance();

        String reservationId = "R123";
        String username = "john";

        Reservation reservation = mock(Reservation.class);
        Rider rider = mock(Rider.class);
        Bike bike = mock(Bike.class);
        Dock dock = mock(Dock.class);
        Station station = mock(Station.class);

        // Reservation lookup
        when(reservationService.getReservationDetails(reservationId)).thenReturn(reservation);
        when(reservation.getRider()).thenReturn(rider);
        when(rider.getUsername()).thenReturn(username);

        // Bike
        when(reservation.getBike()).thenReturn(bike);
        when(bike.getBikeID()).thenReturn("B1");
        when(bikeService.getBikeDetails("B1")).thenReturn(bike);

        // FIXED DOCK ID
        when(bike.getDockID()).thenReturn("S-01-D3");
        when(dockService.getDockDetails("S-01-D3")).thenReturn(dock);

        // Station lookup
        when(stationService.getStationDetails("S-01")).thenReturn(station);

        // Act
        String result = bms.cancelReservation(reservationId, username);

        // Assert
        assertEquals("Successful", result);
        verify(bike).returnBike();
        verify(dock).setBike(bike);
        verify(station).updateADock(dock);
        verify(reservation).setState(ReservationState.CANCELLED);
        verify(reservation).setBike(bike);
        verify(reservationService).updateReservationDetails(reservation);
        verify(bikeService).updateBikeDetails(bike);
        verify(dockService).updateDockDetails(dock);
        verify(stationService).updateStationDetails(station);
        verify(reservation).notifySubscribers("RESERVATION_EXPIRED");
    }
}
