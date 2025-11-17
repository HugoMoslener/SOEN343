package com.TopFounders.application.service;
import com.TopFounders.domain.model.Rider;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class RiderService {
    private static final String User_Collection="users";

    public RiderService(){}

    public String saveRider(Rider rider) throws ExecutionException, InterruptedException {

        try {
            System.out.println("=== Saving Rider to Firestore ===");
            System.out.println("Username: " + rider.getUsername());
            System.out.println("Email: " + rider.getEmail());
            System.out.println("Full Name: " + rider.getFullName());
            System.out.println("Address: " + rider.getAddress());
            System.out.println("Role: " + rider.getRole());
            System.out.println("Payment Info: " + rider.getPaymentInformation());
            System.out.println("Tier: " + (rider.getTier() != null ? rider.getTier().name() : "null"));
            
            Firestore db = FirestoreClient.getFirestore();
            if (db == null) {
                throw new RuntimeException("Firestore database is null!");
            }
            System.out.println("Firestore instance obtained: " + db);
            
            ApiFuture<WriteResult> result = db.collection(User_Collection)
                    .document(rider.getUsername())
                    .set(rider);
            
            System.out.println("Write operation initiated, waiting for result...");
            WriteResult result1 = result.get();
            System.out.println("Rider saved successfully at: " + result1.getUpdateTime());
            System.out.println("Document ID: " + rider.getUsername());
            
            // Verify the document was created
            DocumentReference docRef = db.collection(User_Collection).document(rider.getUsername());
            DocumentSnapshot verifyDoc = docRef.get().get();
            if (verifyDoc.exists()) {
                System.out.println("✓ Verification: Document exists in Firestore");
                System.out.println("Document data: " + verifyDoc.getData());
            } else {
                System.out.println("✗ WARNING: Document was not found after save operation!");
            }
            
            System.out.println("=== Rider Save Complete ===");
            
            return "User saved at: " + result1.getUpdateTime();
        } catch (Exception e) {
            System.out.println("ERROR in saveRider: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Failed to save rider: " + e.getMessage(), e);
        }
    }

    public Rider getRiderDetails(String username) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(User_Collection).document(username);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Rider rider = null;

        if(document.exists()) {
            rider = document.toObject(Rider.class);
            System.out.println(rider.getUsername() + "cdvfvfrv");
            return rider;
        }else {
            return null;
        }
    }

    public String updateRiderDetails(Rider rider) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(User_Collection).document(rider.getUsername()).set(rider);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteRider(String username) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(User_Collection).document(username).delete();
        return "Rider with username "+username+" has been deleted";
    }
}
