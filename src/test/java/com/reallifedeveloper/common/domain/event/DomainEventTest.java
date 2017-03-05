package com.reallifedeveloper.common.domain.event;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.reallifedeveloper.common.domain.TestTimeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring-context-rld-common-registry-test.xml" })
public class DomainEventTest {

    @Autowired
    private TestTimeService timeService;

    private Date testDate = new Date();

    @Before
    public void init() {
        timeService.setDates(testDate);
    }

    @Test
    public void constructor() {
        TestEvent event = new TestEvent();
        Assert.assertEquals("Wrong occurred on: ", testDate, event.occurredOn());
        Assert.assertEquals("Wrong version: ", 1, event.version());
    }

    @Test
    public void constructorVersion() {
        int version = 42;
        TestEvent event = new TestEvent(version);
        Assert.assertEquals("Wrong occurred on: ", testDate, event.occurredOn());
        Assert.assertEquals("Wrong version: ", version, event.version());
    }

    @Test
    public void constructorDate() {
        Date date = new Date();
        TestEvent event = new TestEvent(date);
        Assert.assertEquals("Wrong occurred on: ", date, event.occurredOn());
        Assert.assertEquals("Wrong version: ", 1, event.version());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDate() {
        new TestEvent(null);
    }

    @Test
    public void constructorDateVersion() {
        Date date = new Date();
        int version = 42;
        TestEvent event = new TestEvent(date, version);
        Assert.assertEquals("Wrong occurred on: ", date, event.occurredOn());
        Assert.assertEquals("Wrong version: ", version, event.version());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDateVersion() {
        new TestEvent(null, 1);
    }

    @Test
    public void equalsHashCodeSameObject() {
        TestEvent event = new TestEvent();
        Assert.assertTrue("event should be equal to event", event.equals(event));
        Assert.assertTrue("event should always have the same hashcode", event.hashCode() == event.hashCode());
    }

    @Test
    public void equalsHashcodeSameDateAndVersion() {
        Date date = new Date();
        int version = 42;
        TestEvent event1 = new TestEvent(date, version);
        TestEvent event2 = new TestEvent(date, version);
        Assert.assertTrue("event1 should be equal to event2", event1.equals(event2));
        Assert.assertTrue("event1 and event2 should have the same hashcode", event1.hashCode() == event2.hashCode());
    }

    @Test
    public void equalsHashcodeDifferentDates() {
        Date date = new Date(testDate.getTime() + 1);
        int version = 42;
        TestEvent event1 = new TestEvent(date, version);
        TestEvent event2 = new TestEvent(testDate, version);
        Assert.assertFalse("event1 should not be equal to event2", event1.equals(event2));
        Assert.assertFalse("event1 and event2 should not have the same hashcode",
                event1.hashCode() == event2.hashCode());
    }

    @Test
    public void equalsHashcodeDifferentVersions() {
        Date date = new Date();
        int version1 = 42;
        int version2 = 4711;
        TestEvent event1 = new TestEvent(date, version1);
        TestEvent event2 = new TestEvent(date, version2);
        Assert.assertFalse("event1 should not be equal to event2", event1.equals(event2));
        Assert.assertFalse("event1 and event2 should not have the same hashcode",
                event1.hashCode() == event2.hashCode());
    }

    @Test
    public void equalsNull() {
        TestEvent event = new TestEvent();
        Assert.assertFalse("event should not be equal to null", event.equals(null));
    }

    @Test
    public void equalsDifferentClass() {
        TestEvent event = new TestEvent();
        Assert.assertFalse("event should not be equal to foo", event.equals("foo"));
    }

    @Test
    public void testToString() {
        TestEvent event = new TestEvent(testDate, 42);
        Assert.assertEquals("TestEvent{occurredOn=" + testDate + ", version=42}", event.toString());
    }

    private static class TestEvent extends AbstractDomainEvent {

        private static final long serialVersionUID = 1L;

        TestEvent() {
            super();
        }

        TestEvent(int version) {
            super(version);
        }

        TestEvent(Date date) {
            super(date);
        }

        TestEvent(Date date, int version) {
            super(date, version);
        }
    }
}
