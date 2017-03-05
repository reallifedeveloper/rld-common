package com.reallifedeveloper.common.application.notification;

import java.util.List;

/**
 * A notification log holds a list of {@link Notification Notifications}, i.e., information about domain events
 * that have occurred and have been stored in an {@link com.reallifedeveloper.common.application.eventstore.EventStore}.
 * <p>
 * A notification log may be <i>archived</i>, which means that it will never change. If a log is archived,
 * it means that the ID of the log will always refer to the current list of {@link Notification Notifications}.
 * If it is not archived, the only change that may occur is that more notifications are added to it; the notifications
 * themselves represent historical events that have already occurred, so they are immutable.
 *
 * @author RealLifeDeveloper
 */
public final class NotificationLog {

    private final NotificationLogId currentNotificationLogId;
    private final NotificationLogId nextNotificationLogId;
    private final NotificationLogId previousNotificationLogId;
    private final List<Notification> notifications;
    private final boolean isArchived;

    /**
     * Creates a new <code>NotificationLog</code> with the given attributes.
     *
     * @param currentNotificationLogId the ID of this <code>NotificationLog</code>
     * @param nextNotificationLogId the ID of the next batch of notifications after the ones in this
     * <code>NotificationLog</code>, or <code>null</code> if this is the last
     * @param previousNotificationLogId the ID of the previous batch of notifications before the ones in this
     * <code>NotificationLog</code>, or <code>null</code> if this is the first
     * @param notifications the list of <code>Notifications</code> in this log
     * @param isArchived if <code>true</code> the log is frozen for changes
     *
     * @throws IllegalArgumentException if <code>currentNotificationLogId</code> or
     * <code>notifications</code> is <code>null</code>
     */
    public NotificationLog(NotificationLogId currentNotificationLogId, NotificationLogId nextNotificationLogId,
            NotificationLogId previousNotificationLogId, List<Notification> notifications, boolean isArchived) {
        if (currentNotificationLogId == null || notifications == null) {
            throw new IllegalArgumentException("Arguments must not be null: "
                    + "currentNotificationLogId=" + currentNotificationLogId
                    + ", nextNotificationLogId=" + nextNotificationLogId
                    + ", previousNotificationLogId=" + previousNotificationLogId
                    + ", notifications=" + notifications
                    + ", isArchived=" + isArchived);
        }
        this.currentNotificationLogId = currentNotificationLogId;
        this.nextNotificationLogId = nextNotificationLogId;
        this.previousNotificationLogId = previousNotificationLogId;
        this.notifications = notifications;
        this.isArchived = isArchived;
    }

    /**
     * Gives the ID of this <code>NotificationLog</code>.
     *
     * @return the ID of this <code>NotificationLog</code>
     */
    public NotificationLogId currentNotificationLogId() {
        return currentNotificationLogId;
    }

    /**
     * Gives the ID of the next batch of notifications after the ones in this <code>NotificationLog</code>.
     *
     * @return the ID of the next batch of notifications
     */
    public NotificationLogId nextNotificationLogId() {
        return nextNotificationLogId;
    }

    /**
     * Gives the ID of the previous batch of notifications before the ones in this <code>NotificationLog</code>.
     *
     * @return the ID of the previous batch of notifications
     */
    public NotificationLogId previousNotificationLogId() {
        return previousNotificationLogId;
    }

    /**
     * Gives the list of {@link Notification Notifications} in this this <code>NotificationLog</code>.
     *
     * @return the list of <code>Notifications</code>
     */
    public List<Notification> notifications() {
        return notifications;
    }

    /**
     * Shows if this <code>NotificationLog</code> is archived or not. If the log is archived, the
     * {@link #currentNotificationLogId()} always represents the current list of
     * {@link Notification Notifications}. If it is not archived, more {@link Notification Notifications}
     * may be added as additional domain event occur.
     *
     * @return <code>true</code> if this notification log is archived, <code>false</code> otherwise
     */
    public boolean isArchived() {
        return isArchived;
    }

    @Override
    public String toString() {
        return "NotificationLog{current=" + currentNotificationLogId()
                + ", next=" + nextNotificationLogId()
                + ", previous=" + previousNotificationLogId()
                + ", notifications=" + notifications
                + "}";
    }

}
