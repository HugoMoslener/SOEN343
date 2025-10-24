package com.TopFounders.application.service;
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
public class OperatorService {
    private static final String User_Collection="users";

    public OperatorService(){}

    public String saveOperator(Operator operator) throws ExecutionException, InterruptedException {

        try
        {Firestore db = FirestoreClient.getFirestore();
            System.out.println(db);
        ApiFuture<WriteResult> result = db.collection(User_Collection)
                .document(operator.getUsername()) // or UUID for ID
                .set(operator);
        WriteResult result1 = result.get();
            return "User saved at: " + result1.getUpdateTime();}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public Operator getOperatorDetails(String username) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference documentReference = db.collection(User_Collection).document(username);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Operator operator = null;

        if(document.exists()) {
            operator = document.toObject(Operator.class);
            return operator;
        }else {
            return null;
        }
    }

    public String updateOperatorDetails(Operator operator) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = db.collection(User_Collection).document(operator.getUsername()).set(operator);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteOperator(String username) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(User_Collection).document(username).delete();
        return "Operator with username  "+username+" has been deleted";
    }
}
