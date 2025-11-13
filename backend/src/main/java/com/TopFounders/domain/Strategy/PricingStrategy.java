package com.TopFounders.domain.Strategy;

import com.TopFounders.domain.model.Trip;

import java.util.concurrent.ExecutionException;

public interface PricingStrategy {
    double calculateTotal(Trip trip) throws ExecutionException, InterruptedException;
}
