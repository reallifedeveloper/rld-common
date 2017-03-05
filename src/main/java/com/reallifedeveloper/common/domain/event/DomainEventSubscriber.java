package com.reallifedeveloper.common.domain.event;

/**
 * A handler of domain events.
 *
 * @author RealLifeDeveloper
 *
 * @param <T> the type of domain event to handle
 */
public interface DomainEventSubscriber<T extends DomainEvent> {

    /**
     * Handles the given event.
     *
     * @param event the domain event to handle
     */
    void handleEvent(T event);

    /**
     * Gives the type of domain event to handle.
     *
     * @return the type of domain event to handle
     */
    Class<? extends T> eventType();

}
