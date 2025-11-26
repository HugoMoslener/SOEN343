package com.TopFounders.application.service;

import com.TopFounders.domain.model.Reservation;
import com.TopFounders.domain.model.Station;
import com.TopFounders.domain.model.Trip;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TripService {
    private static final String Collection="trips";
    public TripService(){}

    public String saveTrip(Trip trip) throws ExecutionException, InterruptedException {

        try
        {
            Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
            ApiFuture<WriteResult> result = db.collection(Collection)
                    .document(trip.getTripID()) // or UUID for ID
                    .set(trip);
            WriteResult result1 = result.get();
            return "Trip saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public Trip getTripDetails(String tripID) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(Collection).document(tripID);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Trip trip = null;

        if(document.exists()) {
            trip = document.toObject(Trip.class);
            return trip;
        }else {
            return null;
        }
    }

    public ArrayList<Trip> getAllTrip() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(Collection).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        ArrayList<Trip> tripList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            tripList.add(doc.toObject(Trip.class));
        }

        return tripList;
    }

    public String updateTripDetails(Trip trip) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(Collection).document(trip.getTripID()).set(trip);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteTrip(String tripID) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(Collection).document(tripID).delete();
        return "Trip with ID  "+tripID+" has been deleted";
    }

    public ArrayList<Trip> getTripsByRider(String riderUsername)
            throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(Collection).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        ArrayList<Trip> tripList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Trip trip = doc.toObject(Trip.class);
            // Check nested rider username
            if (trip.getReservation() != null &&
                    trip.getReservation().getRider() != null &&
                    riderUsername.equals(trip.getReservation().getRider().getUsername())) {
                tripList.add(trip);
            }
        }
        return tripList;
    }
}
