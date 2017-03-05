package com.reallifedeveloper.common.domain.event;

/**
 * A publisher of domain events. Subscribers can register with implementations of this interface
 * to be notified when events occur and are published.
 *
 * @author RealLifeDeveloper
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event, i.e., notifies each registered subscriber about the event.
     *
     * @param event the event to publish
     */
    void publish(DomainEvent event);

    /**
     * Registers an event handler with this publisher.
     *
     * @param subscriber the event handler to register
     * @throws IllegalArgumentException if <code>subscriber</code> is <code>null</code>
     */
    void subscribe(DomainEventSubscriber<? extends DomainEvent> subscriber);

}
