package com.reallifedeveloper.common.infrastructure;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.domain.event.AbstractDomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;

public class GsonObjectSerializerTest {

    private GsonObjectSerializer serializer = new GsonObjectSerializer();

    @Test
    public void serializeAndDeserialize() {
        TestEvent event = new TestEvent(1, "foo");
        String serializedEvent = serializer.serialize(event);
        TestEvent deserializedEvent = serializer.deserialize(serializedEvent, event.getClass());
        Assert.assertEquals("Deserialized event has wrong ID: ", event.id(), deserializedEvent.id());
        Assert.assertEquals("Deserialized event has wrong name: ", event.name(), deserializedEvent.name());
        Assert.assertEquals("Deserialized event timestamp is wrong: ", event.occurredOn(),
                deserializedEvent.occurredOn());
    }

    @Test
    public void serializeAndDeserializeWrongClass() {
        Date eventOccurredOn = new Date(10000);
        TestEvent event = new TestEvent(1, "foo", eventOccurredOn);
        String serializedEvent = serializer.serialize(event);
        TestEvent2 deserializedEvent = serializer.deserialize(serializedEvent, TestEvent2.class);
        Assert.assertEquals("Deserialized event has wrong ID: ", 42, deserializedEvent.id());
        Assert.assertEquals("Deserialized event timestamp is wrong: ", eventOccurredOn,
                deserializedEvent.occurredOn());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeIncorrectString() {
        serializer.deserialize("foo", TestEvent.class);
    }

    @Test
    public void serializeNull() {
        Assert.assertEquals("null should serialize to 'null': ", "null", serializer.serialize(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNullSerializedEvent() {
        serializer.deserialize(null, TestEvent.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNullEventType() {
        serializer.deserialize("null", null);
    }

    @Test
    public void deserializeTheStringNull() {
        Assert.assertNull("Deserializing 'null' should give null: ", serializer.deserialize("null", TestEvent.class));
    }

    private static final class TestEvent2 extends AbstractDomainEvent {

        private static final long serialVersionUID = 1L;

        private final int id = 42;

        private TestEvent2() {
            super(new Date());
        }

        public int id() {
            return id;
        }

        @Override
        public String toString() {
            return "TestEvent2{id=" + id() + ", occurredOn=" + occurredOn() + "}";
        }
    }
}
