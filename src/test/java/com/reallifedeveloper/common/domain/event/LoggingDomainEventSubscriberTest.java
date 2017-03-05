package com.reallifedeveloper.common.domain.event;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.reallifedeveloper.tools.test.Log4jTestAppender;

public class LoggingDomainEventSubscriberTest {

    private Log4jTestAppender testAppender = new Log4jTestAppender();

    @Before
    public void init() throws Exception {
        testAppender.setThreshold(Level.INFO);
        Logger.getRootLogger().addAppender(testAppender);
    }

    @Test
    public void handleEvent() {
        LoggingDomainEventSubscriber subscriber = new LoggingDomainEventSubscriber();
        TestEvent event = new TestEvent(42, "foo", new Date(), 1);
        Assert.assertTrue("There should be no logging events", testAppender.loggingEvents().isEmpty());
        subscriber.handleEvent(event);
        Assert.assertEquals("Wrong number of logging events: ", 1, testAppender.loggingEvents().size());
        Assert.assertEquals("Wrong log message: ", event.toString(), testAppender.loggingEvents().get(0)
                .getMessage());
    }

    @Test
    public void eventType() {
        LoggingDomainEventSubscriber subscriber = new LoggingDomainEventSubscriber();
        Assert.assertEquals("Wrong event type: ", DomainEvent.class, subscriber.eventType());
    }
}
