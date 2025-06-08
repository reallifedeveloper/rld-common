package com.reallifedeveloper.common.application.eventstore;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;
import com.reallifedeveloper.common.test.TestUtil;

public class EventStoreTest {

    private final ObjectSerializer<String> eventSerializer = new GsonObjectSerializer();
    private final StoredEventRepository eventRepository = new InMemoryStoredEventRepository();
    private final EventStore eventStore = new EventStore(eventSerializer, eventRepository);

    @Test
    public void addOneEvent() throws Exception {
        TestEvent event = new TestEvent(42, "foo", ZonedDateTime.now(), 2);
        StoredEvent storedEvent = eventStore.add(event);
        Assertions.assertEquals(1, storedEvent.id().longValue(), "Stored event has wrong ID");
        Assertions.assertEquals(TestEvent.class.getName(), storedEvent.eventType(), "Stored event has wrong type");
        Assertions.assertEquals(event.eventOccurredOn(), storedEvent.occurredOn(), "Stored event timestamp is wrong");
        Assertions.assertEquals(event.eventVersion(), storedEvent.version().intValue(), "Stored event version is wrong");

        TestEvent retrievedEvent = eventSerializer.deserialize(storedEvent.eventBody(), TestEvent.class);
        Assertions.assertEquals(event.id(), retrievedEvent.id(), "Retrieved event has wrong ID");
        Assertions.assertEquals(event.name(), retrievedEvent.name(), "Retrieved event has wrong name");
        TestUtil.assertEquals(event.eventOccurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
        Assertions.assertEquals(event.eventVersion(), retrievedEvent.eventVersion(), "Retrieved event version is wrong");
    }

    @Test
    public void addNullEvent() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventStore.add(null));
    }

    @Test
    public void allEventsSinceWithNoEvents() {
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        Assertions.assertEquals(0, storedEvents.size(), "There should be no stored events");
        Assertions.assertEquals(0, eventStore.lastStoredEventId(), "Wrong last stored event ID");
    }

    @Test
    public void allEventsSince() {
        ZonedDateTime start = ZonedDateTime.now();
        final int numEventsTotal = 10;
        int eventVersion = 3;
        for (int i = 0; i < numEventsTotal; i++) {
            eventStore.add(new TestEvent(i, "foo" + i, start.plusSeconds(i), eventVersion));
            Assertions.assertEquals(i + 1, eventStore.lastStoredEventId(), "Wrong last stored event ID");
        }

        final int firstEventIdToRetrieve = 5;
        final int numEventsToRetrieve = numEventsTotal - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = eventStore.allEventsSince(firstEventIdToRetrieve - 1);
        Assertions.assertEquals(numEventsToRetrieve, storedEvents.size(), "Wrong number of stored events found");

        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assertions.assertEquals(expectedStoredEventId, storedEvent.id().longValue(), "Stored event has wrong ID");
            Assertions.assertEquals(start.plusSeconds(expectedStoredEventId - 1).toInstant(), storedEvent.occurredOn().toInstant(),
             "Stored event timestamp is wrong");
            Assertions.assertEquals(eventVersion, storedEvent.version().intValue(), "Stored event version is wrong");

            TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
            Assertions.assertEquals(expectedStoredEventId - 1, retrievedEvent.id(), "Retrieved event has wrong ID");
            Assertions.assertEquals("foo" + (expectedStoredEventId - 1), retrievedEvent.name(), "Retrieved event has wrong name");
            TestUtil.assertEquals(storedEvent.occurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
            Assertions.assertEquals(storedEvent.version().intValue(), retrievedEvent.eventVersion(), "Retrieved event version is wrong");
        }
    }

    @Test
    public void allEventsBetween() {
        ZonedDateTime start = ZonedDateTime.now();
        final int numEventsTotal = 10;
        int eventVersion = 3;
        for (int i = 0; i < numEventsTotal; i++) {
            eventStore.add(new TestEvent(i, "foo" + i, start.plusSeconds(i), eventVersion));
        }

        final int firstEventIdToRetrieve = 5;
        final int lastEventIdToRetrieve = 7;
        final int numEventsToRetrieve = lastEventIdToRetrieve - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = eventStore.allEventsBetween(firstEventIdToRetrieve, lastEventIdToRetrieve);
        Assertions.assertEquals(numEventsToRetrieve, storedEvents.size(), "Wrong number of stored events found");

        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assertions.assertEquals(expectedStoredEventId, storedEvent.id().longValue(), "Stored event has wrong ID");
            Assertions.assertEquals(start.plusSeconds(expectedStoredEventId - 1).toInstant(), storedEvent.occurredOn().toInstant(),
             "Stored event timestamp is wrong");
            Assertions.assertEquals(eventVersion, storedEvent.version().intValue(), "Stored event version is wrong");

            TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
            Assertions.assertEquals(expectedStoredEventId - 1, retrievedEvent.id(), "Retrieved event has wrong ID");
            Assertions.assertEquals("foo" + (expectedStoredEventId - 1), retrievedEvent.name(), "Retrieved event has wrong name");
            TestUtil.assertEquals(storedEvent.occurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
            Assertions.assertEquals(storedEvent.version().intValue(), retrievedEvent.eventVersion(), "Retrieved event version is wrong");
        }
    }

    @Test
    public void toDomainEvent() throws Exception {
        TestEvent event = new TestEvent(42, "foo", ZonedDateTime.now(), 2);
        StoredEvent storedEvent = eventStore.add(event);
        TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);

        Assertions.assertEquals(event.id(), retrievedEvent.id(), "Retrieved event has wrong ID");
        Assertions.assertEquals(event.name(), retrievedEvent.name(), "Retrieved event has wrong name");
        TestUtil.assertEquals(event.eventOccurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
        Assertions.assertEquals(event.eventVersion(), retrievedEvent.eventVersion(), "Retrieved event has wrong version");
    }

    @Test
    public void toDomainEventNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventStore.toDomainEvent(null));
    }

    @Test
    public void toDomainEventUnknownClass() {
        StoredEvent storedEvent = new StoredEvent("foo", "bar", ZonedDateTime.now(), 1);
        Assertions.assertThrows(IllegalStateException.class, () -> eventStore.toDomainEvent(storedEvent));
    }

    @Test
    public void constructorNullSerializer() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EventStore(null, eventRepository));
    }

    @Test
    public void constructorNullRepository() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EventStore(eventSerializer, null));
    }
}
