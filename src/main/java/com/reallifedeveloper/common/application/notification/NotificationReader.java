package com.reallifedeveloper.common.application.notification;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * A reader of serialized {@link Notification Notifications}, that lets the user read information from the notification without having to
 * deserialize it to a Java object. This means that the notification can be used without having access to the class files for the domain
 * events in question.
 * <p>
 * This interface provides a number of methods to access fields in the the domain event that caused the notification to occur, e.g.,
 * {@link #eventIntValue(String)}. These methods take the field name as an argument. The field name can be simple, e.g., "foo", or nested,
 * e.g., "foo.bar". A nested field name refers to a similarly nested object in the domain event.
 * <p>
 * For example, if the method {@link #eventIntValue(String)} is called with the field name "foo.bar", the domain event should contain an
 * object named "foo" which contains an integer field named "bar".
 *
 * @author RealLifeDeveloper
 */
public interface NotificationReader {

    /**
     * Gives the name of the domain event class.
     *
     * @return the name of the domain event class
     */
    String eventType();

    /**
     * Gives the ID of the {@link com.reallifedeveloper.common.application.eventstore.StoredEvent} that the {@link Notification} is based
     * on.
     *
     * @return the ID of the {@code StoredEvent}
     */
    Long storedEventId();

    /**
     * Gives the date and time when the domain event occurred.
     *
     * @return the date and time the domain event occurred
     */
    ZonedDateTime occurredOn();

    /**
     * Gives the version of the domain event.
     *
     * @return the version of the domain event
     */
    Integer eventVersion();

    /**
     * Gives the integer value of a field in the domain event.
     *
     * @param fieldName the name of the field to lookup, potentially nested, e.g., "foo.bar"
     *
     * @return the optoinl integer value of the field
     */
    Optional<Integer> eventIntValue(String fieldName);

    /**
     * Gives the long integer value of a field in the domain event.
     *
     * @param fieldName the name of the field to lookup, potentially nested, e.g., "foo.bar"
     *
     * @return the optional long integer value of the field
     */
    Optional<Long> eventLongValue(String fieldName);

    /**
     * Gives the double value of a field in the domain event.
     *
     * @param fieldName the name of the field to lookup, potentially nested, e.g., "foo.bar"
     *
     * @return the optional double value of the field
     */
    Optional<Double> eventDoubleValue(String fieldName);

    /**
     * Gives the string value of a field in the domain event.
     *
     * @param fieldName the name of the field to lookup, potentially nested, e.g., "foo.bar"
     *
     * @return the optional string value of the field
     */
    Optional<String> eventStringValue(String fieldName);

    /**
     * Gives the value of a field in the domain event as a {@code java.time.ZonedDateTime} object.
     *
     * @param fieldName the name of the field to lookup, potentially nested, e.g., "foo.bar"
     *
     * @return the optional {@code java.time.ZonedDateTime} value of the field
     */
    Optional<ZonedDateTime> zonedDateTimeValue(String fieldName);

}
