package com.reallifedeveloper.common.infrastructure.persistence;

import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.eventstore.StoredEventRepository;
import com.reallifedeveloper.tools.test.database.dbunit.AbstractDbTest;

@SpringJUnitConfig(locations = { "classpath:META-INF/spring-context-rld-common-test.xml" })
public class JpaStoredEventRepositoryIT extends AbstractDbTest {

    @Autowired
    private StoredEventRepository repository;

    @Autowired
    private DataSource ds;

    @Autowired
    private IDataTypeFactory dataTypeFactory;

    public JpaStoredEventRepositoryIT() {
        super(null, "/dbunit/rld-common.dtd", "/dbunit/stored_event.xml");
    }

    @BeforeEach
    public void init() throws Exception {
        // Reset sequence for test databases that use sequences for primary key generation.
        try (Statement statement = ds.getConnection().createStatement()) {
            statement.executeUpdate("ALTER SEQUENCE stored_event_seq RESTART WITH 60");
        }
    }

    @Test
    public void allEventsSince() {
        ZonedDateTime startDateTime = ZonedDateTime.parse("2014-06-07T13:52:00Z");
        final int numEventsTotal = 10;
        final int firstEventIdToRetrieve = 6;
        final int numEventsToRetrieve = numEventsTotal - firstEventIdToRetrieve + 1;

        List<StoredEvent> storedEvents = repository.allEventsSince(firstEventIdToRetrieve - 1);
        Assertions.assertEquals(numEventsToRetrieve, storedEvents.size(), "Wrong number of stored events found");

        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assertions.assertEquals(expectedStoredEventId, storedEvent.id().longValue(), "Stored event has wrong ID");
            Assertions.assertEquals("foo" + expectedStoredEventId, storedEvent.eventType(), "Stored event has wrong type");
            Assertions.assertEquals("bar" + expectedStoredEventId, storedEvent.eventBody(), "Stored event has wrong body");
            Assertions.assertEquals(startDateTime.plusSeconds(expectedStoredEventId - 1), storedEvent.occurredOn(),
             "Stored event timestamp is wrong");
            Assertions.assertEquals(2, storedEvent.version().intValue(), "Stored event version is wrong");
        }
    }

    @Test
    public void allEventsSinceWithNoEvents() {
        List<StoredEvent> storedEvents = repository.allEventsSince(4711);
        Assertions.assertEquals(0, storedEvents.size(), "There should be no stored events");
    }

    @Test
    public void allEventsBetween() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTime.parse("2014-06-07T13:52:00Z");
        final int firstEventIdToRetrieve = 5;
        final int lastEventIdToRetrieve = 7;
        final int numEventsToRetrieve = lastEventIdToRetrieve - firstEventIdToRetrieve + 1;

        List<StoredEvent> storedEvents = repository.allEventsBetween(firstEventIdToRetrieve, lastEventIdToRetrieve);
        Assertions.assertEquals(numEventsToRetrieve, storedEvents.size(), "Wrong number of stored events found");

        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assertions.assertEquals("foo" + expectedStoredEventId, storedEvent.eventType(), "Stored event has wrong type");
            Assertions.assertEquals("bar" + expectedStoredEventId, storedEvent.eventBody(), "Stored event has wrong body");
            Assertions.assertEquals(startDateTime.plusSeconds(expectedStoredEventId - 1), storedEvent.occurredOn(),
             "Stored event timestamp is wrong");
            Assertions.assertEquals(2, storedEvent.version().intValue(), "Stored event version is wrong");
        }
    }

    @Test
    public void allEventsBetweenWithNoEvents() {
        List<StoredEvent> storedEvents = repository.allEventsBetween(4711, 4711);
        Assertions.assertEquals(0, storedEvents.size(), "There should be no stored events");
    }

    @Test
    public void saveEvent() throws Exception {
        StoredEvent storedEvent = new StoredEvent("foo", "bar", ZonedDateTime.now(), 1);
        repository.save(storedEvent);

        Assertions.assertNotNull(storedEvent.id(), "ID should have been set");

        List<StoredEvent> storedEvents = repository.allEventsBetween(storedEvent.id(), storedEvent.id());
        Assertions.assertEquals(1, storedEvents.size(), "Wrong number of stored events found");
        Assertions.assertEquals(storedEvent.id(), repository.lastStoredEventId().get(), "Wrong last stored event ID");
    }

    @Test
    public void lastStoredEventId() {
        Assertions.assertEquals(10, repository.lastStoredEventId().get().longValue(), "Wrong last stored event ID");
    }

    @Test
    public void lastStoredEventIdNoEvents() {
        ((JpaStoredEventRepository) repository).deleteAll();
        Assertions.assertTrue(repository.lastStoredEventId().isEmpty(), "Last stored event ID in empty repository should be empty");
    }

    @Override
    protected DataSource getDataSource() {
        return ds;
    }

    @Override
    protected Optional<IDataTypeFactory> getDataTypeFactory() {
        return Optional.of(dataTypeFactory);
    }
}
