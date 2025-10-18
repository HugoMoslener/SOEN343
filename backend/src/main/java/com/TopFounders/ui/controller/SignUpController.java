package com.TopFounders.ui.controller;

import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.model.User;
import com.TopFounders.domain.factory.UserFactory;
import com.TopFounders.application.service.OperatorService;
import com.TopFounders.application.service.RiderService;
import com.TopFounders.application.service.UserService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/signIn")
public class SignUpController {

    private final RiderService riderService = new RiderService();
    private final OperatorService operatorService = new OperatorService();

    @GetMapping("/hello")
    public String getHello(){
        return "Hello Word";
    }

    @PostMapping("/saveRider")
    public String saveData(@RequestBody RiderHelperClass rider ){
        try{
            System.out.println("Post request reached here");
            UserFactory factory = new UserFactory();
            String message = riderService.saveRider((Rider)factory.CreateUser(rider.getUsername(),rider.getPaymentInformation(),rider.getEmail(),rider.getFullName(),rider.getAddress(), "rider"));
            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/saveOperator")
    public String saveDataOperator(@RequestBody OperatorHelperClass operator ){
        try{
            System.out.println("Post request reached here");
            UserFactory factory = new UserFactory();
            String message = operatorService.saveOperator((Operator)factory.CreateUser(operator.getUsername(),"",operator.getEmail(),operator.getFullName(),operator.getAddress(), "operator"));
            return "true";
        }
        catch (Exception e) {
            return "false";
        }

    }

    @PostMapping("/getRiderData")
    public Rider getRider(@RequestBody String username ){
        try {
            System.out.println(username);
            Rider data = riderService.getRiderDetails(username);
            if(data==null){
                System.out.println("Some");
            }
            System.out.println(data.getPaymentInformation());
            System.out.println(data.getEmail());
            System.out.println("Some");
            return data;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage()+" : " + e.getCause());}

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

    @PostMapping("/getUserData")
    public User getUser(@RequestBody String username ){
        try {
            User data = new UserService().getUserDetails(username);
                if(data.getRole().equals("rider")){
                    return ((Rider)data);
                }
                return ((Operator)data);
        }
        catch(Exception e) {
            throw new RuntimeException("Something went wrong!");}

    }


}

