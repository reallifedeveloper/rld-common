package com.reallifedeveloper.common.application.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;

/**
 * An application service to work with {@link NotificationLog NotificationLogs}.
 *
 * @author RealLifeDeveloper
 */
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EventStore eventStore;

    @Autowired
    private PublishedMessageTrackerRepository messageTrackerRepository;

    @Autowired
    private NotificationPublisher notificationPublisher;

    /**
     * Creates a new <code>NotificationService</code> that uses the given components.
     *
     * @param eventStore an event store for finding stored domain events
     * @param messageTrackerRepository a repository for keeping track of the last notification published
     * @param notificationPublisher a publisher of notifications to external systems
     *
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    public NotificationService(EventStore eventStore, PublishedMessageTrackerRepository messageTrackerRepository,
            NotificationPublisher notificationPublisher) {
        if (eventStore == null || messageTrackerRepository == null || notificationPublisher == null) {
            throw new IllegalArgumentException("Arguments must not be null: eventStore=" + eventStore
                    + ", messageTrackerRepository=" + messageTrackerRepository
                    + ", notificationPublisher=" + notificationPublisher);
        }
        this.eventStore = eventStore;
        this.messageTrackerRepository = messageTrackerRepository;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * Used by Spring to configure the object.
     */
    NotificationService() {
        super();
    }

    /**
     * Gives a {@link NotificationLog} containing the most recent {@link Notification Notifications}.
     *
     * @param batchSize the maximum number of notifications in the notification log
     *
     * @return a notification log with the most recent notifications
     */
    @Transactional(readOnly = true)
    public NotificationLog currentNotificationLog(int batchSize) {
        LOG.trace("currentNotificationLog: batchSize={}", batchSize);
        NotificationLogId notificationLogId = calculateCurrentNotificationLogId(batchSize);
        NotificationLog notificationLog = findNotificationLog(notificationLogId);
        LOG.trace("currentNotificationLog: {}", notificationLog);
        return notificationLog;
    }

    /**
     * Gives an archived {@link NotificationLog}.
     *
     * @param notificationLogId represents the first and last {@link Notification} in the log
     *
     * @return an archived <code>NotificationLog</code>
     */
    @Transactional(readOnly = true)
    public NotificationLog notificationLog(NotificationLogId notificationLogId) {
        LOG.trace("notificationLog: notificationLogId={}", notificationLogId);
        NotificationLog notificationLog = findNotificationLog(notificationLogId);
        LOG.trace("notificationLog: {}", notificationLog);
        return notificationLog;
    }

    private NotificationLog findNotificationLog(NotificationLogId notificationLogId) {
        List<StoredEvent> storedEvents = eventStore.allEventsBetween(notificationLogId.low(), notificationLogId.high());
        long lastStoredEventId = eventStore.lastStoredEventId();
        boolean archivedIndicator = notificationLogId.high() <= lastStoredEventId;
        NotificationLogId next = notificationLogId.high() < lastStoredEventId ? notificationLogId.next() : null;
        NotificationLogId previous = notificationLogId.low() > 1 ? notificationLogId.previous() : null;
        NotificationLog notificationLog = new NotificationLog(notificationLogId, next, previous,
                notificationsFrom(storedEvents), archivedIndicator);
        return notificationLog;
    }

    private NotificationLogId calculateCurrentNotificationLogId(int batchSize) {
        long count = eventStore.lastStoredEventId();
        long remainder = count %  batchSize;
        if (remainder == 0) {
            remainder = batchSize;
        }
        long low = count - remainder + 1;
        if (low < 1) {
            low = 1;
        }
        long high = low + batchSize - 1;
        return new NotificationLogId(low, high);
    }

    private List<Notification> notificationsFrom(List<StoredEvent> storedEvents) {
        List<Notification> notifications = new ArrayList<>();
        NotificationFactory notificationFactory = NotificationFactory.instance(eventStore);
        for (StoredEvent storedEvent : storedEvents) {
            notifications.add(notificationFactory.fromStoredEvent(storedEvent));
        }
        return notifications;
    }

    /**
     * Publishes notifications about all events that have occurred since the last publication to the given
     * publication channel.
     *
     * @param publicationChannel the name of the publication channel to publish notifications on
     *
     * @throws IOException if publishing failed
     */
    @Transactional
    public void publishNotifications(String publicationChannel) throws IOException {
        LOG.trace("publishNotifications: publicationChannel={}", publicationChannel);
        PublishedMessageTracker messageTracker = messageTracker(publicationChannel);
        List<Notification> notifications = unpublishedNotifications(messageTracker.lastPublishedMessageId());
        notificationPublisher.publish(notifications, publicationChannel);
        trackLastPublishedMessage(messageTracker, notifications);
        LOG.trace("publishNotifications: done");
    }

    private PublishedMessageTracker messageTracker(String publicationChannel) {
        PublishedMessageTracker messageTracker = messageTrackerRepository.findByPublicationChannel(publicationChannel);
        if (messageTracker == null) {
            messageTracker = new PublishedMessageTracker(0, publicationChannel);
        }
        return messageTracker;
    }

    private List<Notification> unpublishedNotifications(long lastPublishedMessageId) {
        List<StoredEvent> storedEvents = eventStore.allEventsSince(lastPublishedMessageId);
        return notificationsFrom(storedEvents);
    }

    private void trackLastPublishedMessage(PublishedMessageTracker messageTracker, List<Notification> notifications) {
        if (!notifications.isEmpty()) {
            Notification lastNotification = notifications.get(notifications.size() - 1);
            messageTracker.setLastPublishedMessageid(lastNotification.storedEventId());
            messageTrackerRepository.save(messageTracker);
        }
    }
}
