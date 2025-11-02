package com.TopFounders.domain.factory;

public abstract class UserCreator {
    public abstract Object createUser(String username, String paymentInformation, String email, String fullName, String address, String role);
}
