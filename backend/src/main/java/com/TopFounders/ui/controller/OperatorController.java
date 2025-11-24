package com.TopFounders.ui.controller;

import com.TopFounders.application.service.OperatorService;
import com.TopFounders.application.service.RiderService;
import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/operator")
public class OperatorController {

    private final OperatorService operatorService;
    private final RiderService riderService;

    public OperatorController() {
        this.operatorService = new OperatorService();
        this.riderService = new RiderService();
    }

    /**
     * 1. Get the rider linked to an operator
     * Called when the Operator presses “Switch to Rider”
     */
    @GetMapping("/{username}/rider")
    public Rider getLinkedRider(@PathVariable String username) {
        try {
            System.out.println("Checking linked rider for operator: " + username);

            Rider rider = operatorService.getRiderFromOperator(username);
            if (rider == null) {
                System.out.println("Operator has no linked rider.");
            } else {
                System.out.println("Found rider: " + rider.getUsername());
            }
            return rider;
        } catch (Exception e) {
            System.out.println("Error in getLinkedRider: " + e.getMessage());
            return null;
        }
    }

    /**
     * 2. Create a rider for an operator
     * Called when operator switches roles for the first time and enters payment info
     */
    @PostMapping("/{username}/createRider")
    public Rider createRiderForOperator(@PathVariable String username,
                                        @RequestBody Map<String, String> body) {
        try {
            String paymentInfo = body.get("paymentInformation");

            System.out.println("Creating new rider for operator: " + username +
                    " with payment info: " + paymentInfo);

            Rider rider = operatorService.createRiderForOperator(username, paymentInfo);

            System.out.println("Rider created: " + rider.getUsername());
            return rider;
        } catch (Exception e) {
            System.out.println("Error creating rider for operator: " + e.getMessage());
            return null;
        }
    }

    @GetMapping("/rider/{riderUsername}/operator")
    public ResponseEntity<?> getOperatorFromRider(@PathVariable String riderUsername) {
        try {
            // Only operator-created riders are allowed
            if (!riderUsername.contains("operator")) {
                return ResponseEntity.status(403).body("This rider cannot switch to an operator.");
            }

            // Extract operator username ("operator1-rider" → "operator1")
            String operatorUsername = riderUsername.split("-")[0];

            Operator operator = operatorService.getOperatorDetails(operatorUsername);

            if (operator == null) {
                return ResponseEntity.status(404).body("Linked operator not found");
            }

            return ResponseEntity.ok(operator);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/switchToOperator")
    public ResponseEntity<?> switchToOperator(@RequestBody String riderUsername) {
        try {
            riderUsername = riderUsername.replace("\"", "").trim();

            if (!riderUsername.contains("operator")) {
                return ResponseEntity.status(403).body("Forbidden");
            }

            String operatorUsername = riderUsername.split("-")[0];

            Operator operator = operatorService.getOperatorDetails(operatorUsername);

            if (operator == null) {
                return ResponseEntity.status(404).body("Operator not found");
            }

            return ResponseEntity.ok(operator);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}