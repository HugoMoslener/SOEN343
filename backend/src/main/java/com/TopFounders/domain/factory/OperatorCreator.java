package com.TopFounders.domain.factory;

import com.TopFounders.domain.model.Operator;
import com.TopFounders.domain.model.Rider;

public class OperatorCreator extends UserCreator {
    public OperatorCreator() {}
    @Override
    public Operator createUser(String username, String paymentInformation, String email, String fullName, String address, String role){
            return new Operator(username,email,fullName,address,role);}
}
