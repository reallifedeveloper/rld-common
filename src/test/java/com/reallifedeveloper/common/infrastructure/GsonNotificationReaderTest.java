package com.reallifedeveloper.common.infrastructure;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationFactory;
import com.reallifedeveloper.common.application.notification.NotificationReader;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.AbstractDomainEvent;

public class GsonNotificationReaderTest {

    private static final double DELTA = 0.0000001;

    private InMemoryStoredEventRepository storedEventRepository = new InMemoryStoredEventRepository();
    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
    private EventStore eventStore = new EventStore(objectSerializer, storedEventRepository);
    private NotificationFactory notificationFactory = NotificationFactory.instance(eventStore);

    private DateFormat dateFormat = new SimpleDateFormat(GsonObjectSerializer.DATE_FORMAT);

    @Test
    public void realNotification() {
        TestEvent testEvent = new TestEvent(42, "Foo!", 1.05);
        eventStore.add(testEvent);
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        Assert.assertEquals("Wrong number of stored events: ", 1, storedEvents.size());
        List<Notification> notifications = notificationFactory.fromStoredEvents(storedEvents);
        Assert.assertEquals("Wrong number of notifications: ", 1, notifications.size());
        Notification notification = notifications.get(0);
        String serializedNotification = objectSerializer.serialize(notification);
        NotificationReader reader = new GsonNotificationReader(serializedNotification);
        Assert.assertEquals("Wrong event type: ", testEvent.getClass().getName(), reader.eventType());
        Assert.assertEquals("Wrong stored event ID: ", 1, reader.storedEventId());
        Assert.assertEquals("Wrong occurred on timestamp: ", testEvent.occurredOn(), reader.occurredOn());
        Assert.assertEquals("Wrong event version: ", 1, reader.eventVersion());
        Assert.assertEquals("Wrong event ID: ", testEvent.id, reader.eventLongValue("id").longValue());
        Assert.assertEquals("Wrong event name: ", testEvent.name, reader.eventStringValue("name"));
        Assert.assertEquals("Wrong event double value: ", testEvent.d, reader.eventDoubleValue("d"), DELTA);
        Assert.assertEquals("Wrong event color value: ", testEvent.color.getRGB(),
                reader.eventIntValue("color.value").intValue());
    }

    @Test
    public void eventIntValue() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", 42, reader.eventIntValue("foo").intValue());
    }

    @Test
    public void eventIntValueMin() {
        String json = "{\"event\":{\"foo\":" + Integer.MIN_VALUE + "}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", Integer.MIN_VALUE, reader.eventIntValue("foo").intValue());
    }

    @Test
    public void eventIntValueMax() {
        String json = "{\"event\":{\"foo\":" + Integer.MAX_VALUE + "}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", Integer.MAX_VALUE, reader.eventIntValue("foo").intValue());
    }

    @Test
    public void eventIntValueNull() {
        String json = "{\"event\":{\"foo\":null}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Foo should be null", reader.eventIntValue("foo"));
    }

    @Test
    public void eventIntValueNonExisting() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Baz should be null", reader.eventIntValue("baz"));
    }

    @Test(expected = NumberFormatException.class)
    public void eventIntValueNotInteger() {
        String json = "{\"event\":{\"foo\":\"bar\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventIntValue("foo");
    }

    @Test
    public void eventLongValue() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", 42, reader.eventLongValue("foo").longValue());
    }

    @Test
    public void eventLongValueMin() {
        String json = "{\"event\":{\"foo\":" + Long.MIN_VALUE + "}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", Long.MIN_VALUE, reader.eventLongValue("foo").longValue());
    }

    @Test
    public void eventLongValueMax() {
        String json = "{\"event\":{\"foo\":" + Long.MAX_VALUE + "}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", Long.MAX_VALUE, reader.eventLongValue("foo").longValue());
    }

    @Test
    public void eventLongValueNull() {
        String json = "{\"event\":{\"foo\":null}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Foo should be null", reader.eventLongValue("foo"));
    }

    @Test
    public void eventLongValueNonExisting() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Baz should be null", reader.eventLongValue("baz"));
    }

    @Test(expected = NumberFormatException.class)
    public void eventLongValueNotInteger() {
        String json = "{\"event\":{\"foo\":\"bar\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventLongValue("foo");
    }

    @Test
    public void eventDoubleValue() {
        String json = "{\"event\":{\"foo\":47.11}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", 47.11, reader.eventDoubleValue("foo"), DELTA);
    }

    @Test
    public void eventDoubleValueMin() {
        String json = "{\"event\":{\"foo\":" + Double.MIN_VALUE + "}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", Double.MIN_VALUE, reader.eventDoubleValue("foo"), DELTA);
    }

    @Test
    public void eventDoubleValueMax() {
        String json = "{\"event\":{\"foo\":" + Double.MAX_VALUE + "}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", Double.MAX_VALUE, reader.eventDoubleValue("foo"), DELTA);
    }

    @Test
    public void eventDoubleValueNull() {
        String json = "{\"event\":{\"foo\":null}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Foo should be null", reader.eventDoubleValue("foo"));
    }

    @Test
    public void eventDoubleValueNonExisting() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Baz should be null", reader.eventDoubleValue("baz"));
    }

    @Test(expected = NumberFormatException.class)
    public void eventDoubleValueNotNumber() {
        String json = "{\"event\":{\"foo\":\"bar\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventDoubleValue("foo");
    }

    @Test
    public void eventStringValue() {
        String json = "{\"event\":{\"foo\":\"bar\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", "bar", reader.eventStringValue("foo"));
    }

    @Test
    public void eventStringValueNull() {
        String json = "{\"event\":{\"foo\":null}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Foo should be null", reader.eventStringValue("foo"));
    }

    @Test
    public void eventStringValueNonExisting() {
        String json = "{\"event\":{\"foo\":\"bar\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Baz should be null", reader.eventStringValue("baz"));
    }

    @Test
    public void eventDateValue() throws Exception {
        String strDate = "2014-07-21T14:41:00:123+0100";
        Date date = dateFormat.parse(strDate);
        String json = "{\"event\":{\"foo\":\"" + strDate + "\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertEquals("Foo has wrong value: ", date, reader.eventDateValue("foo"));
    }

    @Test
    public void eventDateValueNull() {
        String json = "{\"event\":{\"foo\":null}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Foo should be null", reader.eventDateValue("foo"));
    }

    @Test
    public void eventDateValueNonExisting() {
        String json = "{\"event\":{\"foo\":null}}";
        NotificationReader reader = new GsonNotificationReader(json);
        Assert.assertNull("Baz should be null", reader.eventDateValue("baz"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void eventDateValueNonWrongFormat() {
        String json = "{\"event\":{\"foo\":\"bar\"}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventDateValue("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void eventTypeMissingType() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventType();
    }

    @Test(expected = IllegalArgumentException.class)
    public void notificationIdMissingId() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.storedEventId();
    }

    @Test(expected = IllegalArgumentException.class)
    public void occurredOnMissingDate() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.occurredOn();
    }

    @Test(expected = IllegalArgumentException.class)
    public void eventVersionMissingVersion() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventVersion();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nestedFieldNameNonExistingField() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventStringValue("bar.baz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nestedFieldNameNotAnObject() {
        String json = "{\"event\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventStringValue("foo.bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullJsonObject() {
        new GsonNotificationReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNotAJsonObject() {
        new GsonNotificationReader("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMalformedJsonObject() {
        new GsonNotificationReader("\\//+");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validJsonButNotValidEventMessage() {
        String json = "{\"crap\":{\"foo\":42}}";
        NotificationReader reader = new GsonNotificationReader(json);
        reader.eventStringValue("foo");
    }

    private static class TestEvent extends AbstractDomainEvent {
        private static final long serialVersionUID = 1L;
        private long id;
        private String name;
        private double d;
        private Color color = Color.CYAN;

        TestEvent(long id, String name, double d) {
            super(new Date());
            this.id = id;
            this.name = name;
            this.d = d;
        }
    }
}
