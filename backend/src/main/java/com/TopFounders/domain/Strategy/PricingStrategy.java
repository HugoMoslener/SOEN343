package com.TopFounders.domain.Strategy;

import com.TopFounders.domain.model.Trip;

public interface PricingStrategy {
    double calculateTotal(Trip trip);
}
