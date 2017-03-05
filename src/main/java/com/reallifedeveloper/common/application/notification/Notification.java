package com.reallifedeveloper.common.application.notification;

import java.util.Date;

import com.reallifedeveloper.common.domain.event.DomainEvent;

/**
 * A notification is information about a domain event that has occurred, and that has been stored in an
 * {@link com.reallifedeveloper.common.application.eventstore.EventStore}. The notification can be sent
 * to external systems that need to be informed about the event.
 *
 * @author RealLifeDeveloper
 */
public final class Notification {

    private final String eventType;
    private final long storedEventId;
    private final Date occurredOn;
    private final DomainEvent event;

    /**
     * Creates a new <code>Notification</code> from the given {@link DomainEvent} and the given
     * <code>storedEventId</code>.
     * <p>
     * The <code>storedEventId</code> should be the ID given to the
     * {@link com.reallifedeveloper.common.application.eventstore.EventStore} when the domain event
     * was added to the {@link com.reallifedeveloper.common.application.eventstore.StoredEvent}.
     *
     * @param event the domain event that is the cause of the notification
     * @param storedEventId the id of the <code>StoredEvent</code> representing the domain event
     *
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    public Notification(DomainEvent event, Long storedEventId) {
        if (event == null || storedEventId == null) {
            throw new IllegalArgumentException("Arguments must not be null: event=" + event
                    + ", storedEventId=" + storedEventId);
        }
        this.event = event;
        this.storedEventId = storedEventId;
        this.eventType = event.getClass().getName();
        if (event.occurredOn() == null) {
            this.occurredOn = null;
        } else {
            this.occurredOn = new Date(event.occurredOn().getTime());
        }
    }

    /**
     * Gives the name of the domain event class.
     *
     * @return the name of the domain event class
     */
    public String eventType() {
        return eventType;
    }

    /**
     * Gives the ID of the {@link com.reallifedeveloper.common.application.eventstore.StoredEvent} that the
     * {@link Notification} is based on.
     *
     * @return the ID of the <code>StoredEvent</code>
     */
    public long storedEventId() {
        return storedEventId;
    }

    /**
     * Gives the date and time when the domain event occurred.
     *
     * @return the date and time the domain event occurred
     */
    public Date occurredOn() {
        if (occurredOn == null) {
            return null;
        } else {
            return new Date(occurredOn.getTime());
        }
    }

    /**
     * Gives the {@link DomainEvent} that caused this notification.
     *
     * @return the <code>DomainEvent</code> that caused this notification
     */
    public DomainEvent event() {
        return event;
    }

    @Override
    public String toString() {
        return "Notification{eventType=" + eventType + ", storedEventId=" + storedEventId + ", occurredOn="
                + occurredOn + ", event=" + event + "}";
    }

}
