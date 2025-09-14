package com.reallifedeveloper.common.domain.event;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;

import com.reallifedeveloper.common.test.CommonTestUtil;
import com.reallifedeveloper.tools.test.TestUtil;

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
        super(TestUtil.utcNow());
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
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TestEvent other) {
            // We do not call super.equals(), instead we compare eventOccurredOn and eventVersion here in order to round eventOccurredOn to
            // milliseconds and to use the time-zone offset instead of time-zone name when comparing. This way, we can use assertEquals
            // for events that have passed through processing that rounds to milliseconds.
            return CommonTestUtil.format(eventOccurredOn()).equals(CommonTestUtil.format(other.eventOccurredOn()))
                    && eventVersion() == other.eventVersion() && id == other.id && Objects.equals(name, other.name);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TestEvent{id=" + id() + ", name=" + name() + ", eventOccurredOn=" + eventOccurredOn() + ", eventVersion=" + eventVersion()
                + "}";
    }

    public static void assertTestEventsEqual(TestEvent expected, TestEvent actual) {
        Assertions.assertEquals(expected.id(), actual.id(), "Event has wrong ID: ");
        Assertions.assertEquals(expected.name(), actual.name(), "Event has wrong name: ");
        CommonTestUtil.assertEquals(expected.eventOccurredOn(), actual.eventOccurredOn(), "Event has wrong occurredOn: ");
        Assertions.assertEquals(expected.eventVersion(), actual.eventVersion(), "Event has wrong version: ");
    }
}
