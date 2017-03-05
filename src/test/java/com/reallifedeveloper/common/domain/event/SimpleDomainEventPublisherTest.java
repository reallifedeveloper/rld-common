package com.reallifedeveloper.common.domain.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SimpleDomainEventPublisherTest {

    @Test(expected = IllegalArgumentException.class)
    public void createPublisherWithNullSubscribers() {
        new SimpleDomainEventPublisher(null);
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
        Assert.assertEquals("No events should have been handled: ", 0, subscriber.handledEvents().size());
        publisher.publish(new BaseDomainEvent());
        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber.handledEvents().size());
    }

    @Test
    public void correctSubscribersGetNotified() {
        BaseDomainEventSubscriber subscriber1 = new BaseDomainEventSubscriber();
        SubDomainEventSubscriber subscriber2 = new SubDomainEventSubscriber();
        List<DomainEventSubscriber<? extends DomainEvent>> subscribers = new ArrayList<>();
        subscribers.add(subscriber1);
        subscribers.add(subscriber2);
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher(subscribers);
        Assert.assertEquals("No events should have been handled: ", 0, subscriber1.handledEvents().size());
        Assert.assertEquals("No events should have been handled: ", 0, subscriber2.handledEvents().size());
        publisher.publish(new BaseDomainEvent());
        publisher.publish(new SubDomainEvent());
        Assert.assertEquals("Exactly two events should have been handled: ", 2, subscriber1.handledEvents().size());
        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber2.handledEvents().size());
    }

    @Test
    public void subscribe() {
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher();
        BaseDomainEventSubscriber subscriber = new BaseDomainEventSubscriber();
        publisher.subscribe(subscriber);
        Assert.assertEquals("No events should have been handled: ", 0, subscriber.handledEvents().size());
        publisher.publish(new BaseDomainEvent());
        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber.handledEvents().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void publishNullEvent() {
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher();
        publisher.publish(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subscribeNullSubscriber() {
        SimpleDomainEventPublisher publisher = new SimpleDomainEventPublisher();
        publisher.subscribe(null);
    }

    static class BaseDomainEvent extends AbstractDomainEvent {
        private static final long serialVersionUID = 1L;
        BaseDomainEvent() {
            super(new Date());
        }
    }

    static class SubDomainEvent extends BaseDomainEvent {
        private static final long serialVersionUID = 1L;
    }

    /**
     * A <code>DomainEventSubscriber</code> that listens for any <code>BaseDomainEvent</code>
     * and holds a list with the events handled.
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
     * A <code>DomainEventSubscriber</code> that listens for <code>SubDomainEvent</code>
     * and holds a list with the events handled.
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
