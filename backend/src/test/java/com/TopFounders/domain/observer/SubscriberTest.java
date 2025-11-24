package com.TopFounders.domain.observer;

import com.TopFounders.domain.model.Bike;
import com.TopFounders.domain.model.BikeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberTest {

    @Test
    void testSubscriberCanBeImplementedAsLambda() {
        boolean[] updateCalled = {false};
        String[] receivedEventType = {null};
        Object[] receivedObject = {null};
        
        Subscriber lambdaSubscriber = (eventType, object) -> {
            updateCalled[0] = true;
            receivedEventType[0] = eventType;
            receivedObject[0] = object;
        };
        
        assertNotNull(lambdaSubscriber);
        lambdaSubscriber.update("TEST_EVENT", new Bike("BIKE001", BikeType.STANDARD));
        
        assertTrue(updateCalled[0]);
        assertEquals("TEST_EVENT", receivedEventType[0]);
        assertNotNull(receivedObject[0]);
    }

    @Test
    void testSubscriberCanBeImplementedAsAnonymousClass() {
        boolean[] updateCalled = {false};
        String[] receivedEventType = {null};
        
        Subscriber anonymousSubscriber = new Subscriber() {
            @Override
            public void update(String eventType, Object object) {
                updateCalled[0] = true;
                receivedEventType[0] = eventType;
            }
        };
        
        assertNotNull(anonymousSubscriber);
        anonymousSubscriber.update("ANONYMOUS_EVENT", null);
        
        assertTrue(updateCalled[0]);
        assertEquals("ANONYMOUS_EVENT", receivedEventType[0]);
    }

    @Test
    void testSubscriberCanBeImplementedAsConcreteClass() {
        TestSubscriberImpl subscriber = new TestSubscriberImpl();
        
        assertNotNull(subscriber);
        subscriber.update("CONCRETE_EVENT", new Bike("BIKE002", BikeType.E_BIKE));
        
        assertTrue(subscriber.wasUpdateCalled());
        assertEquals("CONCRETE_EVENT", subscriber.getReceivedEventType());
        assertNotNull(subscriber.getReceivedObject());
    }

    @Test
    void testSubscriberUpdateMethodSignature() {
        Subscriber subscriber = (eventType, object) -> {
            assertNotNull(eventType);
            // object can be null
        };
        
        subscriber.update("EVENT", null);
        subscriber.update("EVENT2", new Bike("BIKE003", BikeType.STANDARD));
        
        // If no exception is thrown, the signature is correct
        assertTrue(true);
    }

    // Concrete implementation for testing
    private static class TestSubscriberImpl implements Subscriber {
        private boolean updateCalled = false;
        private String receivedEventType = null;
        private Object receivedObject = null;

        @Override
        public void update(String eventType, Object object) {
            updateCalled = true;
            receivedEventType = eventType;
            receivedObject = object;
        }

        public boolean wasUpdateCalled() {
            return updateCalled;
        }

        public String getReceivedEventType() {
            return receivedEventType;
        }

        public Object getReceivedObject() {
            return receivedObject;
        }
    }
}


