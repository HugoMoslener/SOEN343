package com.TopFounders.application.service;
import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    private static final String USER_COLLECTION = "users";

    public UserService(){}

    public User getUserDetails(String username) throws InterruptedException, ExecutionException {
        try {
            System.out.println("🔍 Looking up user: " + username);
            System.out.println(username);
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection(USER_COLLECTION).document(username);
            DocumentSnapshot document = docRef.get().get();

            if (!document.exists()) {
                System.out.println("❌ User not found: " + username);
                return null;
            }

            // Get role first to determine type
            String role = document.getString("role");
            System.out.println("🎭 User role: " + role);

            if (role == null) {
                System.out.println("⚠️ No role found, using generic User class");
                return document.toObject(User.class);
            }

            switch (role.toLowerCase()) {
                case "rider":
                    Rider rider = document.toObject(Rider.class);
                    System.out.println("✅ Converted to Rider: " + (rider != null ? rider.getUsername() : "null"));
                    return rider;
                case "operator":
                    Operator operator = document.toObject(Operator.class);
                    System.out.println("✅ Converted to Operator: " + (operator != null ? operator.getUsername() : "null"));
                    return operator;
                default:
                    System.out.println("⚠️ Unknown role, using generic User");
                    return document.toObject(User.class);
            }
        } catch (Exception e) {
            System.out.println("💥 ERROR in getUserDetails: " + e.getMessage());
            e.printStackTrace();
            throw new ExecutionException(e);
        }
    }

    public String deleteUser(String username) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(USER_COLLECTION).document(username).delete();
        return "User with username "+username+" has been deleted";
    }
}
