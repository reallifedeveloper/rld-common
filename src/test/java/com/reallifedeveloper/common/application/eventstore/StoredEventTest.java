package com.reallifedeveloper.common.application.eventstore;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StoredEventTest {

    @Test
    public void constructor() {
        String eventType = "foo";
        String eventBody = "bar";
        ZonedDateTime eventOccurredOn = ZonedDateTime.now();
        int eventVersion = 1;
        StoredEvent storedEvent = new StoredEvent(eventType, eventBody, eventOccurredOn, eventVersion);

        Assertions.assertNull(storedEvent.id(), "Stored event should have null ID");
        Assertions.assertEquals(eventType, storedEvent.eventType(), "Stored event has wrong type");
        Assertions.assertEquals(eventBody, storedEvent.eventBody(), "Stored event has wrong body");
        Assertions.assertEquals(eventOccurredOn, storedEvent.occurredOn(), "Stored event timestamp is wrong");
        Assertions.assertEquals(eventVersion, storedEvent.version().intValue(), "Stored event version is wrong");
    }

    @Test
    public void constructorNullEventType() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            new StoredEvent(null, "bar", ZonedDateTime.now(), 1),
            "Expected constructor to throw IllegalArgumentException on null eventType"
        );
    }

    @Test
    public void constructorNullEventBody() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            new StoredEvent("foo", null, ZonedDateTime.now(), 1),
            "Expected constructor to throw IllegalArgumentException on null eventBody"
        );
    }

    @Test
    public void constructorNullOccurredOn() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            new StoredEvent("foo", "bar", null, 1),
            "Expected constructor to throw IllegalArgumentException on null occurredOn"
        );
    }

}
