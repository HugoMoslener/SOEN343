package com.TopFounders.domain.observer;

import com.TopFounders.domain.model.Bike;

public interface Subscriber {

    void update(String eventType, Bike bike);

}
