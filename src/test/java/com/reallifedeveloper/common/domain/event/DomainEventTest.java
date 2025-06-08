package com.reallifedeveloper.common.domain.event;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.ClockTimeService;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistryTest;

public class DomainEventTest {

    private ZonedDateTime testDateTime = ZonedDateTime.now();

    @BeforeAll
    public static void initClass() {
        CommonDomainRegistryTest.initCommonDomainRegistry();
    }

    @BeforeEach
    public void init() {
        ClockTimeService timeService = (ClockTimeService) CommonDomainRegistry.timeService();
        timeService.setClock(Clock.fixed(testDateTime.toInstant(), testDateTime.getZone()));
    }

    @Test
    public void constructor() {
        TestEvent event = new TestEvent();
        Assertions.assertEquals(testDateTime, event.eventOccurredOn(), "Wrong occurred on: ");
        Assertions.assertEquals(1, event.eventVersion(), "Wrong version: ");
    }

    @Test
    public void constructorVersion() {
        int version = 42;
        TestEvent event = new TestEvent(version);
        Assertions.assertEquals(testDateTime, event.eventOccurredOn(), "Wrong occurred on: ");
        Assertions.assertEquals(version, event.eventVersion(), "Wrong version: ");
    }

    @Test
    public void constructorDateTime() {
        ZonedDateTime now = ZonedDateTime.now();
        TestEvent event = new TestEvent(now);
        Assertions.assertEquals(now, event.eventOccurredOn(), "Wrong occurred on: ");
        Assertions.assertEquals(1, event.eventVersion(), "Wrong version: ");
    }

    @Test
    public void constructorNullDate() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TestEvent((ZonedDateTime) null));
    }

    @Test
    public void constructorDateTimeVersion() {
        ZonedDateTime now = ZonedDateTime.now();
        int version = 42;
        TestEvent event = new TestEvent(now, version);
        Assertions.assertEquals(now, event.eventOccurredOn(), "Wrong occurred on: ");
        Assertions.assertEquals(version, event.eventVersion(), "Wrong version: ");
    }

    @Test
    public void constructorNullDateVersion() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TestEvent(null, 1));
    }

    @Test
    public void equalsHashCodeSameObject() {
        TestEvent event = new TestEvent();
        Assertions.assertEquals(event, event, "event should be equal to event");
        Assertions.assertEquals(event.hashCode(), event.hashCode(), "event should always have the same hashcode");
    }

    @Test
    public void equalsHashcodeSameOccurredOnAndVersion() {
        ZonedDateTime now = ZonedDateTime.now();
        int version = 42;
        TestEvent event1 = new TestEvent(now, version);
        TestEvent event2 = new TestEvent(now, version);
        Assertions.assertEquals(event1, event2, "event1 should be equal to event2");
        Assertions.assertEquals(event1.hashCode(), event2.hashCode(), "event1 and event2 should have the same hashcode");
    }

    @Test
    public void equalsHashcodeDifferentOccurredOns() {
        ZonedDateTime date = testDateTime.plusSeconds(1);
        int version = 42;
        TestEvent event1 = new TestEvent(date, version);
        TestEvent event2 = new TestEvent(testDateTime, version);
        Assertions.assertNotEquals(event1, event2, "event1 should not be equal to event2");
        Assertions.assertNotEquals(event1.hashCode(), event2.hashCode(), "event1 and event2 should not have the same hashcode");
    }

    @Test
    public void equalsHashcodeDifferentVersions() {
        ZonedDateTime now = ZonedDateTime.now();
        int version1 = 42;
        int version2 = 4711;
        TestEvent event1 = new TestEvent(now, version1);
        TestEvent event2 = new TestEvent(now, version2);
        Assertions.assertNotEquals(event1, event2, "event1 should not be equal to event2");
        Assertions.assertNotEquals(event1.hashCode(), event2.hashCode(), "event1 and event2 should not have the same hashcode");
    }

    @Test
    public void equalsNull() {
        TestEvent event = new TestEvent();
        Assertions.assertNotEquals(event, null, "event should not be equal to null");
    }

    @Test
    public void equalsDifferentClass() {
        TestEvent event = new TestEvent();
        Assertions.assertNotEquals(event, "foo", "event should not be equal to foo");
    }

    @Test
    public void testToString() {
        TestEvent event = new TestEvent(testDateTime, 42);
        Assertions.assertEquals("TestEvent{eventOccurredOn=" + testDateTime + ", eventVersion=42}", event.toString());
    }

    private static class TestEvent extends AbstractDomainEvent {

        private static final long serialVersionUID = 1L;

        TestEvent() {
            super();
        }

        TestEvent(int version) {
            super(version);
        }

        TestEvent(ZonedDateTime occurredOn) {
            super(occurredOn);
        }

        TestEvent(ZonedDateTime occurredOn, int version) {
            super(occurredOn, version);
        }
    }
}
