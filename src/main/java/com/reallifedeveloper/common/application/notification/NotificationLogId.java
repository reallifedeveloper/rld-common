package com.reallifedeveloper.common.application.notification;

/**
 * The ID of a {@link NotificationLog}, representing an interval of {@link Notification Notifications} that can be held in the log.
 * <p>
 * The low and high properties of the {@code NotificationLogId} refer to the IDs of the
 * {@link com.reallifedeveloper.common.application.eventstore.StoredEvent StoredEvents} represented by the first and last
 * {@link Notification Notifications}, respectively.
 * <p>
 * For a {@link NotificationLog} that has been archived, the low and high attributes of the ID exactly match the stored event IDs of the
 * first and last notifications. If the log has not yet been archived, it is not yet "full", so the high attribute may be greater than the
 * stored event ID of the last notification.
 * <p>
 * For example, an unarchived notification log which can hold 20 notifications may contain only 5 notifications, with stored event IDs 80
 * through 84. In this case, the {@code NotificationLogId} would have a low property of 80 and a high property of 99.
 *
 * @author RealLifeDeveloper
 */
public final class NotificationLogId {

    private final long low;
    private final long high;

    /**
     * Creates a new {@code NotificationLogId} with the given low and high properties.
     *
     * @param low  the stored event ID of the first {@link Notification}
     * @param high the stored event ID of the last {@link Notification}
     *
     * @throws IllegalArgumentException if {@code low} is greater than {@code high}
     */
    public NotificationLogId(long low, long high) {
        if (low > high) {
            throw new IllegalArgumentException("low must not be greater than high: low=" + low + ", high=" + high);
        }
        this.low = low;
        this.high = high;
    }

    /**
     * Creates a new {@code NotificationLogId} with the low and high properties parsed from a comma-separated string.
     *
     * @param notificationLogId a string on the form "&lt;low&gt;,&lt;high&gt;"
     *
     * @throws IllegalArgumentException if the string is {@code null} or has the wrong form
     */
    public NotificationLogId(String notificationLogId) {
        if (notificationLogId == null) {
            throw new IllegalArgumentException("notificationLogId must not be null");
        }
        String[] textIds = notificationLogId.split(",");
        if (textIds.length != 2) {
            throw new IllegalArgumentException("notificationLogId should be on the form '<low>,<high>'"
                    + ", where <low> and <high> are integers: notificationLogId=" + notificationLogId);
        }
        try {
            this.low = Long.parseLong(textIds[0]);
            this.high = Long.parseLong(textIds[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("notificationLogId should be on the form '<low>,<high>'"
                    + ", where <low> and <high> are integers: notificationLogId=" + notificationLogId, e);
        }
        if (low() > high()) {
            throw new IllegalArgumentException("low must not be greater than high: low=" + low() + ", high=" + high());
        }
    }

    /**
     * Gives a standard string representation of this {@code NotificationLogId}, on the form "&lt;low&gt;,&lt;high&gt;".
     *
     * @return a standard string representation of this {@code NotificationLogId}
     */
    public String externalForm() {
        return Long.toString(low()) + "," + Long.toString(high());
    }

    /**
     * Gives the low property of this {@code NotificationLogId}, i.e., the stored event ID of the first notification that may be stored in a
     * {@link NotificationLog}.
     *
     * @return the low property of this {@code NotificationLogId}
     */
    public long low() {
        return low;
    }

    /**
     * Gives the high property of this {@code NotificationLogId}, i.e., the stored event ID of the last notification that may be stored in a
     * {@link NotificationLog}.
     *
     * @return the high property of this {@code NotificationLogId}
     */
    public long high() {
        return high;
    }

    /**
     * Gives a {@code NotificationLogId} representing the next batch of notifications, after the ones in the current
     * {@link NotificationLog}.
     *
     * @return a {@code NotificationLogId} representing the next batch of notifications
     */
    public NotificationLogId next() {
        return new NotificationLogId(low() + batchSize(), high() + batchSize());
    }

    /**
     * Gives a {@code NotificationLogId} representing the previous batch of notifications, before the ones in the current
     * {@link NotificationLog}.
     *
     * @return a {@code NotificationLogId} representing the previous batch of notifications
     */
    public NotificationLogId previous() {
        return new NotificationLogId(low() - batchSize(), high() - batchSize());
    }

    /**
     * Gives the batch size of the {@link NotificationLog} with this {@code NotificationLogId}, i.e., the number of {@link Notification
     * Notifications} it can hold.
     *
     * @return the batch size
     */
    public int batchSize() {
        return (int) (high() - low() + 1);
    }

    @Override
    public String toString() {
        return externalForm();
    }

}
