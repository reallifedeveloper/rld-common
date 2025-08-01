package com.reallifedeveloper.common.application.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

public class StoredEventTest {

    @Test
    public void constructor() {
        String eventType = "foo";
        String eventBody = "bar";
        ZonedDateTime eventOccurredOn = ZonedDateTime.now();
        int eventVersion = 1;
        StoredEvent storedEvent = new StoredEvent(eventType, eventBody, eventOccurredOn, eventVersion);

        assertNull(storedEvent.id(), "Stored event should have null ID");
        assertEquals(eventType, storedEvent.eventType(), "Stored event has wrong type");
        assertEquals(eventBody, storedEvent.eventBody(), "Stored event has wrong body");
        assertEquals(eventOccurredOn, storedEvent.occurredOn(), "Stored event timestamp is wrong");
        assertEquals(eventVersion, storedEvent.version().intValue(), "Stored event version is wrong");
    }

    @Test
    public void constructorNullEventType() {
        assertThrows(IllegalArgumentException.class, () -> new StoredEvent(null, "bar", ZonedDateTime.now(), 1),
                "Expected constructor to throw IllegalArgumentException on null eventType");
    }

    @Test
    public void constructorNullEventBody() {
        assertThrows(IllegalArgumentException.class, () -> new StoredEvent("foo", null, ZonedDateTime.now(), 1),
                "Expected constructor to throw IllegalArgumentException on null eventBody");
    }

    @Test
    public void constructorNullOccurredOn() {
        assertThrows(IllegalArgumentException.class, () -> new StoredEvent("foo", "bar", null, 1),
                "Expected constructor to throw IllegalArgumentException on null occurredOn");
    }

    @Test
    public void testToString() {
        ZonedDateTime now = ZonedDateTime.now();
        StoredEvent storedEvent = new StoredEvent("foo", "bar", now, 42);
        assertEquals("StoredEvent{id=null, eventType=foo, eventBody=bar, occurredOn=" + now + ", version=42}", storedEvent.toString());
    }
}
