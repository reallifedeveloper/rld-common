package com.reallifedeveloper.common.domain.event;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;

import com.reallifedeveloper.common.test.TestUtil;

public class TestEvent extends AbstractDomainEvent {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public TestEvent(int id, String name, ZonedDateTime occurredOn, int version) {
        super(occurredOn, version);
        this.id = id;
        this.name = name;
    }

    public TestEvent(int id, String name, ZonedDateTime occurredOn) {
        super(occurredOn);
        this.id = id;
        this.name = name;
    }

    public TestEvent(int id, String name) {
        super(ZonedDateTime.now());
        this.id = id;
        this.name = name;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestEvent other = (TestEvent) obj;
        // We do not call super.equals(), instead we compare eventOccurredOn and eventVersion here in order to round eventOccurredOn to
        // milliseconds and to use the time-zone offset instead of time-zone name when comparing. This way, we can use assertEquals
        // for events that have passed through processing that rounds to milliseconds.
        return TestUtil.format(eventOccurredOn()).equals(TestUtil.format(other.eventOccurredOn()))
                && Objects.equals(eventVersion(), other.eventVersion()) && Objects.equals(id, other.id) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "TestEvent{id=" + id() + ", name=" + name() + ", eventOccurredOn=" + eventOccurredOn() + ", eventVersion=" + eventVersion()
                + "}";
    }

    public static void assertTestEventsEqual(TestEvent expected, TestEvent actual) {
        Assertions.assertEquals(expected.id(), actual.id(), "Event has wrong ID: ");
        Assertions.assertEquals(expected.name(), actual.name(), "Event has wrong name: ");
        TestUtil.assertEquals(expected.eventOccurredOn(), actual.eventOccurredOn(), "Event has wrong occurredOn: ");
        Assertions.assertEquals(expected.eventVersion(), actual.eventVersion(), "Event has wrong version: ");
    }
}
