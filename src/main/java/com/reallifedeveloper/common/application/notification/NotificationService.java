package com.reallifedeveloper.common.application.notification;

import static com.reallifedeveloper.common.domain.LogUtil.removeCRLF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * An application service to work with {@link NotificationLog NotificationLogs}.
 *
 * @author RealLifeDeveloper
 */
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final EventStore eventStore;

    private final PublishedMessageTrackerRepository messageTrackerRepository;

    private final NotificationPublisher notificationPublisher;

    /**
     * Creates a new {@code NotificationService} that uses the given components.
     *
     * @param eventStore               an event store for finding stored domain events
     * @param messageTrackerRepository a repository for keeping track of the last notification published
     * @param notificationPublisher    a publisher of notifications to external systems
     *
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public NotificationService(EventStore eventStore, PublishedMessageTrackerRepository messageTrackerRepository,
            NotificationPublisher notificationPublisher) {
        ErrorHandling.checkNull("Arguments must not be null: eventStore=%s, messageTrackerRepository=%s, notificationPublisher=%s",
                eventStore, messageTrackerRepository, notificationPublisher);
        this.eventStore = eventStore;
        this.messageTrackerRepository = messageTrackerRepository;
        this.notificationPublisher = notificationPublisher;
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
     * @return an archived {@code NotificationLog}
     */
    @Transactional(readOnly = true)
    @SuppressFBWarnings(value = "CRLF_INJECTION_LOGS", justification = "Logging only of objects, not user data")
    public NotificationLog notificationLog(NotificationLogId notificationLogId) {
        LOG.trace("notificationLog: notificationLogId={}", notificationLogId);
        ErrorHandling.checkNull("notificationLogId must not be null", notificationLogId);
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
        return new NotificationLog(notificationLogId, next, previous, notificationsFrom(storedEvents), archivedIndicator);
    }

    private NotificationLogId calculateCurrentNotificationLogId(int batchSize) {
        long count = eventStore.lastStoredEventId();
        long remainder = count % batchSize;
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
     * Publishes notifications about all events that have occurred since the last publication to the given publication channel.
     *
     * @param publicationChannel the name of the publication channel to publish notifications on
     *
     * @throws IOException if publishing failed
     */
    @Transactional
    public void publishNotifications(String publicationChannel) throws IOException {
        LOG.trace("publishNotifications: publicationChannel={}", removeCRLF(publicationChannel));
        PublishedMessageTracker messageTracker = messageTracker(publicationChannel);
        List<Notification> notifications = unpublishedNotifications(messageTracker.lastPublishedMessageId());
        notificationPublisher.publish(notifications, publicationChannel);
        trackLastPublishedMessage(messageTracker, notifications);
        LOG.trace("publishNotifications: done");
    }

    private PublishedMessageTracker messageTracker(String publicationChannel) {
        return messageTrackerRepository.findByPublicationChannel(publicationChannel)
                .orElseGet(() -> new PublishedMessageTracker(0, publicationChannel));
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

    /**
     * Make finalize method final to avoid "Finalizer attacks" and corresponding SpotBugs warning (CT_CONSTRUCTOR_THROW).
     *
     * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/OBJ11-J.+Be+wary+of+letting+constructors+throw+exceptions">
     *      Explanation of finalizer attack</a>
     */
    @Override
    @Deprecated
    @SuppressWarnings({ "checkstyle:NoFinalizer", "PMD.EmptyFinalizer" })
    protected final void finalize() throws Throwable {
        // Do nothing
    }

}
