package com.TopFounders.ui.controller;

import com.TopFounders.application.service.BikeService;
import com.TopFounders.application.service.DockService;
import com.TopFounders.application.service.StationService;
import com.TopFounders.domain.factory.UserFactory;
import com.TopFounders.domain.model.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/create")
public class CreationController {

    private final BikeService bikeService;
    private final DockService dockService;
    private final StationService stationService;

    public CreationController(BikeService bikeService, DockService dockService, StationService stationService) {
        this.bikeService = bikeService;
        this.dockService = dockService;
        this.stationService = stationService;
    }

    @PostMapping("/saveBike")
    public String saveBikeData(@RequestBody String id ){
        try{
            System.out.println("Post request reached here");
            String message = bikeService.saveBike(new Bike(id, BikeType.STANDARD));

            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/saveDock")
    public String saveDockData(@RequestBody DockHelperClass dock ){
        try{
            System.out.println("Post request reached here");
            String message = dockService.saveDock(new Dock(dock.getDockID(), dock.getStationID()));
            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/saveStation")
    public String saveDockData(@RequestBody StationHelperClass station ){
        try{
            System.out.println("Post request reached here");
            Station station2 = new Station(station.getStationID(), station.getName(), station.getLatitude(),station.getLongitude(), station.getAddress(), station.getCapacity());

            for(Dock dock : station2.getDocks()){
                Bike bike = new Bike(dock.getDockID(), BikeType.STANDARD);
                bike.setDock(dock.getDockID());
                bikeService.saveBike(bike);
                dock.setBike(bike);
                dock.setState(DockState.OCCUPIED);
                dockService.saveDock(dock);
            }
            stationService.saveStation(station2);
            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

    @GetMapping("/setUpDataCorrectly")
    public String setUpData(){
        try{
            System.out.println("Post request reached here");

            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

}
