package com.reallifedeveloper.common.application.eventstore;

import java.util.List;

import com.reallifedeveloper.common.domain.Repository;

/**
 * A repository for working with {@link StoredEvent} objects.
 *
 * @author RealLifeDeveloper
 */
public interface StoredEventRepository extends Repository<StoredEvent, Long> {

    /**
     * Gives all <code>StoredEvents</code> with IDs greater than <code>storedEventId</code>, i.e., all
     * events that occurred after the event with the given ID.
     *
     * @param storedEventId find all events with IDs greater than this
     * @return a list of <code>StoredEvents</code> with IDs greater than or equal to <code>firstStoredEventId</code>
     */
    List<StoredEvent> allEventsSince(long storedEventId);

    /**
     * Gives all <code>StoredEvents</code> with IDs greater than or equal to <code>firstStoredEventId</code>
     * and less than or equal to <code>lastStoredEventId</code>, i.e., all events that occurred between the
     * events with the given IDs, inclusive.
     *
     * @param firstStoredEventId ID of the first <code>StoredEvent</code> to retrieve
     * @param lastStoredEventId ID of the last <code>StoredEvent</code> to retrieve
     * @return a list of all <code>StoredEvents</code> with IDs between <code>firstStoredEventId</code>
     * and <code>lastStoredEventId</code>, inclusive
     */
    List<StoredEvent> allEventsBetween(long firstStoredEventId, long lastStoredEventId);

    /**
     * Saves a {@link StoredEvent}.
     *
     * @param storedEvent the <code>StoredEvent</code> to save
     * @param <S> the type of the <code>StoredEvent</code>
     * @return the saved <code>StoredEvent</code>, which may have been changed by the save operation
     */
    <S extends StoredEvent> S save(S storedEvent);

    /**
     * Gives the ID of the most recently saved <code>StoredEvent</code> in the repository.
     *
     * @return the ID of the most recently saved <code>StoredEvent</code> or <code>null</code> if the
     * repository is empty
     */
    Long lastStoredEventId();
}
