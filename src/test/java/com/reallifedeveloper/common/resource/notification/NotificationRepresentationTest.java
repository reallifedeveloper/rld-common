package com.reallifedeveloper.common.resource.notification;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationRepresentationTest {

    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @Test
    public void constructor() {
        TestEvent event = new TestEvent(1, "foo");
        long storedEventId = 42;
        Notification notification = new Notification(event, storedEventId);
        NotificationRepresentation representation = new NotificationRepresentation(notification, objectSerializer);
        Assert.assertEquals("Wrong event type: ", TestEvent.class.getName(), representation.getEventType());
        Assert.assertEquals("Wrong stored event ID: ", storedEventId, representation.getStoredEventId());
        Assert.assertEquals("Wrong timestamp: ", event.occurredOn(), representation.getOccurredOn());
        Assert.assertEquals("Wrong event: ", objectSerializer.serialize(event), representation.getEvent());
    }

    @Test
    public void constructorEventWithNullOccurredOn() {
        NullableTestEvent event = new NullableTestEvent(null, 1);
        Notification notification = new Notification(event, 42L);
        NotificationRepresentation representation = new NotificationRepresentation(notification, objectSerializer);
        Assert.assertNull("occurredOn should be null", representation.getOccurredOn());
    }

    @Test
    public void defensiveCopyOfOccurredOnInConstructor() {
        Date occurredOn = new Date();
        long occurredOnMillis = occurredOn.getTime();
        NullableTestEvent event = new NullableTestEvent(occurredOn, 1);
        Notification notification = new Notification(event, 42L);
        NotificationRepresentation representation = new NotificationRepresentation(notification, objectSerializer);
        occurredOn.setTime(0);
        Assert.assertEquals("Wrong occuredOn timestamp: ", occurredOnMillis, representation.getOccurredOn().getTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullNotification() {
        new NotificationRepresentation(null, objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullSerializer() {
        TestEvent event = new TestEvent(1, "foo");
        long storedEventId = 42;
        Notification notification = new Notification(event, storedEventId);
        new NotificationRepresentation(notification, null);
    }


    /**
     * An implementation of {@link DomainEvent} that allows <code>null</code> for the <code>occurredOn</code>
     * timestamp.
     */
    private static class NullableTestEvent implements DomainEvent {

        private static final long serialVersionUID = 1L;

        private Date occurredOn;
        private int version;

        NullableTestEvent(Date occurredOn, int version) {
            this.occurredOn = occurredOn;
            this.version = version;
        }

        @Override
        public Date occurredOn() {
            return occurredOn;
        }

        @Override
        public int version() {
            return version;
        }

        @Override
        public String toString() {
            return "NullableTestEvent{occurredOn=" + occurredOn + ", version=" + version + "}";
        }
    }
}
