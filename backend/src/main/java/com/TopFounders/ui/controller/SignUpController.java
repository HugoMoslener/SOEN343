package com.TopFounders.ui.controller;

import com.TopFounders.application.service.BMS;
import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.model.User;
import com.TopFounders.domain.factory.UserFactory;
import com.TopFounders.application.service.OperatorService;
import com.TopFounders.application.service.RiderService;
import com.TopFounders.application.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            String message = BMS.getInstance().saveRiderData(rider.getUsername(),rider.getPaymentInformation(),rider.getEmail(),rider.getFullName(),rider.getAddress(), "rider");
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

    @GetMapping("/setupSystemOperators")
    public String setupOperators() {
        try {
            createOperatorAccount("operator1", "operator1@TopFounders.com", "operator123!", "Operator One", "123 Operator St");
            createOperatorAccount("operator2", "operator2@TopFounders.com", "operator123!", "Operator Two", "456 Operator St");
            return "System operators created successfully!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void createOperatorAccount(String username, String email, String password, String fullName, String address) throws Exception {
        // 1. Create Firebase Auth
        UserRecord.CreateRequest authRequest = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(fullName);
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(authRequest);

        // 2. Set operator role
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "operator");
        FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);

        // 3. Create in Firestore
        UserFactory factory = new UserFactory();
        operatorService.saveOperator((Operator)factory.CreateUser(
                username, "", email, fullName, address, "operator"
        ));
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
    public ResponseEntity<?> getUser(@RequestBody String username) {
        try {
            System.out.println("🔍 Getting user data for: " + username);

            // Remove any quotes that might be sent from frontend
            String cleanUsername = username.replace("\"", "").trim();
            System.out.println("🔍 Cleaned username: " + cleanUsername);

            UserService userService = new UserService();
            User data = userService.getUserDetails(cleanUsername);

            if(data == null) {
                System.out.println("❌ User not found: " + cleanUsername);
                return ResponseEntity.status(404).body("User not found: " + cleanUsername);
            }

            System.out.println("✅ User found: " + data.getUsername() + " with role: " + data.getRole());
            System.out.println("✅ User class: " + data.getClass().getSimpleName());

            if(data.getRole().equals("rider")){
                System.out.println("Returning Rider object");
                return ResponseEntity.ok((Rider)data);
            } else {
                System.out.println("Returning Operator object");
                return ResponseEntity.ok((Operator)data);
            }
        } catch(Exception e) {
            System.out.println("💥 ERROR in getUserData: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/debugUserData")
    public ResponseEntity<?> debugUser(@RequestBody String username) {
        try {
            System.out.println("=== DEBUG USER DATA ===");
            System.out.println("Raw username: " + username);

            String cleanUsername = username.replace("\"", "").trim();
            System.out.println("Clean username: " + cleanUsername);

            UserService userService = new UserService();
            System.out.println("Before getUserDetails call");

            User data = userService.getUserDetails(cleanUsername);
            System.out.println("After getUserDetails call");

            if(data == null) {
                System.out.println("User is null");
                return ResponseEntity.status(404).body("User not found");
            }

            System.out.println("User class: " + data.getClass().getName());
            System.out.println("User role: " + data.getRole());
            System.out.println("User username: " + data.getUsername());

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


}

