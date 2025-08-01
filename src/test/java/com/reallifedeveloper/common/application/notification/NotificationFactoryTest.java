package com.reallifedeveloper.common.application.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.eventstore.StoredEventRepository;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationFactoryTest {

    private final ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
    private final StoredEventRepository repository = new InMemoryStoredEventRepository();
    private final EventStore eventStore = new EventStore(objectSerializer, repository);

    private final NotificationFactory factory = NotificationFactory.instance(eventStore);

    @Test
    public void fromStoredEventWithSingleEventReturnsNotification() {
        // Given
        long eventId = eventStore.lastStoredEventId();
        TestEvent event = new TestEvent(42, "foo");
        eventStore.add(event);
        StoredEvent storedEvent = eventStore.allEventsSince(eventId).get(0);

        // When
        Notification notification = factory.fromStoredEvent(storedEvent);

        // Then
        assertNotification(notification, event, 1);
    }

    @Test
    public void fromStoredEventWithMultipleEventsReturnsAList() {
        // Given
        long eventId = eventStore.lastStoredEventId();
        TestEvent event1 = new TestEvent(42, "foo");
        eventStore.add(event1);
        TestEvent event2 = new TestEvent(4711, "bar");
        eventStore.add(event2);
        List<StoredEvent> storedEvents = eventStore.allEventsSince(eventId);

        // When
        List<Notification> notifications = factory.fromStoredEvents(storedEvents);

        // Then
        assertEquals(2, notifications.size());
        assertNotification(notifications.get(0), event1, 1);
        assertNotification(notifications.get(1), event2, 2);
    }

    private static void assertNotification(Notification notification, DomainEvent event, long storedEventId) {
        assertNotNull(notification);
        assertEquals(event, notification.event());
        assertEquals(event.getClass().getName(), notification.eventType());
        assertEquals(storedEventId, notification.storedEventId());
    }

    @Test
    public void fromStoreventWithNullEventShouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> factory.fromStoredEvent(null));
        assertEquals("storedEvent must not be null", e.getMessage());
    }

    @Test
    public void fromStoredEventsWillNullListShouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> factory.fromStoredEvents(null));
        assertEquals("storedEvents must not be null", e.getMessage());
    }

    @Test
    public void instanceWithNullEventStoreShouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> NotificationFactory.instance(null));
        assertEquals("eventStore must not be null", e.getMessage());
    }
}
