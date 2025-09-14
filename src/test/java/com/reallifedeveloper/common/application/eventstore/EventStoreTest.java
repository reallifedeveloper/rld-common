package com.reallifedeveloper.common.application.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;
import com.reallifedeveloper.common.test.CommonTestUtil;
import com.reallifedeveloper.tools.test.LogbackTestUtil;
import com.reallifedeveloper.tools.test.TestUtil;

@SuppressWarnings("NullAway")
public class EventStoreTest {

    private final ObjectSerializer<String> eventSerializer = new GsonObjectSerializer();
    private final StoredEventRepository eventRepository = new InMemoryStoredEventRepository();
    private final EventStore eventStore = new EventStore(eventSerializer, eventRepository);

    @Test
    public void addOneEvent() throws Exception {
        TestEvent event = new TestEvent(42, "foo", TestUtil.utcNow(), 2);
        StoredEvent storedEvent = eventStore.add(event);
        assertEquals(1, storedEvent.id().longValue(), "Stored event has wrong ID");
        assertEquals(TestEvent.class.getName(), storedEvent.eventType(), "Stored event has wrong type");
        assertEquals(event.eventOccurredOn(), storedEvent.occurredOn(), "Stored event timestamp is wrong");
        assertEquals(event.eventVersion(), storedEvent.version().intValue(), "Stored event version is wrong");

        TestEvent retrievedEvent = eventSerializer.deserialize(storedEvent.eventBody(), TestEvent.class);
        assertEquals(event.id(), retrievedEvent.id(), "Retrieved event has wrong ID");
        assertEquals(event.name(), retrievedEvent.name(), "Retrieved event has wrong name");
        CommonTestUtil.assertEquals(event.eventOccurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
        assertEquals(event.eventVersion(), retrievedEvent.eventVersion(), "Retrieved event version is wrong");
    }

    @Test
    public void addNullEvent() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> eventStore.add(null));
        assertEquals("event must not be null", e.getMessage());
    }

    @Test
    public void allEventsSinceWithNoEvents() {
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        assertEquals(0, storedEvents.size(), "There should be no stored events");
        assertEquals(0, eventStore.lastStoredEventId(), "Wrong last stored event ID");
    }

    @Test
    public void allEventsSince() {
        ZonedDateTime start = TestUtil.utcNow();
        final int numEventsTotal = 10;
        int eventVersion = 3;
        for (int i = 0; i < numEventsTotal; i++) {
            eventStore.add(new TestEvent(i, "foo" + i, start.plusSeconds(i), eventVersion));
            assertEquals(i + 1, eventStore.lastStoredEventId(), "Wrong last stored event ID");
        }

        final int firstEventIdToRetrieve = 5;
        final int numEventsToRetrieve = numEventsTotal - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = eventStore.allEventsSince(firstEventIdToRetrieve - 1);
        assertEquals(numEventsToRetrieve, storedEvents.size(), "Wrong number of stored events found");

        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            assertEquals(expectedStoredEventId, storedEvent.id().longValue(), "Stored event has wrong ID");
            assertEquals(start.plusSeconds(expectedStoredEventId - 1).toInstant(), storedEvent.occurredOn().toInstant(),
                    "Stored event timestamp is wrong");
            assertEquals(eventVersion, storedEvent.version().intValue(), "Stored event version is wrong");

            TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
            assertEquals(expectedStoredEventId - 1, retrievedEvent.id(), "Retrieved event has wrong ID");
            assertEquals("foo" + (expectedStoredEventId - 1), retrievedEvent.name(), "Retrieved event has wrong name");
            CommonTestUtil.assertEquals(storedEvent.occurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
            assertEquals(storedEvent.version().intValue(), retrievedEvent.eventVersion(), "Retrieved event version is wrong");
        }
    }

    @Test
    public void allEventsBetween() {
        ZonedDateTime start = TestUtil.utcNow();
        final int numEventsTotal = 10;
        int eventVersion = 3;
        for (int i = 0; i < numEventsTotal; i++) {
            eventStore.add(new TestEvent(i, "foo" + i, start.plusSeconds(i), eventVersion));
        }

        final int firstEventIdToRetrieve = 5;
        final int lastEventIdToRetrieve = 7;
        final int numEventsToRetrieve = lastEventIdToRetrieve - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = eventStore.allEventsBetween(firstEventIdToRetrieve, lastEventIdToRetrieve);
        assertEquals(numEventsToRetrieve, storedEvents.size(), "Wrong number of stored events found");

        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            assertEquals(expectedStoredEventId, storedEvent.id().longValue(), "Stored event has wrong ID");
            assertEquals(start.plusSeconds(expectedStoredEventId - 1).toInstant(), storedEvent.occurredOn().toInstant(),
                    "Stored event timestamp is wrong");
            assertEquals(eventVersion, storedEvent.version().intValue(), "Stored event version is wrong");

            TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
            assertEquals(expectedStoredEventId - 1, retrievedEvent.id(), "Retrieved event has wrong ID");
            assertEquals("foo" + (expectedStoredEventId - 1), retrievedEvent.name(), "Retrieved event has wrong name");
            CommonTestUtil.assertEquals(storedEvent.occurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
            assertEquals(storedEvent.version().intValue(), retrievedEvent.eventVersion(), "Retrieved event version is wrong");
        }
    }

    @Test
    public void toDomainEvent() throws Exception {
        TestEvent event = new TestEvent(42, "foo", TestUtil.utcNow(), 2);
        StoredEvent storedEvent = eventStore.add(event);
        TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);

        assertEquals(event.id(), retrievedEvent.id(), "Retrieved event has wrong ID");
        assertEquals(event.name(), retrievedEvent.name(), "Retrieved event has wrong name");
        CommonTestUtil.assertEquals(event.eventOccurredOn(), retrievedEvent.eventOccurredOn(), "Retrieved event timestamp is wrong");
        assertEquals(event.eventVersion(), retrievedEvent.eventVersion(), "Retrieved event has wrong version");
    }

    @Test
    public void toDomainEventNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> eventStore.toDomainEvent(null));
        assertEquals("storedEvent must not be null", e.getMessage());
    }

    @Test
    public void toDomainEventUnknownClass() {
        StoredEvent storedEvent = new StoredEvent("foo", "bar", TestUtil.utcNow(), 1);
        Exception e = assertThrows(IllegalStateException.class, () -> eventStore.toDomainEvent(storedEvent));
        assertEquals("Failed to load class foo", e.getMessage());
    }

    @Test
    public void constructorNullSerializer() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new EventStore(null, eventRepository));
        assertEquals("Arguments must not be null: serializer=null, repository=" + eventRepository, e.getMessage());
    }

    @Test
    public void constructorNullRepository() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new EventStore(eventSerializer, null));
        assertEquals("Arguments must not be null: serializer=" + eventSerializer + ", repository=null", e.getMessage());
    }

    @Test
    public void verifyLogging() {
        LogbackTestUtil.clearLoggingEvents();
        Logger logger = (Logger) LoggerFactory.getLogger(EventStore.class);
        Level originalLevel = logger.getLevel();
        logger.setLevel(Level.TRACE);
        ZonedDateTime now = TestUtil.utcNow();

        EventStore myEventStore = new EventStore(eventSerializer, eventRepository);
        assertSingleLogEntry(Level.INFO, "Creating new EventStore: serializer=" + eventSerializer + ", repository=" + eventRepository);

        StoredEvent storedEvent = myEventStore.add(new TestEvent(42, "foo\nbar", now, 2));
        assertSingleLogEntry(Level.TRACE, "add: event=TestEvent{id=42, name=foobar, eventOccurredOn=" + now + ", eventVersion=2}");

        myEventStore.allEventsSince(4711);
        assertSingleLogEntry(Level.TRACE, "allEventsSince: storedEventId=4711");

        myEventStore.allEventsBetween(42, 4711);
        assertSingleLogEntry(Level.TRACE, "allEventsBetween: firstStoredEventId=42, lastStoredEventId=4711");

        myEventStore.toDomainEvent(storedEvent);
        assertSingleLogEntry(Level.TRACE,
                "toDomainEvent: storedEvent=StoredEvent{id=1, eventType=" + TestEvent.class.getName()
                        + ", eventBody={\"id\":42,\"name\":\"foo\\nbar\",\"eventOccurredOn\":\"" + now.truncatedTo(ChronoUnit.MILLIS)
                        + "\",\"eventVersion\":2}, occurredOn=" + now + ", version=2}");

        myEventStore.lastStoredEventId();
        assertSingleLogEntry(Level.TRACE, "lastStoredEventId");

        logger.setLevel(originalLevel);
    }

    private static void assertSingleLogEntry(Level level, String message) {
        List<ILoggingEvent> loggingEvents = LogbackTestUtil.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        ILoggingEvent loggingEvent = loggingEvents.get(0);
        assertEquals(level, loggingEvent.getLevel());
        assertEquals(message, loggingEvent.getFormattedMessage());
        LogbackTestUtil.clearLoggingEvents();
    }
}
