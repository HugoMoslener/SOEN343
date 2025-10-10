package com.TopFounders.domain.service;
import com.TopFounders.domain.model.Rider;
import com.TopFounders.domain.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import lombok.Locked;
import org.checkerframework.checker.guieffect.qual.SafeType;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.*;

@Service
public class RiderService {
    private static final String User_Collection="users";

    public RiderService(){}

    public String saveRider(Rider rider) throws ExecutionException, InterruptedException {

        try
        {Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
        ApiFuture<WriteResult> result = db.collection(User_Collection)
                .document(rider.getUsername()) // or UUID for ID
                .set(rider);
        WriteResult result1 = result.get();
        System.out.println("Message reaches inside riderservice");
            return "User saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
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
