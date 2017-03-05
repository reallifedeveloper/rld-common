package com.reallifedeveloper.common.application.notification;

/**
 * Repository to work with {@link PublishedMessageTracker} objects.
 *
 * @author RealLifeDeveloper
 */
public interface PublishedMessageTrackerRepository {

    /**
     * Gives the {@link PublishedMessageTracker} associated with the given publication channel.
     *
     * @param publicationChannel the name of the publication channel
     *
     * @return the <code>PublishedMessageTracker</code> associated with <code>publicationChannel</code>
     */
    PublishedMessageTracker findByPublicationChannel(String publicationChannel);

    /**
     * Saves or updates the given {@link PublishedMessageTracker}.
     *
     * @param messageTracker the <code>PublishedMessageTracker</code> to save
     * @param <P> the type of <code>PublishedMessageTracker</code>, may be a sub-class
     *
     * @return the saved <code>PublishedMessageTracker</code>, which may have been changed by the operation
     */
    <P extends PublishedMessageTracker> P save(P messageTracker);

}
