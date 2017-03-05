package com.reallifedeveloper.common.application.eventstore;

import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.DomainEventSubscriber;

/**
 * A {@link DomainEventSubscriber} that stores all events using a {@link EventStore}.
 *
 * @author RealLifeDeveloper
 */
public class EventStoringSubscriber implements DomainEventSubscriber<DomainEvent> {

    private final EventStore eventStore;

    /**
     * Creates a new <code>EventStoringSubscriber</code> that uses the given {@link EventStore}.
     *
     * @param eventStore the <code>EventStore</code> to use
     */
    public EventStoringSubscriber(EventStore eventStore) {
        if (eventStore == null) {
            throw new IllegalArgumentException("eventStore must not be null");
        }
        this.eventStore = eventStore;
    }

    @Override
    public void handleEvent(DomainEvent event) {
        eventStore.add(event);
    }

    @Override
    public Class<? extends DomainEvent> eventType() {
        return DomainEvent.class;
    }

}
