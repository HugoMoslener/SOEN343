package com.TopFounders.application.service;

import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Reservation;
import com.TopFounders.domain.model.Station;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReservationService {
    private static final String Collection="reservations";
    public ReservationService(){}
    
    private boolean isFirebaseAvailable() {
        return !FirebaseApp.getApps().isEmpty();
    }

    public String saveReservation(Reservation reservation) throws ExecutionException, InterruptedException {
        if (!isFirebaseAvailable()) {
            System.out.println("Firebase not available. Cannot save reservation.");
            return "Firebase not available - reservation not saved";
        }

        try
        {
            Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
            ApiFuture<WriteResult> result = db.collection(Collection)
                    .document(reservation.getReservationID()) // or UUID for ID
                    .set(reservation);
            WriteResult result1 = result.get();
            return "Reservation saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public Reservation getReservationDetails(String reservationID) throws InterruptedException, ExecutionException {
        if (!isFirebaseAvailable()) {
            System.out.println("Firebase not available. Cannot retrieve reservation details.");
            return null;
        }
        
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(Collection).document(reservationID);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Reservation reservation = null;

        if(document.exists()) {
            reservation = document.toObject(Reservation.class);
            return reservation;
        }else {
            return null;
        }
    }

    public ArrayList<Reservation> getAllReservations() throws InterruptedException, ExecutionException {
        if (!isFirebaseAvailable()) {
            System.out.println("Firebase not available. Returning empty reservation list.");
            return new ArrayList<>();
        }
        
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(Collection).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        ArrayList<Reservation> reservationList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            reservationList.add(doc.toObject(Reservation.class));
        }

        return reservationList;
    }

    public String updateReservationDetails(Reservation reservation) throws InterruptedException, ExecutionException {
        if (!isFirebaseAvailable()) {
            System.out.println("Firebase not available. Cannot update reservation details.");
            return "Firebase not available - reservation not updated";
        }
        
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(Collection).document(reservation.getReservationID()).set(reservation);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteReservation(String reservationID) {
        if (!isFirebaseAvailable()) {
            System.out.println("Firebase not available. Cannot delete reservation.");
            return "Firebase not available - reservation not deleted";
        }
        
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(Collection).document(reservationID).delete();
        return "reservation with ID  "+reservationID+" has been deleted";
    }
}
