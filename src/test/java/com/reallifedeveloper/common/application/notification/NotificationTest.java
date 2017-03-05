package com.reallifedeveloper.common.application.notification;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.domain.event.DomainEvent;

public class NotificationTest {

    @Test
    public void constructor() {
        Date occurredOn = new Date();
        NullableTestEvent event = new NullableTestEvent(occurredOn, 1);
        long storedEventId = 42L;
        Notification notification = new Notification(event, storedEventId);
        Assert.assertEquals("Wrong event type: ", NullableTestEvent.class.getName(), notification.eventType());
        Assert.assertEquals("Wrong stored event ID: ", storedEventId, notification.storedEventId());
        Assert.assertEquals("Wrong occurredOn timestamp: ", occurredOn, notification.occurredOn());
        Assert.assertEquals("Wrong event: ", event, notification.event());
    }
    @Test
    public void constructorEventWithNullOccurredOn() {
        NullableTestEvent event = new NullableTestEvent(null, 1);
        Notification notification = new Notification(event, 42L);
        Assert.assertNull("occurredOn should be null", notification.occurredOn());
    }

    @Test
    public void defensiveCopyOfOccurredOnInConstructor() {
        Date occurredOn = new Date();
        long occurredOnMillis = occurredOn.getTime();
        NullableTestEvent event = new NullableTestEvent(occurredOn, 1);
        Notification notification = new Notification(event, 42L);
        occurredOn.setTime(0);
        Assert.assertEquals("Wrong occuredOn timestamp: ", occurredOnMillis, notification.occurredOn().getTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullEvent() {
        new Notification(null, 42L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullStoredEventId() {
        new Notification(new NullableTestEvent(new Date(), 1), null);
    }

    @Test
    public void testToString() {
        Date occurredOn = new Date();
        int version = 1;
        NullableTestEvent event = new NullableTestEvent(occurredOn, version);
        long storedEventId = 42L;
        Notification notification = new Notification(event, storedEventId);
        Assert.assertEquals("Wrong result of toString: ", "Notification{eventType=" + NullableTestEvent.class.getName()
                + ", storedEventId=" + storedEventId + ", occurredOn=" + occurredOn
                + ", event=NullableTestEvent{occurredOn=" + occurredOn + ", version=" + version + "}}",
                notification.toString());
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
