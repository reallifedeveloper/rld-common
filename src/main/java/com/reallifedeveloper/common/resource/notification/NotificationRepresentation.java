package com.reallifedeveloper.common.resource.notification;

import java.time.ZonedDateTime;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * A REST-ful representation of a {@link Notification}.
 *
 * @author RealLifeDeveloper
 */
@XmlRootElement(name = "Notification")
@XmlAccessorType(XmlAccessType.FIELD)
public final class NotificationRepresentation {

    @XmlElement(name = "eventType")
    private @Nullable String eventType;

    @XmlElement(name = "storedEventId")
    private long storedEventId;

    @XmlElement(name = "occurredOn")
    private @Nullable ZonedDateTime occurredOn;

    @XmlElement(name = "event")
    @JsonRawValue
    private @Nullable String event;

    /**
     * Creates a new {@code NotificationRepresentation} representing the given {@link Notification}, and using the given
     * {@link ObjectSerializer} to serialize the domain event.
     *
     * @param notification     the notification to represent
     * @param objectSerializer the object serializer to use to serialize the domain event
     */
    public NotificationRepresentation(Notification notification, ObjectSerializer<String> objectSerializer) {
        ErrorHandling.checkNull("Arguments must not be null: notification=%s, objectSerializer=%s", notification, objectSerializer);
        this.eventType = notification.eventType();
        this.storedEventId = notification.storedEventId();
        this.occurredOn = notification.occurredOn();
        this.event = objectSerializer.serialize(notification.event());
    }

    /**
     * Required by JAXB.
     */
    /* package-private */ NotificationRepresentation() {
        super();
    }

    /**
     * Gives the name of the domain event class.
     *
     * @return the name of the domain event class
     */
    public @Nullable String getEventType() {
        return eventType;
    }

    /**
     * Gives the ID of the stored event represented by the notification.
     *
     * @return the ID of the stored event represented by the notification
     */
    public long getStoredEventId() {
        return storedEventId;
    }

    /**
     * Gives the date and time the domain event occurred.
     *
     * @return the date and time the domain event occurred
     */
    public @Nullable ZonedDateTime getOccurredOn() {
        return occurredOn;
    }

    /**
     * Gives the serialized form of the domain event.
     * <p>
     * Note that this form depends only on which {@link ObjectSerializer} is being used, and not on how this
     * {@code NotificationRepresentation} is serialized. For example, if a JSON object serializer is used, the event will be serialized as a
     * JSON string, even if the representation is serialized as XML.
     *
     * @return the serialized form of the domain event
     */
    public @Nullable String getEvent() {
        return event;
    }

}
