package com.reallifedeveloper.common.resource.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationLog;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * A REST-ful representation of a {@link NotificationLog}, containing links to resources to get related representations.
 *
 * @author RealLifeDeveloper
 */
@XmlRootElement(name = "NotificationLog")
@XmlAccessorType(XmlAccessType.FIELD)
public final class NotificationLogRepresentation {

    @XmlElement(name = "next")
    private @Nullable String next;

    @XmlElement(name = "self")
    private @Nullable String self;

    @XmlElement(name = "previous")
    private @Nullable String previous;

    @XmlElement(name = "isArchived")
    private boolean isArchived;

    @XmlElementWrapper(name = "notifications")
    @XmlElement(name = "notification")
    private final List<NotificationRepresentation> notifications = new ArrayList<>();

    /**
     * Creates a new {@code NotificationLogRepresentation} representing the given {@link NotificationLog}, and using the given
     * {@link ObjectSerializer} to serialize the domain events.
     *
     * @param notificationLog  the notification log to represent
     * @param objectSerializer the object serializer to use to serialize domain events
     */
    public NotificationLogRepresentation(NotificationLog notificationLog, ObjectSerializer<String> objectSerializer) {
        ErrorHandling.checkNull("Arguments must not be null: notificationLog=%s, objectSerializer=%s", notificationLog, objectSerializer);
        for (Notification notification : notificationLog.notifications()) {
            notifications.add(new NotificationRepresentation(notification, objectSerializer));
        }
        this.isArchived = notificationLog.isArchived();
    }

    /**
     * Required by JAXB.
     */
    /* package-private */ NotificationLogRepresentation() {
        super();
    }

    /**
     * Gives a canonical link to the current set of notifications.
     *
     * @return a canonical link to the current set of notifications
     */
    public @Nullable String getSelf() {
        return self;
    }

    /**
     * Sets the canonical link to the current set of notifications.
     *
     * @param self a canonical link to the current set of notifications
     */
    public void setSelf(String self) {
        this.self = self;
    }

    /**
     * Gives a canonical link to the next set of notifications.
     *
     * @return a canonical link to the next set of notifications
     */
    public @Nullable String getNext() {
        return next;
    }

    /**
     * Sets the canonical link to the next set of notifications.
     *
     * @param next a canonical link to the next set of notifications
     */
    public void setNext(String next) {
        this.next = next;
    }

    /**
     * Gives a canonical link to the previous set of notifications.
     *
     * @return a canonical link to the previous set of notifications
     */
    public @Nullable String getPrevious() {
        return previous;
    }

    /**
     * Sets the canonical link to the previous set of notifications.
     *
     * @param previous a canonical link to the previous set of notifications
     */
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    /**
     * Gives the notifications represented.
     *
     * @return the notifications represented
     */
    public List<NotificationRepresentation> notifications() {
        return Collections.unmodifiableList(notifications);
    }

    /**
     * Shows if this represents an archived notification log or not.
     *
     * @return {@code true} if this represents an archived notification log, {@code false} otherwise
     */
    public boolean isArchived() {
        return isArchived;
    }

}
