package com.reallifedeveloper.common.application.notification;

import java.util.Optional;

/**
 * Repository to work with {@link PublishedMessageTracker} objects.
 *
 * @author RealLifeDeveloper
 */
public interface PublishedMessageTrackerRepository {

    /**
     * Gives the {@link PublishedMessageTracker} associated with the given publication channel, if available.
     *
     * @param publicationChannel the name of the publication channel
     *
     * @return the {@code PublishedMessageTracker} associated with {@code publicationChannel}, if available
     */
    Optional<PublishedMessageTracker> findByPublicationChannel(String publicationChannel);

    /**
     * Saves or updates the given {@link PublishedMessageTracker}.
     *
     * @param messageTracker the {@code PublishedMessageTracker} to save
     * @param <P>            the type of {@code PublishedMessageTracker}, may be a sub-class
     *
     * @return the saved {@code PublishedMessageTracker}, which may have been changed by the operation
     */
    <P extends PublishedMessageTracker> P save(P messageTracker);

}
