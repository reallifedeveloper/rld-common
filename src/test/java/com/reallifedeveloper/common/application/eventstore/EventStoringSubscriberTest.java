package com.reallifedeveloper.common.application.eventstore;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class EventStoringSubscriberTest {

    private ObjectSerializer<String> eventSerializer = new GsonObjectSerializer();
    private StoredEventRepository eventRepository = new InMemoryStoredEventRepository();
    private EventStore eventStore = new EventStore(eventSerializer, eventRepository);

    @Test
    public void handleEvent() {
        EventStoringSubscriber subscriber = new EventStoringSubscriber(eventStore);
        TestEvent event = new TestEvent(42, "foo", new Date(), 1);
        subscriber.handleEvent(event);
        Assert.assertEquals("Wrong last stored event ID: ", 1, eventStore.lastStoredEventId());
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        Assert.assertEquals("Wrong number of stored events in list: ", 1, storedEvents.size());
        StoredEvent storedEvent = storedEvents.get(0);
        TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
        Assert.assertEquals("Wrong retrieved event: ", event, retrievedEvent);
    }

    @Test
    public void eventType() {
        EventStoringSubscriber subscriber = new EventStoringSubscriber(eventStore);
        Assert.assertEquals("Wrong event type: ", DomainEvent.class, subscriber.eventType());
    }
}
