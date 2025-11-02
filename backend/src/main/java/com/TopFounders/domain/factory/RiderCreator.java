package com.TopFounders.domain.factory;

import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;

public class RiderCreator extends UserCreator {
    public RiderCreator() {}
    @Override
    public Rider createUser(String username, String paymentInformation, String email, String fullName, String address, String role){
        return new Rider(username,paymentInformation,email,fullName,address,role);
    }
}
