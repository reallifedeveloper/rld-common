package com.reallifedeveloper.common.domain.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link DomainEventSubscriber} that saves all events in memory.
 *
 * @author RealLifeDeveloper
 */
public class EventSavingSubscriber implements DomainEventSubscriber<DomainEvent> {

    private final List<DomainEvent> events = new ArrayList<>();

    @Override
    public void handleEvent(DomainEvent event) {
        events.add(event);

    }

    @Override
    public Class<? extends DomainEvent> eventType() {
        return DomainEvent.class;
    }

    /**
     * Gives an unmodifiable list of the events that have been handled so far.
     *
     * @return a list of the events that have been handled.
     */
    public List<DomainEvent> events() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Removes all handled events.
     */
    public void clear() {
        events.clear();
    }

}
