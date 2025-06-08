package com.reallifedeveloper.common.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.event.AbstractDomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.test.TestUtil;

public class GsonObjectSerializerTest {

    private final GsonObjectSerializer serializer = new GsonObjectSerializer();

    @Test
    public void serializeAndDeserializeEvent() {
        TestEvent event = new TestEvent(1, "foo");
        String serializedEvent = serializer.serialize(event);
        TestEvent deserializedEvent = serializer.deserialize(serializedEvent, event.getClass());
        TestEvent.assertTestEventsEqual(event, deserializedEvent);
    }

    @Test
    public void serializeAndDeserializeNotification() {
        TestEvent event = new TestEvent(42, "foo");
        Notification notification = Notification.create(event, 4711L);
        String serializedNotification = serializer.serialize(notification);
        Notification deserializedNotification = serializer.deserialize(serializedNotification, Notification.class);
        Assertions.assertEquals(notification.eventType(), deserializedNotification.eventType(),
                "Deserialized notification has wrong event type");
        Assertions.assertEquals(notification.storedEventId(), deserializedNotification.storedEventId(),
                "Deserialized notification has wrong stored event ID");
        Assertions.assertEquals(notification.occurredOn().truncatedTo(ChronoUnit.MILLIS).toInstant(),
                deserializedNotification.occurredOn().toInstant(), "Deserialized notification has wrong timestamp");
        TestEvent.assertTestEventsEqual(event, (TestEvent) deserializedNotification.event());
    }

    @Test
    public void deserializeNotificationWithEmptyEvent() {
        String serializedNotificationWithoutEvent = """
                {
                    "eventType":"%s",
                    "storedEventId":42,
                    "eventVersion":1
                }
                """.formatted(TestEvent.class.getName()).replaceAll("\\s", "");
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> serializer.deserialize(serializedNotificationWithoutEvent, Notification.class));
        assertEquals("JSON notification is missing event: json=" + serializedNotificationWithoutEvent, e.getMessage());
    }

    @Test
    public void serializeAndDeserializeWrongClass() {
        ZonedDateTime eventOccurredOn = ZonedDateTime.ofInstant(Instant.ofEpochMilli(10000), ZoneOffset.UTC);
        TestEvent event = new TestEvent(1, "foo", eventOccurredOn);
        String serializedEvent = serializer.serialize(event);
        TestEvent2 deserializedEvent = serializer.deserialize(serializedEvent, TestEvent2.class);
        Assertions.assertEquals(42, deserializedEvent.id(), "Deserialized event has wrong ID");
        Assertions.assertEquals(eventOccurredOn, deserializedEvent.eventOccurredOn(), "Deserialized event timestamp is wrong");
    }

    @Test
    public void serializeAndDeserializeLocalDate() {
        TestEvent3 event = new TestEvent3(LocalDate.now(), LocalDateTime.now());
        String serializedEvent = serializer.serialize(event);
        JsonObject jsonObject = JsonParser.parseString(serializedEvent).getAsJsonObject();
        String localDate = jsonObject.get("localDate").getAsString();
        Assertions.assertEquals(event.localDate.toString(), localDate, "Unexpected LocalDate in serialized form");
        TestEvent3 deserializedEvent = serializer.deserialize(serializedEvent, TestEvent3.class);
        Assertions.assertEquals(event.localDate, deserializedEvent.localDate, "Unexpected LocalDate in deserialized object");
    }

    @Test
    public void serializeAndDeserializeNullLocalDate() {
        TestEvent3 event = new TestEvent3(null, LocalDateTime.now());
        String serializedEvent = serializer.serialize(event);
        JsonObject jsonObject = JsonParser.parseString(serializedEvent).getAsJsonObject();
        Assertions.assertNull(jsonObject.get("localDate"), "LocalDate should be null in serialized form");
        TestEvent3 deserializedEvent = serializer.deserialize(serializedEvent, TestEvent3.class);
        Assertions.assertNull(deserializedEvent.localDate, "LocalDate should be null in deserialized form");
    }

    @Test
    public void serializeAndDeserializeLocalDateTime() {
        TestEvent3 event = new TestEvent3(LocalDate.now(), LocalDateTime.now());
        String serializedEvent = serializer.serialize(event);
        JsonObject jsonObject = JsonParser.parseString(serializedEvent).getAsJsonObject();
        String localDateTime = jsonObject.get("localDateTime").getAsString();
        Assertions.assertEquals(TestUtil.format(event.localDateTime), localDateTime, "Unexpected LocalDateTime in serialized form");
        TestEvent3 deserializedEvent = serializer.deserialize(serializedEvent, TestEvent3.class);
        TestUtil.assertEquals(event.localDateTime, deserializedEvent.localDateTime, "Unexpected LocalDateTime in deserialized object");
    }

    @Test
    public void serializeAndDeserializeNullLocalDateTime() {
        TestEvent3 event = new TestEvent3(LocalDate.now(), null);
        String serializedEvent = serializer.serialize(event);
        JsonObject jsonObject = JsonParser.parseString(serializedEvent).getAsJsonObject();
        Assertions.assertNull(jsonObject.get("localDateTime"), "LocalDateTime should be null in serialized form");
        TestEvent3 deserializedEvent = serializer.deserialize(serializedEvent, TestEvent3.class);
        Assertions.assertNull(deserializedEvent.localDateTime, "LocalDateTime should be null in deserialized form");
    }

    @Test
    public void deserializeIncorrectString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> serializer.deserialize("foo", TestEvent.class));
    }

    @Test
    public void serializeNull() {
        Assertions.assertEquals("null", serializer.serialize(null), "null should serialize to 'null'");
    }

    @Test
    public void deserializeNullSerializedEvent() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(null, TestEvent.class));
    }

    @Test
    public void deserializeNullEventType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> serializer.deserialize("null", null));
    }

    @Test
    public void deserializeTheStringNull() {
        Assertions.assertNull(serializer.deserialize("null", TestEvent.class), "Deserializing 'null' should give null");
    }

    private static final class TestEvent2 extends AbstractDomainEvent {
        private static final long serialVersionUID = 1L;

        private final int id = 42;

        private TestEvent2() {
            super(ZonedDateTime.now());
        }

        public int id() {
            return id;
        }

        @Override
        public String toString() {
            return "TestEvent2{id=" + id() + ", occurredOn=" + eventOccurredOn() + "}";
        }
    }

    private static final class TestEvent3 extends AbstractDomainEvent {
        private static final long serialVersionUID = 1L;

        private LocalDate localDate = LocalDate.now();
        private LocalDateTime localDateTime = LocalDateTime.now();

        private TestEvent3(LocalDate localDate, LocalDateTime localDateTime) {
            super(ZonedDateTime.now());
            this.localDate = localDate;
            this.localDateTime = localDateTime;
        }
    }
}
