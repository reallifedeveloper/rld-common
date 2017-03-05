package com.reallifedeveloper.common.domain.event;

import java.util.ArrayList;
import java.util.List;

/**
 * A publisher of domain events that holds subscribers in a list. Publishing is handled
 * synchronously; to handle events asynchronously, a subscriber could send a message to
 * a message queue or store the event for later processing.
 * <p>
 * The idea behind this publisher is that it will be configured once and for all, for
 * example using Spring:
 *
 * <pre>
 *   &lt;bean class="th.co.edge.domain.event.SimpleDomainEventPublisher"&gt;
 *     &lt;constructor-arg&gt;
 *       &lt;list&gt;
 *         &lt;bean class="com.foo.FooSubscriber" /&gt;
 *         &lt;bean class="com.foo.BarSubscriber" /&gt;
 *       &lt;/list&gt;
 *     &lt;/constructor-arg&gt;
 *   &lt;/bean&gt;
 * </pre>
 * <p>
 * The publisher can then be injected in the classes that need to publish events:
 *
 * <pre>
 *   &#64;Autowired
 *   private DomainEventSubscriber eventSubscriber;
 * </pre>
 * <p>
 * Used this way in a normal enterprise application, several threads may call the
 * {@link #publish(DomainEvent)} method simultaneously, so the subscribers should
 * be thread safe.
 *
 * @author RealLifeDeveloper
 */
public class SimpleDomainEventPublisher implements DomainEventPublisher {

    private List<DomainEventSubscriber<? extends DomainEvent>> subscribers = new ArrayList<>();

    /**
     * Creates a new <code>SimpleDomainEventPublisher</code> with no subscribers registered.
     * <p>
     * Use the {@link #subscribe(DomainEventSubscriber)} to add subscribers.
     */
    public SimpleDomainEventPublisher() {
        super();
    }

    /**
     * Creates a new <code>SimpleDomainEventPublisher</code> with a number of subscribers
     * registered to be notified when events are published.
     *
     * @param subscribers a list of subscribers to notify when events are published
     * @throws IllegalArgumentException if <code>subscribers</code> is <code>null</code>
     */
    public SimpleDomainEventPublisher(List<DomainEventSubscriber<? extends DomainEvent>> subscribers) {
        if (subscribers == null) {
            throw new IllegalArgumentException("subscribers must not be null");
        }
        this.subscribers = subscribers;
    }

    /**
     * Publishes a domain event, i.e., calls the {@link DomainEventSubscriber#handleEvent(DomainEvent)}
     * method for each registered subscriber.
     *
     * @param event the domain event to publish
     * @throws IllegalArgumentException if <code>event</code> is <code>null</code>
     */
    @Override
    public void publish(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        for (DomainEventSubscriber<? extends DomainEvent> subscriber : subscribers) {
            if (subscriber.eventType().isAssignableFrom(event.getClass())) {
                @SuppressWarnings("unchecked")
                DomainEventSubscriber<DomainEvent> s = (DomainEventSubscriber<DomainEvent>) subscriber;
                s.handleEvent(event);
            }
        }
    }

    /**
     * Registers an event handler with this publisher.
     *
     * @param subscriber the event handler to register
     * @throws IllegalArgumentException if <code>subscriber</code> is <code>null</code>
     */
    @Override
    public void subscribe(DomainEventSubscriber<? extends DomainEvent> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("subscriber must not be null");
        }
        subscribers.add(subscriber);
    }

}
