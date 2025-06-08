package com.reallifedeveloper.common.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.tools.test.LogbackTestUtil;

public class LoggingDomainEventSubscriberTest {

    @BeforeEach
    public void init() throws Exception {
        LogbackTestUtil.clearLoggingEvents();
    }

    @Test
    public void handleEvent() {
        LoggingDomainEventSubscriber subscriber = new LoggingDomainEventSubscriber();
        TestEvent event = new TestEvent(42, "foo", ZonedDateTime.now(), 1);
        assertTrue(LogbackTestUtil.getLoggingEvents().isEmpty(), "There should be no logging events");
        subscriber.handleEvent(event);
        assertEquals(1, LogbackTestUtil.getLoggingEvents().size(), "Wrong number of logging events: ");
        assertEquals(event.toString(), LogbackTestUtil.getLoggingEvents().get(0).getMessage(), "Wrong log message: ");
    }

    @Test
    public void eventType() {
        LoggingDomainEventSubscriber subscriber = new LoggingDomainEventSubscriber();
        assertEquals(DomainEvent.class, subscriber.eventType(), "Wrong event type: ");
    }
}
