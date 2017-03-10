package com.reallifedeveloper.common.infrastructure.persistence;

import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.eventstore.StoredEventRepository;
import com.reallifedeveloper.tools.test.TestUtil;
import com.reallifedeveloper.tools.test.database.dbunit.AbstractDbTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring-context-rld-common-test.xml" })
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

    @Before
    public void init() throws Exception {
        // In dbunit/stored_event.xml we insert 10 events, so we want the next event ID to be 11.
        // This works for test databases using sequences for primary key generation, such as HSQLDB.
        try (Statement statement = ds.getConnection().createStatement()) {
            statement.executeUpdate("ALTER SEQUENCE hibernate_sequence RESTART WITH 11");
        }
    }

    @Test
    public void allEventsSince() {
        Date startDate = TestUtil.parseDateTime("2014-06-07 13:52:00");
        final int numEventsTotal = 10;
        final int firstEventIdToRetrieve = 6;
        final int numEventsToRetrieve = numEventsTotal - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = repository.allEventsSince(firstEventIdToRetrieve - 1);
        Assert.assertEquals("Wrong number of stored events found: ", numEventsToRetrieve, storedEvents.size());
        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assert.assertEquals("Stored event has wrong ID: ", expectedStoredEventId, storedEvent.id().longValue());
            Assert.assertEquals("Stored event has wrong type: ", "foo" + expectedStoredEventId,
                    storedEvent.eventType());
            Assert.assertEquals("Stored event has wrong body: ", "bar" + expectedStoredEventId,
                    storedEvent.eventBody());
            Assert.assertEquals("Stored event timestamp is wrong: ",
                    startDate.getTime() + (expectedStoredEventId - 1) * 1000, storedEvent.occurredOn().getTime());
            Assert.assertEquals("Stored event version is wrong: ", 2, storedEvent.version().intValue());
        }
    }

    @Test
    public void allEventsSinceWithNoEvents() {
        List<StoredEvent> storedEvents = repository.allEventsSince(4711);
        Assert.assertEquals("There should be no stored events: ", 0, storedEvents.size());
    }

    @Test
    public void allEventsBetween() throws Exception {
        Date startDate = TestUtil.parseDateTime("2014-06-07 13:52:00");
        final int firstEventIdToRetrieve = 5;
        final int lastEventIdToRetrieve = 7;
        final int numEventsToRetrieve = lastEventIdToRetrieve - firstEventIdToRetrieve + 1;
        List<StoredEvent> storedEvents = repository.allEventsBetween(firstEventIdToRetrieve, lastEventIdToRetrieve);
        Assert.assertEquals("Wrong number of stored events found: ", numEventsToRetrieve, storedEvents.size());
        for (int i = 0; i < numEventsToRetrieve; i++) {
            StoredEvent storedEvent = storedEvents.get(i);
            int expectedStoredEventId = i + firstEventIdToRetrieve;
            Assert.assertEquals("Stored event has wrong type: ", "foo" + expectedStoredEventId,
                    storedEvent.eventType());
            Assert.assertEquals("Stored event has wrong body: ", "bar" + expectedStoredEventId,
                    storedEvent.eventBody());
            Assert.assertEquals("Stored event timestamp is wrong: ",
                    startDate.getTime() + (expectedStoredEventId - 1) * 1000, storedEvent.occurredOn().getTime());
            Assert.assertEquals("Stored event version is wrong: ", 2, storedEvent.version().intValue());
        }
    }

    @Test
    public void allEventsBetweenWithNoEvents() {
        List<StoredEvent> storedEvents = repository.allEventsBetween(4711, 4711);
        Assert.assertEquals("There should be no stored events: ", 0, storedEvents.size());
    }

    @Test
    public void saveEvent() throws Exception {
        StoredEvent storedEvent = new StoredEvent("foo", "bar", new Date(), 1);
        repository.save(storedEvent);
        Assert.assertNotNull("ID should have been set", storedEvent.id());
        List<StoredEvent> storedEvents = repository.allEventsBetween(storedEvent.id(), storedEvent.id());
        Assert.assertEquals("Wrong number of stored events found: ", 1, storedEvents.size());
        Assert.assertEquals("Wrong last stored event ID: ", storedEvent.id(), repository.lastStoredEventId());
    }

    @Test
    public void lastStoredEventId() {
        Assert.assertEquals("Wrong last stored event ID: ", 10, repository.lastStoredEventId().longValue());
    }

    @Test
    public void lastStoredEventIdNoEvents() {
        ((JpaStoredEventRepository) repository).deleteAll();
        Assert.assertNull("Last stored event ID in empty repository should be null", repository.lastStoredEventId());
    }

    @Override
    protected DataSource getDataSource() {
        return ds;
    }

    @Override
    protected IDataTypeFactory getDataTypeFactory() {
        return dataTypeFactory;
    }
}
