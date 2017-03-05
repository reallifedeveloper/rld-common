package com.reallifedeveloper.common.application.notification;

import java.util.ArrayList;
import java.util.List;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * A factory for {@link Notification Notifications}.
 *
 * @author RealLifeDeveloper
 */
public final class NotificationFactory {

    private final EventStore eventStore;

    private NotificationFactory(EventStore eventStore) {
        if (eventStore == null) {
            throw new IllegalArgumentException("eventStore must not be null");
        }
        this.eventStore = eventStore;
    }

    /**
     * Gives an instance of the factory that uses the given {@link EventStore} to create
     * {@link Notification Notifications}.
     *
     * @param eventStore the <code>EventStore</code> to use
     *
     * @return a <code>NotificationFactory</code> instance
     */
    public static NotificationFactory instance(EventStore eventStore) {
        return new NotificationFactory(eventStore);
    }

    /**
     * Creates a new {@link Notification} for the given {@link StoredEvent}.
     *
     * @param storedEvent the stored event for which to create a <code>Notification</code>
     *
     * @return a new <code>Notification</code> for the stored event
     */
    public Notification fromStoredEvent(StoredEvent storedEvent) {
        DomainEvent domainEvent = eventStore.toDomainEvent(storedEvent);
        return  new Notification(domainEvent, storedEvent.id());
    }

    /**
     * Creates new {@link Notification Notifications} for the given {@link StoredEvent StoredEvents}.
     *
     * @param storedEvents a list with the stored events for which to create <code>Notifications</code>
     *
     * @return a list of <code>Notifications</code> for the stored events
     */
    public List<Notification> fromStoredEvents(List<StoredEvent> storedEvents) {
        if (storedEvents == null) {
            throw new IllegalArgumentException("storedEvents must not be null");
        }
        List<Notification> notifications = new ArrayList<>(storedEvents.size());
        for (StoredEvent storedEvent : storedEvents) {
            notifications.add(fromStoredEvent(storedEvent));
        }
        return notifications;
    }
}
