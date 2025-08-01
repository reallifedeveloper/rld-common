package com.reallifedeveloper.common.resource.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationRepresentationTest {

    private final ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @Test
    public void constructor() {
        TestEvent event = new TestEvent(1, "foo");
        long storedEventId = 42;
        Notification notification = Notification.create(event, storedEventId);
        NotificationRepresentation representation = new NotificationRepresentation(notification, objectSerializer);

        assertEquals(TestEvent.class.getName(), representation.getEventType(), "Wrong event type:");
        assertEquals(storedEventId, representation.getStoredEventId(), "Wrong stored event ID:");
        assertEquals(event.eventOccurredOn(), representation.getOccurredOn(), "Wrong timestamp:");
        assertEquals(objectSerializer.serialize(event), representation.getEvent(), "Wrong event:");
    }

    @Test
    public void constructorEventWithNullOccurredOn() {
        NullableTestEvent event = new NullableTestEvent(null, 1);
        Notification notification = Notification.create(event, 42L);
        NotificationRepresentation representation = new NotificationRepresentation(notification, objectSerializer);

        assertNull(representation.getOccurredOn(), "occurredOn should be null");
    }

    @Test
    public void constructorNullNotification() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new NotificationRepresentation(null, objectSerializer));
        assertEquals("Arguments must not be null: notification=null, objectSerializer=" + objectSerializer,
                exception.getMessage());
    }

    @Test
    public void constructorNullSerializer() {
        TestEvent event = new TestEvent(1, "foo");
        long storedEventId = 42;
        Notification notification = Notification.create(event, storedEventId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new NotificationRepresentation(notification, null));
        assertEquals("Arguments must not be null: notification=" + notification + ", objectSerializer=null",
                exception.getMessage());
    }

    @Test
    public void packagePrivateConstructorLeavesAllFieldsWithDefaultValues() {
        NotificationRepresentation representation = new NotificationRepresentation();
        assertNull(representation.getEventType());
        assertEquals(0, representation.getStoredEventId());
        assertNull(representation.getOccurredOn());
        assertNull(representation.getEvent());
    }

    /**
     * An implementation of {@link DomainEvent} that allows {@code null} for the {@code occurredOn}
     * timestamp.
     */
    private static class NullableTestEvent implements DomainEvent {

        private static final long serialVersionUID = 1L;

        private final ZonedDateTime occurredOn;
        private final int version;

        NullableTestEvent(ZonedDateTime occurredOn, int version) {
            this.occurredOn = occurredOn;
            this.version = version;
        }

        @Override
        public ZonedDateTime eventOccurredOn() {
            return occurredOn;
        }

        @Override
        public int eventVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "NullableTestEvent{occurredOn=" + occurredOn + ", version=" + version + "}";
        }
    }
}
