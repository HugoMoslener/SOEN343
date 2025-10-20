package com.TopFounders.ui.controller;

import com.TopFounders.application.service.*;
import com.TopFounders.domain.model.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api/action")
public class ActionController {

    private final BikeService bikeService;
    private final DockService dockService;
    private final StationService stationService;
    private final RiderService riderService;

    public ActionController(BikeService bikeService, DockService dockService, StationService stationService, RiderService riderService) {
        this.bikeService = bikeService;
        this.dockService = dockService;
        this.stationService = stationService;
        this.riderService = riderService;
    }


    @PostMapping("/reserveBike")
    public String BikeReservation(@RequestBody ReservationHelperClass reservationHelperClass ){
        try{
            System.out.println("Post request reached here");
            MapService.getInstance().setStations(stationService.getAllStations());
            System.out.println("hello" + reservationHelperClass.getStationName()+": "+reservationHelperClass.getBikeID()+":" + reservationHelperClass.getRiderID());
            Rider rider = riderService.getRiderDetails(reservationHelperClass.getRiderID());
            String message = rider.reserveBike(reservationHelperClass.getStationName(), rider, reservationHelperClass.getBikeID(), reservationHelperClass.getRiderID());
            System.out.println(message);
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/undockBike")
    public String BikeUndocking(@RequestBody UndockingHelperClass undockingHelperClass ){
        try{
            System.out.println("Post request reached here");
            MapService.getInstance().setStations(stationService.getAllStations());
            System.out.println("hello" +undockingHelperClass.getRiderID()+":" + undockingHelperClass.getRiderID());
            Rider rider = riderService.getRiderDetails(undockingHelperClass.getRiderID());
            String message1 = rider.undockBike(undockingHelperClass.getRiderID(), undockingHelperClass.getReservationID());
            return message1;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/dockBike")
    public String BikeDocking(@RequestBody DockingHelperClass dockingHelperClass ){
        try{
            System.out.println("Post request reached here" + dockingHelperClass.getDockID() +":"+dockingHelperClass.getReservationID()+":"+dockingHelperClass.getRiderID());
            MapService.getInstance().setStations(stationService.getAllStations());
            String message1 = BMS.getInstance().dockBike(dockingHelperClass.getRiderID(),dockingHelperClass.getReservationID(),dockingHelperClass.getDockID());
            return message1;
        }
        catch (Exception e) {
            return "false";
        }

    }


    @PostMapping("/cancelReserveBike")
    public String CancelBikeReservation(@RequestBody CancelReservationHelperClass cancelReservationHelperClass ){
        try{
            System.out.println("Post request reached here");
            MapService.getInstance().setStations(stationService.getAllStations());
            Rider rider = riderService.getRiderDetails(cancelReservationHelperClass.getRiderID());
            String message = rider.cancelBikeReservation(cancelReservationHelperClass.getReservationID(), cancelReservationHelperClass.getRiderID());
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/moveABikefromDockAToDockB")
    public String moveABikefromDockAToDockB(@RequestBody MoveABikeHelperClass moveABikeHelperClass ){
        try{
            System.out.println("Post request reached here");
            Dock dock1 = dockService.getDockDetails(moveABikeHelperClass.getDock1ID());
            Dock dock2 = dockService.getDockDetails(moveABikeHelperClass.getDock2ID());
            Bike bike = bikeService.getBikeDetails(moveABikeHelperClass.getBikeID());

            String message = BMS.getInstance().moveABikefromDockAToDockB(dock1,dock2,bike);
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/setAStationAsOutOfService")
    public String setAStationAsOutOfService(@RequestBody String stationID ){
        try{
            System.out.println("Post request reached here");
            Station station = stationService.getStationDetails(stationID);
            String message = BMS.getInstance().setAStationAsOutOfService(station);
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/setABikeAsMaintenance")
    public String setABikeAsMaintenance(@RequestBody String BikeID ){
        try{
            System.out.println("Post request reached here");
            Bike bike = bikeService.getBikeDetails(BikeID);
            String message = BMS.getInstance().setABikeAsMaintenance(bike);
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/setAStationAsActive")
    public String setAStationAsActive(@RequestBody String stationID ){
        try{
            System.out.println("Post request reached here");
            Station station = stationService.getStationDetails(stationID);
            String message = BMS.getInstance().setAStationAsActive(station);
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/setABikeAsAvailable")
    public String setABikeAsAvailable(@RequestBody String BikeID ){
        try{
            System.out.println("Post request reached here");
            Bike bike = bikeService.getBikeDetails(BikeID);
            String message = BMS.getInstance().setABikeAsAvailable(bike);
            return message;
        }
        catch (Exception e) {
            return "false";
        }

    }


}
