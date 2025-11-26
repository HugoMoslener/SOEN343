package com.TopFounders.application.service;
import com.TopFounders.domain.factory.RiderCreator;
import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;
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

    // Getting the operator's rider account
    public Rider getRiderFromOperator(String operatorUsername) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        // 1. Fetch operator
        DocumentReference operatorRef = db.collection(User_Collection).document(operatorUsername);
        DocumentSnapshot operatorSnap = operatorRef.get().get();

        if (!operatorSnap.exists()) {
            return null;
        }

        Operator operator = operatorSnap.toObject(Operator.class);

        // 2. Check if linkedRider exists
        if (operator.getLinkedRider() == null || operator.getLinkedRider().isEmpty()) {
            return null;
        }

        String riderUsername = operator.getLinkedRider();

        // 3. Fetch Rider Object
        DocumentReference riderRef = db.collection(User_Collection).document(riderUsername);
        DocumentSnapshot riderSnap = riderRef.get().get();

        if (!riderSnap.exists()) {
            return null;
        }

        return riderSnap.toObject(Rider.class);
    }

    // Creating a rider for the operator who doesn't have one yet
    public Rider createRiderForOperator(String operatorUsername, String paymentInfo)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        // 1. Fetch operator
        DocumentReference operatorRef = db.collection(User_Collection).document(operatorUsername);
        DocumentSnapshot operatorSnap = operatorRef.get().get();

        if (!operatorSnap.exists()) {
            throw new RuntimeException("Operator does not exist: " + operatorUsername);
        }

        Operator operator = operatorSnap.toObject(Operator.class);

        // 2. Compute rider username
        String riderUsername = operatorUsername + "-rider"; // <operator_name>-rider

        // 3. Build rider object
        RiderCreator creator = new RiderCreator();
        Rider rider = creator.createUser(
                riderUsername,
                paymentInfo,
                operator.getEmail(),
                operator.getFullName(),
                operator.getAddress(),
                "rider"
        );

        rider.setFlexMoney(0.0);

        // 4. Save rider
        ApiFuture<WriteResult> riderSave = db.collection(User_Collection)
                .document(riderUsername)
                .set(rider);
        riderSave.get();

        // 5. Link operator → rider
        operator.setLinkedRider(riderUsername);

        ApiFuture<WriteResult> operatorSave = db.collection(User_Collection)
                .document(operator.getUsername())
                .set(operator);
        operatorSave.get();

        // 6. Return Rider object so controller can perform login switch
        return rider;
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
