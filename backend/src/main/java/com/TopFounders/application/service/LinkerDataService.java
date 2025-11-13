package com.TopFounders.application.service;

import com.TopFounders.domain.model.Dock;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class LinkerDataService {
    private static final String Collection="flexdollar";

    public LinkerDataService(){}

    public String saveFlexDollars(String id, double flexdollars) throws ExecutionException, InterruptedException {

        try
        {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> data = new HashMap<>();
            data.put("flexdollars", flexdollars);


            System.out.println(db);
            ApiFuture<WriteResult> result = db.collection(Collection)
                    .document(id)
                    .set(data);
            WriteResult result1 = result.get();
            return "Dock saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public  Map<String, Object>  getFlexDollar(String id) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection(Collection)
                .document(id)
                .get()
                .get();

        if (!doc.exists()) {
            System.out.println("No document found for ID: " + id);
            return null;
        }

        return doc.getData();
    }

    public String updateFlexDollar(String id, double flexdollars) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
         Map<String, Object> updates = new HashMap<>();
        updates.put("flexdollars", flexdollars);
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(Collection).document(id).set(updates);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

}
