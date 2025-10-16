package com.TopFounders.application.service;
import com.TopFounders.domain.model.Station;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class StationService {
    private static final String Collection="stations";

    public StationService(){}

    public String saveStation(Station station) throws ExecutionException, InterruptedException {

        try
        {Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
        ApiFuture<WriteResult> result = db.collection(Collection)
                .document(station.getStationID()) // or UUID for ID
                .set(station);
        WriteResult result1 = result.get();
            return "Station saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public Station getStationDetails(String id) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(Collection).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Station station = null;

        if(document.exists()) {
            station = document.toObject(Station.class);
            return station;
        }else {
            return null;
        }
    }

    public ArrayList<Station> getAllStations() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection("stations").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        ArrayList<Station> stationList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Station station = doc.toObject(Station.class);
            stationList.add(station);
        }

        return stationList;
    }

    public String updateStationDetails(Station station) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(Collection).document(station.getStationID()).set(station);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteStation(String id) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(Collection).document(id).delete();
        return "Station with id  "+id+" has been deleted";
    }
}
