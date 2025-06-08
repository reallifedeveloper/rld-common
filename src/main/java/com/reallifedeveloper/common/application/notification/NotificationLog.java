package com.reallifedeveloper.common.application.notification;

import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * A notification log holds a list of {@link Notification Notifications}, i.e., information about domain events that have occurred and have
 * been stored in an {@link com.reallifedeveloper.common.application.eventstore.EventStore}.
 * <p>
 * A notification log may be <i>archived</i>, which means that it will never change. If a log is archived, it means that the ID of the log
 * will always refer to the current list of {@link Notification Notifications}. If it is not archived, the only change that may occur is
 * that more notifications are added to it; the notifications themselves represent historical events that have already occurred, so they are
 * immutable.
 *
 * @param current          the ID of the current notification log, must not be {@code null}
 * @param nullableNext     the ID of the next notification log, may be {@code null}
 * @param nullablePrevious the ID of the previous notification log, may be {@code null}
 * @param notifications    the list of notifications in this notification log, must not be {@code null}
 * @param isArchived       if {@code true}, this notification log is archived, i.e., it will never change
 *
 * @author RealLifeDeveloper
 */
public record NotificationLog(NotificationLogId current, @Nullable NotificationLogId nullableNext,
        @Nullable NotificationLogId nullablePrevious, List<Notification> notifications, boolean isArchived) {

    /**
     * Creates a new {@code NotificationLog}.
     */
    public NotificationLog {
        ErrorHandling.checkNull("Arguments must not be null: current=%s, notifications=%s", current, notifications);
        notifications = List.copyOf(notifications);
    }

    /**
     * Gives the ID of the next notification log as a {@code java.util.Optional}.
     *
     * @return the ID of the next notification log
     */
    public Optional<NotificationLogId> next() {
        return Optional.ofNullable(nullableNext);
    }

    /**
     * Gives the ID of the previous notification log as a {@code java.util.Optional}.
     *
     * @return the ID of the previous notification log
     */
    public Optional<NotificationLogId> previous() {
        return Optional.ofNullable(nullablePrevious);
    }
}
