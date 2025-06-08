package com.reallifedeveloper.common.application.eventstore;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class EventStoringSubscriberTest {

    private final ObjectSerializer<String> eventSerializer = new GsonObjectSerializer();
    private final StoredEventRepository eventRepository = new InMemoryStoredEventRepository();
    private final EventStore eventStore = new EventStore(eventSerializer, eventRepository);

    @Test
    public void handleEvent() {
        EventStoringSubscriber subscriber = new EventStoringSubscriber(eventStore);
        TestEvent event = new TestEvent(
                42,
                "foo",
                ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS).withZoneSameInstant(ZoneId.of("+01:00")),
                1
        );
        subscriber.handleEvent(event);

        Assertions.assertEquals(1, eventStore.lastStoredEventId(), "Wrong last stored event ID");
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        Assertions.assertEquals(1, storedEvents.size(), "Wrong number of stored events in list");

        StoredEvent storedEvent = storedEvents.get(0);
        TestEvent retrievedEvent = eventStore.toDomainEvent(storedEvent);
        Assertions.assertEquals(event, retrievedEvent, "Wrong retrieved event");
    }

    @Test
    public void eventType() {
        EventStoringSubscriber subscriber = new EventStoringSubscriber(eventStore);
        Assertions.assertEquals(DomainEvent.class, subscriber.eventType(), "Wrong event type");
    }
}
