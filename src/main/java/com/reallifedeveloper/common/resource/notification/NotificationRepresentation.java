package com.reallifedeveloper.common.resource.notification;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonRawValue;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * A REST-ful representation of a {@link Notification}.
 *
 * @author RealLifeDeveloper
 */
@XmlRootElement(name = "Notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationRepresentation {

    @XmlElement(name = "eventType")
    private String eventType;

    @XmlElement(name = "storedEventId")
    private long storedEventId;

    @XmlElement(name = "occurredOn")
    private Date occurredOn;

    @XmlElement(name = "event")
    @JsonRawValue
    private String event;

    /**
     * Creates a new <code>NotificationRepresentation</code> representing the given {@link Notification},
     * and using the given {@link ObjectSerializer} to serialize the domain event.
     *
     * @param notification the notification to represent
     * @param objectSerializer the object serializer to use to serialize the domain event
     */
    public NotificationRepresentation(Notification notification, ObjectSerializer<String> objectSerializer) {
        if (notification == null || objectSerializer == null) {
            throw new IllegalArgumentException("Arguments must not be null: notification=" + notification
                    + ", objectSerializer=" + objectSerializer);
        }
        this.eventType = notification.eventType();
        this.storedEventId = notification.storedEventId();
        this.occurredOn = notification.occurredOn();
        this.event = objectSerializer.serialize(notification.event());
    }

    /**
     * Required by JAXB.
     */
    NotificationRepresentation() {
        super();
    }

    /**
     * Gives the name of the domain event class.
     *
     * @return the name of the domain event class
     */
    public String getEventType() {
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
    public Date getOccurredOn() {
        if (occurredOn == null) {
            return null;
        } else {
            return new Date(occurredOn.getTime());
        }
    }

    /**
     * Gives the serialized form of the domain event.
     * <p>
     * Note that this form depends only on which {@link ObjectSerializer} is being used, and not on how
     * this <code>NotificationRepresentation</code> is serialized. For example, if a JSON object serializer
     * is used, the event will be serialized as a JSON string, even if the representation is serialized
     * as XML.
     *
     * @return the serialized form of the domain event
     */
    public String getEvent() {
        return event;
    }

}
