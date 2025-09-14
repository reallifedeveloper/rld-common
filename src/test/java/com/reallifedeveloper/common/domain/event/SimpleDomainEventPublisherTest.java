package com.reallifedeveloper.common.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.reallifedeveloper.tools.test.TestUtil;

public class SimpleDomainEventPublisherTest {

    @Test
    @SuppressWarnings("NullAway")
    public void createPublisherWithNullSubscribers() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleDomainEventPublisher(null));
    }

    @Test
    public void publishWithoutSubscribers() {
        List<DomainEventSubscriber<? extends DomainEvent>> subscribers = new ArrayList<>();
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher(subscribers);
        // Check that we do not get an exception
        publisher.publish(new BaseDomainEvent());
    }

    @Test
    public void publishWithSubscriber() {
        BaseDomainEventSubscriber subscriber = new BaseDomainEventSubscriber();
        List<DomainEventSubscriber<? extends DomainEvent>> subscribers = new ArrayList<>();
        subscribers.add(subscriber);
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher(subscribers);
        assertEquals(0, subscriber.handledEvents().size(), "No events should have been handled: ");
        publisher.publish(new BaseDomainEvent());
        assertEquals(1, subscriber.handledEvents().size(), "Exactly one event should have been handled: ");
    }

    @Test
    public void correctSubscribersGetNotified() {
        BaseDomainEventSubscriber subscriber1 = new BaseDomainEventSubscriber();
        SubDomainEventSubscriber subscriber2 = new SubDomainEventSubscriber();
        List<DomainEventSubscriber<? extends DomainEvent>> subscribers = new ArrayList<>();
        subscribers.add(subscriber1);
        subscribers.add(subscriber2);
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher(subscribers);
        assertEquals(0, subscriber1.handledEvents().size(), "No events should have been handled: ");
        assertEquals(0, subscriber2.handledEvents().size(), "No events should have been handled: ");
        publisher.publish(new BaseDomainEvent());
        publisher.publish(new SubDomainEvent());
        assertEquals(2, subscriber1.handledEvents().size(), "Exactly two events should have been handled: ");
        assertEquals(1, subscriber2.handledEvents().size(), "Exactly one event should have been handled: ");
    }

    @Test
    public void subscribe() {
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher();
        BaseDomainEventSubscriber subscriber = new BaseDomainEventSubscriber();
        publisher.subscribe(subscriber);
        assertEquals(0, subscriber.handledEvents().size(), "No events should have been handled: ");
        publisher.publish(new BaseDomainEvent());
        assertEquals(1, subscriber.handledEvents().size(), "Exactly one event should have been handled: ");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void publishNullEvent() {
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher();
        assertThrows(IllegalArgumentException.class, () -> publisher.publish(null));
    }

    @Test
    @SuppressWarnings("NullAway")
    public void subscribeNullSubscriber() {
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher();
        assertThrows(IllegalArgumentException.class, () -> publisher.subscribe(null));
    }

    static class BaseDomainEvent extends AbstractDomainEvent {
        private static final long serialVersionUID = 1L;

        BaseDomainEvent() {
            super(TestUtil.utcNow());
        }
    }

    static class SubDomainEvent extends BaseDomainEvent {
        private static final long serialVersionUID = 1L;
    }

    /**
     * A {@code DomainEventSubscriber} that listens for any {@code BaseDomainEvent} and holds a list with the events handled.
     */
    static class BaseDomainEventSubscriber implements DomainEventSubscriber<BaseDomainEvent> {

        private List<BaseDomainEvent> events = new ArrayList<>();

        @Override
        public void handleEvent(BaseDomainEvent event) {
            events.add(event);
        }

        @Override
        public Class<? extends BaseDomainEvent> eventType() {
            return BaseDomainEvent.class;
        }

        public List<BaseDomainEvent> handledEvents() {
            return events;
        }
    }

    /**
     * A {@code DomainEventSubscriber} that listens for {@code SubDomainEvent} and holds a list with the events handled.
     */
    static class SubDomainEventSubscriber implements DomainEventSubscriber<SubDomainEvent> {

        private List<SubDomainEvent> events = new ArrayList<>();

        @Override
        public void handleEvent(SubDomainEvent event) {
            events.add(event);
        }

        @Override
        public Class<? extends SubDomainEvent> eventType() {
            return SubDomainEvent.class;
        }

        public List<SubDomainEvent> handledEvents() {
            return events;
        }
    }
}
