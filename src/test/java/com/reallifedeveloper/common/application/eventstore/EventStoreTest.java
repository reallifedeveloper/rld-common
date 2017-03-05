package com.reallifedeveloper.common.application.eventstore;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class EventStoreTest {

    private ObjectSerializer<String> eventSerializer = new GsonObjectSerializer();
    private StoredEventRepository eventRepository = new InMemoryStoredEventRepository();
    private EventStore eventStore = new EventStore(eventSerializer, eventRepository);

    @Test
    public void addOneEvent() throws Exception {
        TestEvent event = new TestEvent(42, "foo", new Date(), 2);
        StoredEvent storedEvent = eventStore.add(event);
        Assert.assertEquals("Stored event has wrong ID: ", 1, storedEvent.id().longValue());
        Assert.assertEquals("Stored event has wrong type: ", TestEvent.class.getName(), storedEvent.eventType());
        Assert.assertEquals("Stored event timestamp is wrong: ", event.occurredOn(), storedEvent.occurredOn());
        Assert.assertEquals("Stored event version is wrong: ", event.version(), storedEvent.version().intValue());
        TestEvent retrievedEvent = eventSerializer.deserialize(storedEvent.eventBody(), TestEvent.class);
        Assert.assertEquals("Retrieved event has wrong ID: ", event.id(), retrievedEvent.id());
        Assert.assertEquals("Retrieved event has wrong name: ", event.name(), retrievedEvent.name());
        Assert.assertEquals("Retrieved event timestamp is wrong: ", event.occurredOn(), retrievedEvent.occurredOn());
        Assert.assertEquals("Retrieved event version is wrong: ", event.version(), retrievedEvent.version());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullEvent() {
        eventStore.add(null);
    }

    @Test
    public void allEventsSinceWithNoEvents() {
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        Assert.assertEquals("There should be no stored events: ", 0, storedEvents.size());
        Assert.assertEquals("Wrong last stored event ID: ", 0, eventStore.lastStoredEventId());
    }

    @Test
    public void allEventsSince() {
        Date startDate = new Date();
        final int numEventsTotal = 10;
        int eventVersion = 3;
        for (int i = 0; i < numEventsTotal; i++) {
            // StoredEvents get IDs 1, 2, 3, and so on since we use an InMemoryStoredEventRepository.
            eventStore.add(new TestEvent(i, "foo" + i, new Date(startDate.getTime() + i), eventVersion));
            Assert.assertEquals("Wrong last stored event ID: ", i + 1, eventStore.lastStoredEventId());
        }
        final int firstEventIdToRetrieve = 5;
        final int numEventsToRetrieve = numEventsTotal - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = eventStore.allEventsSince(firstEventIdToRetrieve - 1);
        Assert.assertEquals("Wrong number of stored events found: ", numEventsToRetrieve, storedEvents.size());
        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assert.assertEquals("Stored event has wrong ID: ", expectedStoredEventId, storedEvent.id().longValue());
            Assert.assertEquals("Stored event timestamp is wrong: ",
                    startDate.getTime() + expectedStoredEventId - 1, storedEvent.occurredOn().getTime());
            Assert.assertEquals("Stored event version is wrong: ", eventVersion, storedEvent.version().intValue());
            TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
            Assert.assertEquals("Retrieved event has wrong ID: ", expectedStoredEventId - 1, retrievedEvent.id());
            Assert.assertEquals("Retrieved event has wrong name: ",
                    "foo" + (expectedStoredEventId - 1), retrievedEvent.name());
            Assert.assertEquals("Retrieved event timestamp is wrong: ", storedEvent.occurredOn(),
                    retrievedEvent.occurredOn());
            Assert.assertEquals("Retrieved event version is wrong: ", storedEvent.version().intValue(),
                    retrievedEvent.version());
        }
    }

    @Test
    public void allEventsBetween() {
        Date startDate = new Date();
        final int numEventsTotal = 10;
        int eventVersion = 3;
        for (int i = 0; i < numEventsTotal; i++) {
            // StoredEvents get IDs 1, 2, 3, and so on since we use an InMemoryStoredEventRepository.
            eventStore.add(new TestEvent(i, "foo" + i, new Date(startDate.getTime() + i), eventVersion));
        }
        final int firstEventIdToRetrieve = 5;
        final int lastEventIdToRetrieve = 7;
        final int numEventsToRetrieve = lastEventIdToRetrieve - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = eventStore.allEventsBetween(firstEventIdToRetrieve, lastEventIdToRetrieve);
        Assert.assertEquals("Wrong number of stored events found: ", numEventsToRetrieve, storedEvents.size());
        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assert.assertEquals("Stored event has wrong ID: ", expectedStoredEventId, storedEvent.id().longValue());
            Assert.assertEquals("Stored event timestamp is wrong: ",
                    startDate.getTime() + expectedStoredEventId - 1, storedEvent.occurredOn().getTime());
            Assert.assertEquals("Stored event version is wrong: ", eventVersion, storedEvent.version().intValue());
            TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
            Assert.assertEquals("Retrieved event has wrong ID: ", expectedStoredEventId - 1, retrievedEvent.id());
            Assert.assertEquals("Retrieved event has wrong name: ",
                    "foo" + (expectedStoredEventId - 1), retrievedEvent.name());
            Assert.assertEquals("Retrieved event timestamp is wrong: ", storedEvent.occurredOn(),
                    retrievedEvent.occurredOn());
            Assert.assertEquals("Retrieved event version is wrong: ", storedEvent.version().intValue(),
                    retrievedEvent.version());
        }
    }

    @Test
    public void toDomainEvent() throws Exception {
        TestEvent event = new TestEvent(42, "foo", new Date(), 2);
        StoredEvent storedEvent = eventStore.add(event);
        TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
        Assert.assertEquals("Retrieved event has wrong ID: ", event.id(), retrievedEvent.id());
        Assert.assertEquals("Retrieved event has wrong name: ", event.name(), retrievedEvent.name());
        Assert.assertEquals("Retrieved event timestamp is wrong: ", event.occurredOn(), retrievedEvent.occurredOn());
        Assert.assertEquals("Retrieved event has wrong version: ", event.version(), retrievedEvent.version());
    }

    @Test(expected = IllegalArgumentException.class)
    public void toDomainEventNull() {
        eventStore.toDomainEvent(null);
    }

    @Test(expected = IllegalStateException.class)
    public void toDomainEventUnknownClass() {
        StoredEvent storedEvent = new StoredEvent("foo", "bar", new Date(), 1);
        eventStore.toDomainEvent(storedEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullSerializer() {
        new EventStore(null, eventRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullRepository() {
        new EventStore(eventSerializer, null);
    }

    @Test
    public void constructorNoArguments() {
        // Make sure we don't get an exception
        new EventStore();
    }
}
