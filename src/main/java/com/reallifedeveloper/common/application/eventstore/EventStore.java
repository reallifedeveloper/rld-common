package com.reallifedeveloper.common.application.eventstore;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * An {@code EventStore} saves {@link DomainEvent DomainEvents} in a database as
 * {@link StoredEvent StoredEvents}.
 *
 * @author RealLifeDeveloper
 */
public final class EventStore {

    private static final Logger LOG = LoggerFactory.getLogger(EventStore.class);

    private final ObjectSerializer<String> serializer;

    private final StoredEventRepository repository;

    /**
     * Creates a new {@code EventStore} with the given serializer and repository.
     *
     * @param serializer the {@code DomainEventSerializer} to use to serialize
     * and deserialize {@code DomainEvents}
     * @param repository the {@code StoredEventRepository} to use to work with
     * persisted {@code StoredEvents}
     */
    public EventStore(ObjectSerializer<String> serializer, StoredEventRepository repository) {
        if (serializer == null || repository == null) {
            throw new IllegalArgumentException("Arguments must not be null: serializer=" + serializer + ", repository="
                    + repository);
        }
        LOG.info("EventStore: serializer={}, repository={}", serializer, repository);
        this.serializer = serializer;
        this.repository = repository;
    }

    /**
     * Adds a new {@link StoredEvent} representing the given {@link DomainEvent} to the event store.
     *
     * @param event the {@code DomainEvent} to add
     * @return the saved {@code StoredEvent} representing {@code event}
     */
    public StoredEvent add(DomainEvent event) {
        LOG.trace("add: event={}", event);
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        String serializedEvent = serializer.serialize(event);
        StoredEvent storedEvent = new StoredEvent(event.getClass().getName(), serializedEvent, event.eventOccurredOn(),
                event.eventVersion());
        return repository.save(storedEvent);
    }

    /**
     * Gives all {@code StoredEvents} with IDs greater than {@code storedEventId}, i.e., all
     * events that occurred after the event with the given ID.
     *
     * @param storedEventId return all events with IDs greater than this
     * @return a list of {@code StoredEvents} with IDs greater than or equal to {@code firstStoredEventId}
     */
    public List<StoredEvent> allEventsSince(long storedEventId) {
        LOG.trace("allEventsSince: storedEventId={}", storedEventId);
        return repository.allEventsSince(storedEventId);
    }

    /**
     * Gives all {@code StoredEvents} with IDs greater than or equal to {@code firstStoredEventId}
     * and less than or equals to {@code lastStoredEventId}, i.e., all events that occurred between the
     * events with the given IDs, inclusive.
     *
     * @param firstStoredEventId ID of the first {@code StoredEvent} to retrieve
     * @param lastStoredEventId ID of the last {@code StoredEvent} to retrieve
     * @return a list of all {@code StoredEvents} with IDs between {@code firstStoredEventId}
     * and {@code lastStoredEventId}, inclusive
     */
    public List<StoredEvent> allEventsBetween(long firstStoredEventId, long lastStoredEventId) {
        LOG.trace("allEventsBetween: firstStoredEventId={}, lastStoredEventId={}", firstStoredEventId,
                lastStoredEventId);
        return repository.allEventsBetween(firstStoredEventId, lastStoredEventId);
    }

    /**
     * Converts a {@link StoredEvent} back to its original {@code DomainEvent}.
     * <p>
     * This is only guaranteed to work if the same kind of {@code EventStore}, using
     * the same type of {@code DomainEventSerializer}, was used to add the
     * {@code DomainEvent}.
     *
     * @param storedEvent the {@code StoredEvent} to convert
     * @param <T> the type of {@code DomainEvent} to return
     * @return the original {@code DomainEvent} represented by {@code storedEvent}
     * @throws IllegalArgumentException if {@code storedEvent} is {@code null}
     * @throws IllegalStateException if loading of the class {@code T} failed
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
     * Gives the ID of the most recently added {@code StoredEvents}.
     *
     * @return the ID of the most recently added {@code StoredEvent}
     */
    public long lastStoredEventId() {
        LOG.trace("lastStoredEventId");
        return repository.lastStoredEventId().orElse(0L);
    }
}
