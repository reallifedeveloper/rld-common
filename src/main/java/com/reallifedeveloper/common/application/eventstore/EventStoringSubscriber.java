package com.reallifedeveloper.common.application.eventstore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.DomainEventSubscriber;

/**
 * A {@link DomainEventSubscriber} that stores all events using a {@link EventStore}.
 *
 * @author RealLifeDeveloper
 */
public final class EventStoringSubscriber implements DomainEventSubscriber<DomainEvent> {

    private final EventStore eventStore;

    /**
     * Creates a new {@code EventStoringSubscriber} that uses the given {@link EventStore}.
     *
     * @param eventStore the {@code EventStore} to use
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EventStoringSubscriber(EventStore eventStore) {
        ErrorHandling.checkNull("eventStore must not be null", eventStore);
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
