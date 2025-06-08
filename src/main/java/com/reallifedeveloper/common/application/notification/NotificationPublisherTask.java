package com.reallifedeveloper.common.application.notification;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * A {@code java.lang.Runnable} that calls {@link NotificationService#publishNotifications(String)}. It can be used for scheduling regular
 * publication using a timer service.
 *
 * @author RealLifeDeveloper
 */
public final class NotificationPublisherTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPublisherTask.class);

    private final String publicationChannel;

    private final NotificationService notificationService;

    /**
     * Creates a new {@code NotificationPublishingTimerTask} that publishes to the given publication channel using the given
     * {@link NotificationService}.
     *
     * @param publicationChannel  the name of the channel to which to publish
     * @param notificationService the {@code NotificationService} to use to do the actual publishing
     */
    public NotificationPublisherTask(String publicationChannel, NotificationService notificationService) {
        ErrorHandling.checkNull("Arguments must not be null: publicationChannel=%s, notificationService=%s", publicationChannel,
                notificationService);
        this.publicationChannel = publicationChannel;
        this.notificationService = notificationService;
    }

    @Override
    public void run() {
        try {
            notificationService.publishNotifications(publicationChannel);
        } catch (IOException e) {
            LOG.error("Unexpected problem publishing notifications", e);
        }
    }

}
