package com.reallifedeveloper.common.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationFactory;
import com.reallifedeveloper.common.application.notification.NotificationReader;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.AbstractDomainEvent;
import com.reallifedeveloper.tools.test.TestUtil;

public class GsonNotificationReaderTest {

    private static final String NOTIFICATION_JSON = """
            {
                "eventType":%s,
                "storedEventId":%s,
                "occurredOn":%s,
                "event":%s
            }
            """.replaceAll("\\s", "");

    private static final String FOO_EVENT_JSON = """
            {
                "event": {
                    "foo":%s
                }
            }
            """.replaceAll("\\s", "");

    private static final double DELTA = 0.0000001;

    /**
     * This test shows how an {@link EventStore} can be used to help work with notifications.
     */
    @Test
    public void realNotification() {
        InMemoryStoredEventRepository storedEventRepository = new InMemoryStoredEventRepository();
        ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
        EventStore eventStore = new EventStore(objectSerializer, storedEventRepository);
        NotificationFactory notificationFactory = NotificationFactory.instance(eventStore);
        TestEvent testEvent = new TestEvent(42, "Foo!", 1.05);
        eventStore.add(testEvent);
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        assertEquals(1, storedEvents.size(), "Wrong number of stored events");
        List<Notification> notifications = notificationFactory.fromStoredEvents(storedEvents);
        assertEquals(1, notifications.size(), "Wrong number of notifications");
        Notification notification = notifications.get(0);
        String serializedNotification = objectSerializer.serialize(notification);
        NotificationReader reader = new GsonNotificationReader(serializedNotification);
        assertEquals(testEvent.getClass().getName(), reader.eventType(), "Wrong event type");
        assertEquals(1, reader.storedEventId(), "Wrong stored event ID");
        assertEquals(testEvent.eventOccurredOn().toInstant().truncatedTo(ChronoUnit.MILLIS), reader.occurredOn().toInstant(),
                "Wrong occurred on timestamp");
        assertEquals(1, reader.eventVersion(), "Wrong event version");
        assertEquals(testEvent.id, reader.eventLongValue("id").get().longValue(), "Wrong event ID");
        assertEquals(testEvent.name, reader.eventStringValue("name").get(), "Wrong event name");
        assertEquals(testEvent.d, reader.eventDoubleValue("d").get(), DELTA, "Wrong event double value");
        assertEquals(testEvent.color.getRGB(), reader.eventIntValue("color.value").get().intValue(), "Wrong event color value");
    }

    @Test
    public void eventTypeForNotificationWithNullEventType() {
        String json = NOTIFICATION_JSON.formatted(null, 42, "\"2025-02-12T21:24:45.672+01\"", "{\"foo\":42}");
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.eventType());
        assertEquals("Field eventType is missing or null: object=" + json, e.getMessage());
    }

    @Test
    public void eventTypeForEmptyNotification() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.eventType());
        assertEquals("Field eventType is missing or null: object=" + json, e.getMessage());
    }

    @Test
    public void storedEventIdForNotificationWithNullStoredEventId() {
        String json = NOTIFICATION_JSON.formatted("\"Event Type\"", null, "\"2025-02-12T21:24:45.672+01\"", "{\"foo\":42}");
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.storedEventId());
        assertEquals("Field storedEventId is missing or null: object=" + json, e.getMessage());
    }

    @Test
    public void storedEventIdForEmptyNotification() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.storedEventId());
        assertEquals("Field storedEventId is missing or null: object=" + json, e.getMessage());
    }

    @Test
    public void occurredOnForNotificationWithNullOccurredOn() {
        String json = NOTIFICATION_JSON.formatted("\"Event Type\"", 42, null, "{\"foo\":42}");
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.occurredOn());
        assertEquals("Field occurredOn is missing or null: object=" + json, e.getMessage());
    }

    @Test
    public void occurredOnForEmptyNotification() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.occurredOn());
        assertEquals("Field occurredOn is missing or null: object=" + json, e.getMessage());
    }

    @Test
    public void eventVersionForNotificationWithNullEventVersion() {
        String eventJson = "{\"foo\":42,\"eventVersion\":null}";
        String json = NOTIFICATION_JSON.formatted("\"Event Type\"", 42, null, eventJson);
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.eventVersion());
        assertEquals("Field eventVersion is missing or null: object=" + eventJson, e.getMessage());
    }

    @Test
    public void eventVersionForNotificationWitoutEventVersion() {
        String eventJson = "{\"foo\":42}";
        String json = NOTIFICATION_JSON.formatted("\"Event Type\"", 42, null, eventJson);
        NotificationReader reader = new GsonNotificationReader(json);
        Exception e = assertThrows(IllegalArgumentException.class, () -> reader.eventVersion());
        assertEquals("Field eventVersion is missing or null: object=" + eventJson, e.getMessage());
    }

    @Test
    public void eventVersionForNotificationWitNullEvent() {
        String json = NOTIFICATION_JSON.formatted("\"Event Type\"", 42, "\"2025-02-12T21:24:45.672+01\"", null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> new GsonNotificationReader(json));
        assertEquals("event not found in JSON string: " + json, e.getMessage());
    }

    @Test
    public void eventIntValue() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(42, reader.eventIntValue("foo").get().intValue(), "Foo has wrong value");
    }

    @Test
    public void eventIntValueMin() {
        String json = FOO_EVENT_JSON.formatted(Integer.MIN_VALUE);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(Integer.MIN_VALUE, reader.eventIntValue("foo").get().intValue(), "Foo has wrong value");
    }

    @Test
    public void eventIntValueMax() {
        String json = FOO_EVENT_JSON.formatted(Integer.MAX_VALUE);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(Integer.MAX_VALUE, reader.eventIntValue("foo").get().intValue(), "Foo has wrong value");
    }

    @Test
    public void eventIntValueEmpty() {
        String json = FOO_EVENT_JSON.formatted((Integer) null);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventIntValue("foo").isEmpty(), "Foo should be empty");
    }

    @Test
    public void eventIntValueNonExisting() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventIntValue("baz").isEmpty(), "Baz should be empty");
    }

    @Test
    public void eventIntValueNotInteger() {
        String json = FOO_EVENT_JSON.formatted("\"bar\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(NumberFormatException.class, () -> reader.eventIntValue("foo"));
    }

    @Test
    public void eventLongValue() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(42, reader.eventLongValue("foo").get().longValue(), "Foo has wrong value");
    }

    @Test
    public void eventLongValueMin() {
        String json = FOO_EVENT_JSON.formatted(Long.MIN_VALUE);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(Long.MIN_VALUE, reader.eventLongValue("foo").get().longValue(), "Foo has wrong value");
    }

    @Test
    public void eventLongValueMax() {
        String json = FOO_EVENT_JSON.formatted(Long.MAX_VALUE);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(Long.MAX_VALUE, reader.eventLongValue("foo").get().longValue(), "Foo has wrong value");
    }

    @Test
    public void eventLongValueEmpty() {
        String json = FOO_EVENT_JSON.formatted((Integer) null);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventLongValue("foo").isEmpty(), "Foo should be empty");
    }

    @Test
    public void eventLongValueNonExisting() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventLongValue("baz").isEmpty(), "Baz should be empty");
    }

    @Test
    public void eventLongValueNotInteger() {
        String json = FOO_EVENT_JSON.formatted("\"bar\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(NumberFormatException.class, () -> reader.eventLongValue("foo"));
    }

    @Test
    public void eventDoubleValue() {
        String json = FOO_EVENT_JSON.formatted(47.11);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(47.11, reader.eventDoubleValue("foo").get(), DELTA, "Foo has wrong value");
    }

    @Test
    public void eventDoubleValueMin() {
        String json = FOO_EVENT_JSON.formatted(Double.MIN_VALUE);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(Double.MIN_VALUE, reader.eventDoubleValue("foo").get(), DELTA, "Foo has wrong value");
    }

    @Test
    public void eventDoubleValueMax() {
        String json = FOO_EVENT_JSON.formatted(Double.MAX_VALUE);
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(Double.MAX_VALUE, reader.eventDoubleValue("foo").get(), DELTA, "Foo has wrong value");
    }

    @Test
    public void eventDoubleValueNull() {
        String json = FOO_EVENT_JSON.formatted((Integer) null);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventDoubleValue("foo").isEmpty(), "Foo should be empty");
    }

    @Test
    public void eventDoubleValueNotNumber() {
        String json = FOO_EVENT_JSON.formatted("\"bar\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(NumberFormatException.class, () -> reader.eventDoubleValue("foo"));
    }

    @Test
    public void eventStringValue() {
        String json = FOO_EVENT_JSON.formatted("\"bar\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals("bar", reader.eventStringValue("foo").get(), "Foo has wrong value");
    }

    @Test
    public void eventStringValueNull() {
        String json = FOO_EVENT_JSON.formatted((Integer) null);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventStringValue("foo").isEmpty(), "Foo should be empty");
    }

    @Test
    public void eventStringValueNonExisting() {
        String json = FOO_EVENT_JSON.formatted("\"bar\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.eventStringValue("baz").isEmpty(), "Baz should be empty");
    }

    @Test
    public void eventDateValue() {
        String strDate = "2014-07-21T14:41:00.123Z";
        ZonedDateTime date = ZonedDateTime.parse(strDate);
        String json = FOO_EVENT_JSON.formatted("\"" + strDate + "\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertEquals(date, reader.zonedDateTimeValue("foo").get(), "Foo has wrong value");
    }

    @Test
    public void eventDateValueNull() {
        String json = FOO_EVENT_JSON.formatted((Integer) null);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.zonedDateTimeValue("foo").isEmpty(), "Foo should be empty");
    }

    @Test
    public void eventDateValueNonExisting() {
        String json = FOO_EVENT_JSON.formatted((Integer) null);
        NotificationReader reader = new GsonNotificationReader(json);
        assertTrue(reader.zonedDateTimeValue("baz").isEmpty(), "Baz should be empty");
    }

    @Test
    public void eventDateValueNonWrongFormat() {
        String json = FOO_EVENT_JSON.formatted("\"bar\"");
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(DateTimeParseException.class, () -> reader.zonedDateTimeValue("foo"));
    }

    @Test
    public void eventTypeMissingType() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(IllegalArgumentException.class, reader::eventType);
    }

    @Test
    public void notificationIdMissingId() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(IllegalArgumentException.class, reader::storedEventId);
    }

    @Test
    public void occurredOnMissingDate() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(IllegalArgumentException.class, reader::occurredOn);
    }

    @Test
    public void eventVersionMissingVersion() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(IllegalArgumentException.class, reader::eventVersion);
    }

    @Test
    public void nestedFieldNameNonExistingField() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(IllegalArgumentException.class, () -> reader.eventStringValue("bar.baz"));
    }

    @Test
    public void nestedFieldNameNotAnObject() {
        String json = FOO_EVENT_JSON.formatted(42);
        NotificationReader reader = new GsonNotificationReader(json);
        assertThrows(IllegalArgumentException.class, () -> reader.eventStringValue("foo.bar"));
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullJsonObject() {
        assertThrows(IllegalArgumentException.class, () -> new GsonNotificationReader(null));
    }

    @Test
    public void constructorNotAJsonObject() {
        assertThrows(IllegalArgumentException.class, () -> new GsonNotificationReader("foo"));
    }

    @Test
    public void constructorMalformedJsonObject() {
        assertThrows(IllegalArgumentException.class, () -> new GsonNotificationReader("\\//+"));
    }

    @Test
    public void validJsonButNotValidEventMessage() {
        String json = "{\"crap\":{\"foo\":42}}";
        Exception e = assertThrows(IllegalArgumentException.class, () -> new GsonNotificationReader(json));
        assertEquals("event not found in JSON string: " + json, e.getMessage());
    }

    private static class TestEvent extends AbstractDomainEvent {
        private static final long serialVersionUID = 1L;
        private long id;
        private String name;
        private double d;
        private MyColor color = MyColor.CYAN;

        TestEvent(long id, String name, double d) {
            super(TestUtil.utcNow());
            this.id = id;
            this.name = name;
            this.d = d;
        }

        static class MyColor {
            static final MyColor CYAN = new MyColor(0x00ffff);
            private int value;

            MyColor(int value) {
                this.value = value;
            }

            int getRGB() {
                return value;
            }
        }
    }
}
