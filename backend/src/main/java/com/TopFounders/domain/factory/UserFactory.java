package com.TopFounders.domain.factory;

import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;

public class UserFactory {

    public UserFactory(){}

    public Object CreateUser(String username, String paymentInformation, String email, String fullName, String address, String role){
        if(role.equals("rider")){
            return new Rider(username,paymentInformation,email,fullName,address,role);
        }
        else {
            return new Operator(username,email,fullName,address,role);
        }
    }
}
