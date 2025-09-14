package com.reallifedeveloper.common.application.notification;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.tools.test.TestUtil;

public class NotificationTest {

    @Test
    public void create() {
        ZonedDateTime occurredOn = TestUtil.utcNow();
        NullableTestEvent event = new NullableTestEvent(occurredOn, 1);
        long storedEventId = 42L;
        Notification notification = Notification.create(event, storedEventId);
        Assertions.assertEquals(NullableTestEvent.class.getName(), notification.eventType(), "Wrong event type");
        Assertions.assertEquals(storedEventId, notification.storedEventId().longValue(), "Wrong stored event ID");
        Assertions.assertEquals(occurredOn, notification.occurredOn(), "Wrong occurredOn timestamp");
        Assertions.assertEquals(event, notification.event(), "Wrong event");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void createEventWithNullOccurredOn() {
        NullableTestEvent event = new NullableTestEvent(null, 1);
        Notification notification = Notification.create(event, 42L);
        Assertions.assertNull(notification.occurredOn(), "occurredOn should be null");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void createNullEvent() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Notification.create(null, 42L));
    }

    @Test
    @SuppressWarnings("NullAway")
    public void createNullStoredEventId() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            Notification.create(new NullableTestEvent(TestUtil.utcNow(), 1), null)
        );
    }

    @Test
    public void testToString() {
        ZonedDateTime occurredOn = TestUtil.utcNow();
        int version = 1;
        NullableTestEvent event = new NullableTestEvent(occurredOn, version);
        long storedEventId = 42L;
        Notification notification = Notification.create(event, storedEventId);
        Assertions.assertEquals(
            "Notification[eventType=" + NullableTestEvent.class.getName() + ", storedEventId=" + storedEventId
            + ", occurredOn=" + occurredOn + ", event=NullableTestEvent{occurredOn=" + occurredOn + ", version=" + version + "}]",
            notification.toString(),
            "Wrong result of toString"
        );
    }

    /**
     * An implementation of {@link DomainEvent} that allows {@code null} for the {@code occurredOn}
     * timestamp.
     */
    private static class NullableTestEvent implements DomainEvent {

        private static final long serialVersionUID = 1L;

        private ZonedDateTime occurredOn;
        private int version;

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
