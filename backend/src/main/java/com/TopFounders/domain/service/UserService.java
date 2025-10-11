package com.TopFounders.domain.service;
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

public class UserService {
    private static final String User_Collection="users";

    public UserService(){}

    public User getUserDetails(String username) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(User_Collection).document(username);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        User user = null;

        if(document.exists()) {
            user = document.toObject(User.class);
            if(user.getRole().equalsIgnoreCase("rider")){
                user = document.toObject(Rider.class);
                return user;
            }
            return document.toObject(Operator.class);
        }else {
            return null;
        }
    }

    public String deleteUser(String username) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(User_Collection).document(username).delete();
        return "User with username "+username+" has been deleted";
    }
}
