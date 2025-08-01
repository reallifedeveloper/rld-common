package com.reallifedeveloper.common.application.notification;

import java.util.List;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * A factory for {@link Notification Notifications}.
 *
 * @author RealLifeDeveloper
 */
public final class NotificationFactory {

    private final EventStore eventStore;

    private NotificationFactory(EventStore eventStore) {
        ErrorHandling.checkNull("eventStore must not be null", eventStore);
        this.eventStore = eventStore;
    }

    /**
     * Gives an instance of the factory that uses the given {@link EventStore} to create {@link Notification Notifications}.
     *
     * @param eventStore the {@code EventStore} to use
     *
     * @return a {@code NotificationFactory} instance
     */
    public static NotificationFactory instance(EventStore eventStore) {
        return new NotificationFactory(eventStore);
    }

    /**
     * Creates a new {@link Notification} for the given {@link StoredEvent}.
     *
     * @param storedEvent the stored event for which to create a {@code Notification}
     *
     * @return a new {@code Notification} for the stored event
     */
    public Notification fromStoredEvent(StoredEvent storedEvent) {
        DomainEvent domainEvent = eventStore.toDomainEvent(storedEvent);
        return Notification.create(domainEvent, storedEvent.id());
    }

    /**
     * Creates new {@link Notification Notifications} for the given {@link StoredEvent StoredEvents}.
     *
     * @param storedEvents a list with the stored events for which to create {@code Notifications}
     *
     * @return a list of {@code Notifications} for the stored events
     */
    public List<Notification> fromStoredEvents(List<StoredEvent> storedEvents) {
        ErrorHandling.checkNull("storedEvents must not be null", storedEvents);
        return storedEvents.stream().map(this::fromStoredEvent).toList();
    }
}
