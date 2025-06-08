package com.reallifedeveloper.common.domain.event;

import java.util.ArrayList;
import java.util.List;

/**
 * A publisher of domain events that keeps track of subscribers on a per-thread basis. It is assumed that subscription and publishing are
 * done by the same thread, and publishing is handled synchronously. To handle events asynchronously, a subscriber could send a message to a
 * message queue, or store the event for later processing.
 * <p>
 * If threads are reused, it is important to call the {@link #reset()} method to clear any previous subscribers.
 * <p>
 * The normal use-case for this class is as follows:
 * <ul>
 * <li>A request comes in to an application service.</li>
 * <li>The application service creates or retrieves an instance of this class and calls the {@link #reset()} method.</li>
 * <li>The application service registers all necessary subscribers using the {@link #subscribe(DomainEventSubscriber)} method.</li>
 * <li>The application service delegates to domain services or aggregates, which publish events when something interesting happens in the
 * domain, using the {@link #publish(DomainEvent)} method.</li>
 * </ul>
 *
 * @author RealLifeDeveloper
 */
public class ThreadLocalDomainEventPublisher implements DomainEventPublisher {

    private static ThreadLocal<List<DomainEventSubscriber<DomainEvent>>> subscribers = ThreadLocal.withInitial(ArrayList::new);

    private static ThreadLocal<Boolean> publishing = ThreadLocal.withInitial(() -> false);

    /**
     * Registers an event handler with this publisher.
     *
     * @param subscriber the event handler to register
     *
     * @throws IllegalStateException if called while publishing events
     */
    @Override
    public void subscribe(DomainEventSubscriber<? extends DomainEvent> subscriber) {
        checkPublishing();
        @SuppressWarnings("unchecked")
        DomainEventSubscriber<DomainEvent> s = (DomainEventSubscriber<DomainEvent>) subscriber;
        subscribers().add(s);
    }

    /**
     * Publishes a domain event, i.e., calls the {@link DomainEventSubscriber#handleEvent(DomainEvent)} method for each registered
     * subscriber.
     *
     * @param event the domain event to publish
     *
     * @throws IllegalStateException if called while already publishing events
     */
    @Override
    public void publish(DomainEvent event) {
        checkPublishing();
        try {
            publishing.set(true);
            for (DomainEventSubscriber<DomainEvent> subscriber : subscribers()) {
                if (subscriber.eventType().isAssignableFrom(event.getClass())) {
                    subscriber.handleEvent(event);
                }
            }
        } finally {
            publishing.set(false);
        }
    }

    /**
     * Removes all subscribers. Since subscribers are stored on a per-thread basis, and since threads may be reused, this method should be
     * called when starting to handle a new request.
     *
     * @throws IllegalStateException if called while publishing events
     */
    public void reset() {
        checkPublishing();
        subscribers().clear();
    }

    private List<DomainEventSubscriber<DomainEvent>> subscribers() {
        return subscribers.get();
    }

    private void checkPublishing() {
        if (publishing.get()) {
            throw new IllegalStateException("Method should not be called while publishing events");
        }
    }
}
