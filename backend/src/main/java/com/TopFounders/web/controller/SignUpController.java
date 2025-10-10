package com.TopFounders.web.controller;

import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.service.RiderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/signIn")
public class SignUpController {

    private final RiderService riderService = new RiderService();

    @GetMapping("/hello")
    public String getHello(){
        return "Hello Word";
    }

    @PostMapping("/saveRider")
    public String saveData(@RequestBody Rrider rider ){
        try{
            System.out.println("Post request reached here");
            String message = riderService.saveRider(new Rider(rider.getUsername(),rider.getPaymentInformation(),rider.getEmail(),rider.getFullName(),rider.getAddress(), "rider"));
            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/getRiderData")
    public Rider getRider(@RequestBody String username ){
        try {
            Rider data = riderService.getRiderDetails(username);
            return data;
        }
        catch(Exception e) {
            throw new RuntimeException("Something went wrong!");}

    }

    @PostMapping("/deleteRider")
    public String deleteRider(@RequestBody String username ){
        try {
            String data = riderService.deleteRider(username);
            return data;
        }
        catch(Exception e) {
            throw new RuntimeException("Something went wrong!");}

    }


}

