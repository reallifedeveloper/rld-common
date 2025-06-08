package com.reallifedeveloper.common.application.notification;

import java.time.ZonedDateTime;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * A notification is information about a domain event that has occurred, and that has been stored in an
 * {@link com.reallifedeveloper.common.application.eventstore.EventStore}. The notification can be sent to external systems that need to be
 * informed about the event.
 *
 * @param eventType     the type of the event
 * @param storedEventId the stored event ID, must not be {@code null}
 * @param occurredOn    the timestamp the event occurred
 * @param event         the domain event that occurred, must not be {@code null}
 *
 * @author RealLifeDeveloper
 */
public record Notification(@Nullable String eventType, Long storedEventId, @Nullable ZonedDateTime occurredOn, DomainEvent event) {

    /**
     * Creates a new {@code Notification}.
     *
     * @throws IllegalArgumentException if {@code event} or {@code storedEventId} is {@code null}
     */
    public Notification {
        ErrorHandling.checkNull("Arguments must not be null: event=%s, storedEventId=%s", event, storedEventId);
    }

    /**
     * A factory method that sets the {@code eventType} to the name of the event class and {@code occurredOn} to the date and time when the
     * event occurred.
     *
     * @param event         the event to store and use to set {@code eventType} and {@code occurredOn}
     * @param storedEventId the stored event ID to store
     *
     * @return a new {@code Notification}
     *
     * @throws IllegalArgumentException if {@code event} or {@code storedEventId} is {@code null}
     */
    public static Notification create(DomainEvent event, Long storedEventId) {
        ErrorHandling.checkNull("Arguments must not be null: event=%s, storedEventId=%s", event, storedEventId);
        return new Notification(event.getClass().getName(), storedEventId, event.eventOccurredOn(), event);
    }

}
