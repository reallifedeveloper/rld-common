package com.reallifedeveloper.common.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventSavingSubscriberTest {

    @Test
    public void handleEvent() {
        TestEvent event1 = new TestEvent(1, "foo");
        TestEvent event2 = new TestEvent(2, "bar");
        EventSavingSubscriber subscriber = new EventSavingSubscriber();
        subscriber.handleEvent(event1);
        assertEquals(1, subscriber.events().size(), "Wrong number of handled events: ");
        assertEquals(event1, subscriber.events().get(0), "Wrong handled event at position 0: ");
        subscriber.handleEvent(event2);
        assertEquals(2, subscriber.events().size(), "Wrong number of handled events: ");
        assertEquals(event1, subscriber.events().get(0), "Wrong handled event at position 0: ");
        assertEquals(event2, subscriber.events().get(1), "Wrong handled event at position 1: ");
    }

    @Test
    public void clear() {
        EventSavingSubscriber subscriber = new EventSavingSubscriber();
        subscriber.handleEvent(new TestEvent(1, "foo"));
        assertEquals(1, subscriber.events().size(), "Wrong number of handled events: ");
        subscriber.clear();
        assertTrue(subscriber.events().isEmpty(), "Handled events should be empty after clear");
    }

    @Test
    public void eventType() {
        EventSavingSubscriber subscriber = new EventSavingSubscriber();
        assertEquals(DomainEvent.class, subscriber.eventType(), "Wrong event type: ");
    }
}
