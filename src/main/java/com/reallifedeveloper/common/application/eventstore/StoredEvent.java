package com.reallifedeveloper.common.application.eventstore;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A representation of a {@link com.reallifedeveloper.common.domain.event.DomainEvent} that can be
 * stored in a database.
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
    private Date occurredOn;

    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * Creates a new <code>StoredEvent</code> with the given attributes.
     *
     * @param eventType the class name of the domain event, as given by <code>event.getClass().getName()</code>
     * @param eventBody a string representation of the domain event
     * @param occurredOn the date and time the domain event occurred
     * @param version the version of the domain event
     *
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    public StoredEvent(String eventType, String eventBody, Date occurredOn, int version) {
        if (eventType == null || eventBody == null || occurredOn == null) {
            throw new IllegalArgumentException("Arguments must not be null: eventType=" + eventType
                    + ", eventBody=" + eventBody + ", occurredOn=" + occurredOn + ", version=" + version);
        }
        this.eventType = eventType;
        this.eventBody = eventBody;
        this.occurredOn = new Date(occurredOn.getTime());
        this.version = version;
    }

    StoredEvent() {
        // Required by Hibernate
    }

    /**
     * Gives the ID of this <code>StoredEvent</code>. Note that this is not the same as the
     * ID of the domain event itself.
     *
     * @return the ID of this <code>StoredEvent</code>
     */
    public Long id() {
        return id;
    }

    /**
     * Gives the class name of the domain event represented by this <code>StoredEvent</code>.
     *
     * @return the class name of the domain event
     */
    public String eventType() {
        return eventType;
    }

    /**
     * Gives the string representation of the domain event represented by this <code>StoredEvent</code>.
     *
     * @return a string representation of the domain event
     */
    public String eventBody() {
        return eventBody;
    }

    /**
     * Gives the date and time the domain event represented by this <code>StoredEvent</code> occurred.
     *
     * @return the date and time the domain event occurred
     */
    public Date occurredOn() {
        return new Date(occurredOn.getTime());
    }

    /**
     * Gives the version of the domain event represented by this <code>StoredEvent</code>.
     *
     * @return the version of the domain event
     */
    public Integer version() {
        return version;
    }

    @Override
    public String toString() {
        return "StoredEvent{id=" + id()
                + ", eventType=" + eventType()
                + ", eventBody=" + eventBody()
                + ", occurredOn=" + occurredOn()
                + ", version=" + version()
                + "}";
    }

}
