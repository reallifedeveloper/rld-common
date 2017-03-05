package com.reallifedeveloper.common.domain.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ThreadLocalDomainEventPublisherTest {

    private ThreadLocalDomainEventPublisher publisher = new ThreadLocalDomainEventPublisher();

    @Before
    public void clearSubscribers() {
        publisher.reset();
    }

    @Test
    public void simpleSubscription() {
        TestSubscriber subscriber = new TestSubscriber(BaseDomainEvent.class);
        publisher.subscribe(subscriber);
        Assert.assertEquals("No events should have been handled: ", 0, subscriber.handledEvents().size());
        publisher.publish(new BaseDomainEvent());
        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber.handledEvents().size());
    }

    @Test
    public void correctSubscribersGetNotified() {
        // Subscriber 1 subscribes to all BaseDomainEvents
        TestSubscriber subscriber1 = new TestSubscriber(BaseDomainEvent.class);
        publisher.subscribe(subscriber1);
        // Subscriber 2 subscribes to only SubDomainEvents
        TestSubscriber subscriber2 = new TestSubscriber(SubDomainEvent.class);
        publisher.subscribe(subscriber2);
        Assert.assertEquals("No events should have been handled: ", 0, subscriber1.handledEvents().size());
        Assert.assertEquals("No events should have been handled: ", 0, subscriber2.handledEvents().size());
        publisher.publish(new BaseDomainEvent());
        publisher.publish(new SubDomainEvent());
        Assert.assertEquals("Exactly two events should have been handled: ", 2, subscriber1.handledEvents().size());
        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber2.handledEvents().size());
    }

    @Test
    public void canSubscribeToAllDomainEvents() {
        TestSubscriber subscriber = new TestSubscriber(DomainEvent.class);
        publisher.subscribe(subscriber);
        Assert.assertEquals("No events should have been handled: ", 0, subscriber.handledEvents().size());
        publisher.publish(new BaseDomainEvent());
        publisher.publish(new SubDomainEvent());
        Assert.assertEquals("Exactly two events should have been handled: ", 2, subscriber.handledEvents().size());
    }

    @Test
    public void noSubscribers() {
        // Verify that we do not get an exception
        publisher.publish(new BaseDomainEvent());
    }

    @Test
    public void threadLocalSubscribers() throws Exception {

        final TestSubscriber subscriber1 = new TestSubscriber(BaseDomainEvent.class);
        final TestSubscriber subscriber2 = new TestSubscriber(BaseDomainEvent.class);

        Thread t1 = new Thread() {
            @Override
            public void run() {
                publisher.subscribe(subscriber1);
                publisher.publish(new BaseDomainEvent());
            }
        };

        Thread t2 = new Thread() {
            @Override
            public void run() {
                publisher.subscribe(subscriber2);
                publisher.publish(new SubDomainEvent());
            }
        };

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber1.handledEvents().size());
        Assert.assertEquals("Wrong event type: ", BaseDomainEvent.class,
                subscriber1.handledEvents().get(0).getClass());

        Assert.assertEquals("Exactly one event should have been handled: ", 1, subscriber2.handledEvents().size());
        Assert.assertEquals("Wrong event type: ", SubDomainEvent.class,
                subscriber2.handledEvents().get(0).getClass());
    }

    @Test
    public void reset() {
        TestSubscriber subscriber = new TestSubscriber(BaseDomainEvent.class);
        publisher.subscribe(subscriber);
        Assert.assertEquals("No events should have been handled: ", 0, subscriber.handledEvents().size());
        publisher.reset();
        publisher.publish(new BaseDomainEvent());
        Assert.assertEquals("No events should have been handled: ", 0, subscriber.handledEvents().size());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribingSubscriberNotAllowed() {
        SubscribingSubscriber subscriber = new SubscribingSubscriber(publisher);
        publisher.subscribe(subscriber);
        publisher.publish(new BaseDomainEvent());
    }

    @Test(expected = IllegalStateException.class)
    public void publishingSubscriberNotAllowed() {
        PublishingSubscriber subscriber = new PublishingSubscriber(publisher);
        publisher.subscribe(subscriber);
        publisher.publish(new BaseDomainEvent());
    }

    @Test(expected = IllegalStateException.class)
    public void resettingSubscriberNotAllowed() {
        ResettingSubscriber subscriber = new ResettingSubscriber(publisher);
        publisher.subscribe(subscriber);
        publisher.publish(new BaseDomainEvent());
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
     * A <code>DomainEventSubscriber</code> that holds a list with the events handled.
     */
    static class TestSubscriber implements DomainEventSubscriber<DomainEvent> {

        private Class<? extends DomainEvent> eventType;
        private List<DomainEvent> events = new ArrayList<>();

        /**
         * Creates a new <code>TestSubscriber</code> that listens for the given type of event.
         *
         * @param eventType the kind of <code>DomainEvent</code> to handle
         */
        TestSubscriber(Class<? extends DomainEvent> eventType) {
            this.eventType = eventType;
        }

        @Override
        public void handleEvent(DomainEvent event) {
            this.events.add(event);
        }

        @Override
        public Class<? extends DomainEvent> eventType() {
            return eventType;
        }

        public List<DomainEvent> handledEvents() {
            return events;
        }

    }

    /**
     * A <code>DomainEventSubscriber</code> that tries to call
     * {@link ThreadLocalDomainEventPublisher#subscribe(DomainEventSubscriber) which is not allowed.
     */
    static class SubscribingSubscriber implements DomainEventSubscriber<DomainEvent> {

        private ThreadLocalDomainEventPublisher publisher;

        SubscribingSubscriber(ThreadLocalDomainEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void handleEvent(DomainEvent event) {
            publisher.subscribe(this);
        }

        @Override
        public Class<? extends DomainEvent> eventType() {
            return DomainEvent.class;
        }
    }

    /**
     * A <code>DomainEventSubscriber</code> that tries to call
     * {@link ThreadLocalDomainEventPublisher#publish(DomainEvent)} which is not allowed.
     */
    static class PublishingSubscriber implements DomainEventSubscriber<DomainEvent> {

        private ThreadLocalDomainEventPublisher publisher;

        PublishingSubscriber(ThreadLocalDomainEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void handleEvent(DomainEvent event) {
            publisher.publish(new BaseDomainEvent());
        }

        @Override
        public Class<? extends DomainEvent> eventType() {
            return DomainEvent.class;
        }
    }

    /**
     * A <code>DomainEventSubscriber</code> that tries to call {@link ThreadLocalDomainEventPublisher#reset()}
     * which is not allowed.
     */
    static class ResettingSubscriber implements DomainEventSubscriber<DomainEvent> {

        private ThreadLocalDomainEventPublisher publisher;

        ResettingSubscriber(ThreadLocalDomainEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void handleEvent(DomainEvent event) {
            publisher.reset();
        }

        @Override
        public Class<? extends DomainEvent> eventType() {
            return DomainEvent.class;
        }
    }
}
