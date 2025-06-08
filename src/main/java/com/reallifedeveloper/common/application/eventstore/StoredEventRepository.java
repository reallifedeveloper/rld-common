package com.reallifedeveloper.common.application.eventstore;

import java.util.List;
import java.util.Optional;

import com.reallifedeveloper.common.domain.Repository;

/**
 * A repository for working with {@link StoredEvent} objects.
 *
 * @author RealLifeDeveloper
 */
public interface StoredEventRepository extends Repository<StoredEvent, Long> {

    /**
     * Gives all {@code StoredEvents} with IDs greater than {@code storedEventId}, i.e., all events that occurred after the event with the
     * given ID.
     *
     * @param storedEventId find all events with IDs greater than this
     * @return a list of {@code StoredEvents} with IDs greater than or equal to {@code firstStoredEventId}
     */
    List<StoredEvent> allEventsSince(long storedEventId);

    /**
     * Gives all {@code StoredEvents} with IDs greater than or equal to {@code firstStoredEventId} and less than or equal to
     * {@code lastStoredEventId}, i.e., all events that occurred between the events with the given IDs, inclusive.
     *
     * @param firstStoredEventId ID of the first {@code StoredEvent} to retrieve
     * @param lastStoredEventId  ID of the last {@code StoredEvent} to retrieve
     * @return a list of all {@code StoredEvents} with IDs between {@code firstStoredEventId} and {@code lastStoredEventId}, inclusive
     */
    List<StoredEvent> allEventsBetween(long firstStoredEventId, long lastStoredEventId);

    /**
     * Saves a {@link StoredEvent}.
     *
     * @param storedEvent the {@code StoredEvent} to save
     * @param <S>         the type of the {@code StoredEvent}
     * @return the saved {@code StoredEvent}, which may have been changed by the save operation
     */
    <S extends StoredEvent> S save(S storedEvent);

    /**
     * Gives the ID of the most recently saved {@code StoredEvent} in the repository.
     *
     * @return the ID of the most recently saved {@code StoredEvent} unless the repository is empty
     */
    Optional<Long> lastStoredEventId();
}
