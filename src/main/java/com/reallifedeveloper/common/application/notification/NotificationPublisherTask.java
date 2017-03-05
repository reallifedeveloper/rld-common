package com.reallifedeveloper.common.application.notification;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A <code>java.lang.Runnable</code> that calls {@link NotificationService#publishNotifications(String)}.
 * It can be used for scheduling regular publication using a timer service.
 *
 * @author RealLifeDeveloper
 */
public class NotificationPublisherTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPublisherTask.class);

    private final String publicationChannel;

    @Autowired
    private NotificationService notificationService;

    /**
     * Creates a new <code>NotificationPublishingTimerTask</code> that publishes to the given publication
     * channel using the given {@link NotificationService}.
     *
     * @param publicationChannel the name of the channel to which to publish
     * @param notificationService the <code>NotificationService</code> to use to do the actual publishing
     */
    public NotificationPublisherTask(String publicationChannel, NotificationService notificationService) {
        if (publicationChannel == null || notificationService == null) {
            throw new IllegalArgumentException("Arguments must not be null: publicationChannel=" + publicationChannel
                    + ", notificationService=" + notificationService);
        }
        this.publicationChannel = publicationChannel;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new <code>NotificationPublishingTimerTask</code> that publishes to the given publication
     * channel. The {@link NotificationService} to use is assumed to be injected by Spring.
     *
     * @param publicationChannel the name of the channel to which to publish
     */
    public NotificationPublisherTask(String publicationChannel) {
        if (publicationChannel == null) {
            throw new IllegalArgumentException("publicationChannel must not be null");
        }
        this.publicationChannel = publicationChannel;
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
