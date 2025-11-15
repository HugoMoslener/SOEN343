package com.TopFounders.application.service;
import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.model.Dock;
import com.TopFounders.domain.model.Operator;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DockService {
    private static final String Collection="docks";

    public DockService(){}

    public String saveDock(Dock dock) throws ExecutionException, InterruptedException {

        try
        {Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
        ApiFuture<WriteResult> result = db.collection(Collection)
                .document(dock.getDockID()) // or UUID for ID
                .set(dock);
        WriteResult result1 = result.get();
            return "Dock saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public Dock getDockDetails(String id) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(Collection).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Dock dock = null;

        if(document.exists()) {
            dock = document.toObject(Dock.class);
            dock.markAsLoadingFromFirestore(false);
            return dock;
        }else {
            return null;
        }
    }


    public ArrayList<Dock> getAllDocks() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(Collection).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        ArrayList<Dock> dockList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Dock dock = doc.toObject(Dock.class);
            dock.markAsLoadingFromFirestore(false);
            dockList.add(dock);
        }

        return dockList;
    }

    public String updateDockDetails(Dock dock) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        dock.markAsLoadingFromFirestore(true);
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(Collection).document(dock.getDockID()).set(dock);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteDock(String id) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(Collection).document(id).delete();
        return "Dock with id  "+id+" has been deleted";
    }
}
