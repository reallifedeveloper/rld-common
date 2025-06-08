package com.reallifedeveloper.common.application.notification;

import java.io.IOException;
import java.util.List;

/**
 * A publisher of notifications.
 * <p>
 * An implementation of this interface normally uses a messaging system to publish messages containing information about the notifications.
 * In this case, the <i>publicationChannel</i> that is used by the {@link #publish(List, String)} method would correspond to an exchange
 * name.
 *
 * @author RealLifeDeveloper
 */
public interface NotificationPublisher {

    /**
     * Publishes a number of notifications to the given publication channel.
     *
     * @param notifications      a list of {@code Notifications} to publish
     * @param publicationChannel the name of the publication channel
     *
     * @throws IOException if publishing failed
     */
    void publish(List<Notification> notifications, String publicationChannel) throws IOException;

}
