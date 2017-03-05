package com.reallifedeveloper.common.application.eventstore;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class StoredEventTest {

    @Test
    public void constructor() {
        String eventType = "foo";
        String eventBody = "bar";
        Date eventOccurredOn = new Date();
        int eventVersion = 1;
        StoredEvent storedEvent = new StoredEvent(eventType, eventBody, eventOccurredOn, eventVersion);
        Assert.assertNull("Stored event should have null ID", storedEvent.id());
        Assert.assertEquals("Stored event has wrong type: ", eventType, storedEvent.eventType());
        Assert.assertEquals("Stored event has wrong body: ", eventBody, storedEvent.eventBody());
        Assert.assertEquals("Stored event timestamp is wrong: ", eventOccurredOn, storedEvent.occurredOn());
        Assert.assertEquals("Stored event version is wrong: ", eventVersion, storedEvent.version().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullEventType() {
        new StoredEvent(null, "bar", new Date(), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullEventBody() {
        new StoredEvent("foo", null, new Date(), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullOccurredOn() {
        new StoredEvent("foo", "bar", null, 1);
    }

    @Test
    public void defensiveCopyOfOccurredOnInConstructor() {
        Date occurredOn = new Date();
        long occurredOnMillis = occurredOn.getTime();
        StoredEvent storedEvent = new StoredEvent("foo", "bar", occurredOn, 1);
        occurredOn.setTime(0);
        Assert.assertEquals("Stored event timestamp is wrong: ", occurredOnMillis, storedEvent.occurredOn().getTime());
    }

    @Test
    public void defensiveCopyOfOccurredOnInGetter() {
        Date occurredOn = new Date();
        StoredEvent storedEvent = new StoredEvent("foo", "bar", occurredOn, 1);
        storedEvent.occurredOn().setTime(0);
        Assert.assertEquals("Stored event timestamp is wrong: ", occurredOn, storedEvent.occurredOn());
    }
}
