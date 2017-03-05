package com.reallifedeveloper.common.application.eventstore;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * An <code>EventStore</code> saves {@link DomainEvent DomainEvents} in a database as
 * {@link StoredEvent StoredEvents}.
 *
 * @author RealLifeDeveloper
 */
public class EventStore {

    private static final Logger LOG = LoggerFactory.getLogger(EventStore.class);

    @Autowired
    private ObjectSerializer<String> serializer;

    @Autowired
    private StoredEventRepository repository;

    /**
     * Creates a new <code>EventStore</code> with the given serializer and repository.
     *
     * @param serializer the <code>DomainEventSerializer</code> to use to serialize
     * and deserialize <code>DomainEvents</code>
     * @param repository the <code>StoredEventRepository</code> to use to work with
     * persisted <code>StoredEvents</code>
     */
    public EventStore(ObjectSerializer<String> serializer, StoredEventRepository repository) {
        if (serializer == null || repository == null) {
            throw new IllegalArgumentException("Arguments must not be null: serializer=" + serializer + ", repository="
                    + repository);
        }
        LOG.trace("EventStore: serializer={}, repository={}", serializer, repository);
        this.serializer = serializer;
        this.repository = repository;
    }

    EventStore() {
        // Used by Spring
    }

    /**
     * Adds a new {@link StoredEvent} representing the given {@link DomainEvent} to the event store.
     *
     * @param event the <code>DomainEvent</code> to add
     * @return the saved <code>StoredEvent</code> representing <code>event</code>
     */
    public StoredEvent add(DomainEvent event) {
        LOG.trace("add: event={}", event);
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        String serializedEvent = serializer.serialize(event);
        StoredEvent storedEvent = new StoredEvent(event.getClass().getName(), serializedEvent, event.occurredOn(),
                event.version());
        return repository.save(storedEvent);
    }

    /**
     * Gives all <code>StoredEvents</code> with IDs greater than <code>storedEventId</code>, i.e., all
     * events that occurred after the event with the given ID.
     *
     * @param storedEventId return all events with IDs greater than this
     * @return a list of <code>StoredEvents</code> with IDs greater than or equal to <code>firstStoredEventId</code>
     */
    public List<StoredEvent> allEventsSince(long storedEventId) {
        LOG.trace("allEventsSince: storedEventId={}", storedEventId);
        return repository.allEventsSince(storedEventId);
    }

    /**
     * Gives all <code>StoredEvents</code> with IDs greater than or equal to <code>firstStoredEventId</code>
     * and less than or equals to <code>lastStoredEventId</code>, i.e., all events that occurred between the
     * events with the given IDs, inclusive.
     *
     * @param firstStoredEventId ID of the first <code>StoredEvent</code> to retrieve
     * @param lastStoredEventId ID of the last <code>StoredEvent</code> to retrieve
     * @return a list of all <code>StoredEvents</code> with IDs between <code>firstStoredEventId</code>
     * and <code>lastStoredEventId</code>, inclusive
     */
    public List<StoredEvent> allEventsBetween(long firstStoredEventId, long lastStoredEventId) {
        LOG.trace("allEventsBetween: firstStoredEventId={}, lastStoredEventId={}", firstStoredEventId,
                lastStoredEventId);
        return repository.allEventsBetween(firstStoredEventId, lastStoredEventId);
    }

    /**
     * Converts a {@link StoredEvent} back to its original <code>DomainEvent</code>.
     * <p>
     * This is only guaranteed to work if the same kind of <code>EventStore</code>, using
     * the same type of <code>DomainEventSerializer</code>, was used to add the
     * <code>DomainEvent</code>.
     *
     * @param storedEvent the <code>StoredEvent</code> to convert
     * @param <T> the type of <code>DomainEvent</code> to return
     * @return the original <code>DomainEvent</code> represented by <code>storedEvent</code>
     * @throws IllegalArgumentException if <code>storedEvent</code> is <code>null</code>
     * @throws IllegalStateException if loading of the class <code>T</code> failed
     */
    public <T extends DomainEvent> T toDomainEvent(StoredEvent storedEvent) {
        LOG.trace("toDomainEvent: storedEvent={}", storedEvent);
        if (storedEvent == null) {
            throw new IllegalArgumentException("storedEvent must not be null");
        }
        try {
            @SuppressWarnings("unchecked")
            Class<T> eventClass = (Class<T>) Class.forName(storedEvent.eventType());
            return serializer.deserialize(storedEvent.eventBody(), eventClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load class " + storedEvent.eventType(), e);
        }
    }

    /**
     * Gives the ID of the most recently added <code>StoredEvents</code>.
     *
     * @return the ID of the most recently added <code>StoredEvent</code>
     */
    public long lastStoredEventId() {
        LOG.trace("lastStoredEventId");
        Long lastStoredEventId = repository.lastStoredEventId();
        if (lastStoredEventId == null) {
            return 0;
        } else {
            return lastStoredEventId;
        }
    }
}
