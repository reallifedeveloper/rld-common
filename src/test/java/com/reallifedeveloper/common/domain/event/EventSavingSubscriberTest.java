package com.reallifedeveloper.common.domain.event;

import org.junit.Assert;
import org.junit.Test;

public class EventSavingSubscriberTest {

    @Test
    public void handleEvent() {
        TestEvent event1 = new TestEvent(1, "foo");
        TestEvent event2 = new TestEvent(2, "bar");
        EventSavingSubscriber subscriber = new EventSavingSubscriber();
        subscriber.handleEvent(event1);
        Assert.assertEquals("Wrong number of handled events: ", 1, subscriber.events().size());
        Assert.assertEquals("Wrong handled event at position 0: ", event1, subscriber.events().get(0));
        subscriber.handleEvent(event2);
        Assert.assertEquals("Wrong number of handled events: ", 2, subscriber.events().size());
        Assert.assertEquals("Wrong handled event at position 0: ", event1, subscriber.events().get(0));
        Assert.assertEquals("Wrong handled event at position 1: ", event2, subscriber.events().get(1));
    }

    @Test
    public void clear() {
        EventSavingSubscriber subscriber = new EventSavingSubscriber();
        subscriber.handleEvent(new TestEvent(1, "foo"));
        Assert.assertEquals("Wrong number of handled events: ", 1, subscriber.events().size());
        subscriber.clear();
        Assert.assertTrue("Handled events should be empty after clear", subscriber.events().isEmpty());
    }

    @Test
    public void eventType() {
        EventSavingSubscriber subscriber = new EventSavingSubscriber();
        Assert.assertEquals("Wrong event type: ", DomainEvent.class, subscriber.eventType());
    }
}
