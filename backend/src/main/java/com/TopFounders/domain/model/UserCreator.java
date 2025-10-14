package com.TopFounders.domain.model;

public abstract class UserCreator {
    public abstract Object CreateUser(String username, String paymentInformation, String email, String fullName, String address, String role);
}
