package com.reallifedeveloper.common.application.eventstore;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * A representation of a {@link com.reallifedeveloper.common.domain.event.DomainEvent} that can be stored in a database.
 *
 * @author RealLifeDeveloper
 */
@Entity
@Table(name = "stored_event")
public class StoredEvent {

    private static final int MAX_EVENT_BODY_LENGTH = 8000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stored_event_id")
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_body", length = MAX_EVENT_BODY_LENGTH, nullable = false)
    private String eventBody;

    @Column(name = "occurred_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime occurredOn;

    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * Creates a new {@code StoredEvent} with the given attributes.
     *
     * @param eventType  the class name of the domain event, as given by {@code event.getClass().getName()}
     * @param eventBody  a string representation of the domain event
     * @param occurredOn the date and time the domain event occurred
     * @param version    the version of the domain event
     *
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public StoredEvent(String eventType, String eventBody, ZonedDateTime occurredOn, int version) {
        ErrorHandling.checkNull("Arguments must not be null: eventType=%s, eventBody=%s, occurredOn=%s, version=" + version, eventType,
                eventBody, occurredOn);
        this.eventType = eventType;
        this.eventBody = eventBody;
        this.occurredOn = occurredOn;
        this.version = version;
    }

    /* package-private */ StoredEvent() {
        // Required by Hibernate
    }

    /**
     * Gives the ID of this {@code StoredEvent}. Note that this is not the same as the ID of the domain event itself.
     *
     * @return the ID of this {@code StoredEvent}
     */
    public Long id() {
        return id;
    }

    /**
     * Gives the class name of the domain event represented by this {@code StoredEvent}.
     *
     * @return the class name of the domain event
     */
    public String eventType() {
        return eventType;
    }

    /**
     * Gives the string representation of the domain event represented by this {@code StoredEvent}.
     *
     * @return a string representation of the domain event
     */
    public String eventBody() {
        return eventBody;
    }

    /**
     * Gives the date and time the domain event represented by this {@code StoredEvent} occurred.
     *
     * @return the date and time the domain event occurred
     */
    public ZonedDateTime occurredOn() {
        return occurredOn;
    }

    /**
     * Gives the version of the domain event represented by this {@code StoredEvent}.
     *
     * @return the version of the domain event
     */
    public Integer version() {
        return version;
    }

    @Override
    public String toString() {
        return "StoredEvent{id=" + id() + ", eventType=" + eventType() + ", eventBody=" + eventBody() + ", occurredOn=" + occurredOn()
                + ", version=" + version() + "}";
    }

}
