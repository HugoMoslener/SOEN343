package com.TopFounders.application.service;
import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.model.Operator;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class BikeService {
    private static final String Collection="bikes";

    public BikeService(){}

    public String saveBike(Bike bike) throws ExecutionException, InterruptedException {

        try
        {Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
        ApiFuture<WriteResult> result = db.collection(Collection)
                .document(bike.getBikeID()) // or UUID for ID
                .set(bike);
        WriteResult result1 = result.get();
            return "Bike saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public Bike getBikeDetails(String id) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(Collection).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();
        System.out.println("Bike details at: " + document.getId());

        Bike bike = null;
        if(document.exists()) {
            bike = document.toObject(Bike.class);
            bike.markAsLoadingFromFirestore(false);
            return bike;
        }else {
            return null;
        }
    }

    public String updateBikeDetails(Bike bike) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        bike.markAsLoadingFromFirestore(true);
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(Collection).document(bike.getBikeID()).set(bike);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteBike(String id) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(Collection).document(id).delete();
        return "Bike with id   "+id+" has been deleted";
    }
}
