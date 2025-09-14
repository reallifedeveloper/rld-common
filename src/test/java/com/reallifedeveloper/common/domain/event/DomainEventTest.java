package com.reallifedeveloper.common.domain.event;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.reallifedeveloper.common.domain.ClockTimeService;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistryTest;
import com.reallifedeveloper.tools.test.TestUtil;

public class DomainEventTest {

    private ZonedDateTime testDateTime = TestUtil.utcNow();

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
        ZonedDateTime now = TestUtil.utcNow();
        TestEvent event = new TestEvent(now);
        Assertions.assertEquals(now, event.eventOccurredOn(), "Wrong occurred on: ");
        Assertions.assertEquals(1, event.eventVersion(), "Wrong version: ");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullDate() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TestEvent((ZonedDateTime) null));
    }

    @Test
    public void constructorDateTimeVersion() {
        ZonedDateTime now = TestUtil.utcNow();
        int version = 42;
        TestEvent event = new TestEvent(now, version);
        Assertions.assertEquals(now, event.eventOccurredOn(), "Wrong occurred on: ");
        Assertions.assertEquals(version, event.eventVersion(), "Wrong version: ");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullDateVersion() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TestEvent(null, 1));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(TestEvent.class).verify();
    }

    @Test
    public void testToString() {
        TestEvent event = new TestEvent(testDateTime, 42);
        Assertions.assertEquals("TestEvent{eventOccurredOn=" + testDateTime + ", eventVersion=42}", event.toString());
    }

    private static final class TestEvent extends AbstractDomainEvent {

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
