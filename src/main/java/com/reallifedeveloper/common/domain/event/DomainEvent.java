package com.reallifedeveloper.common.domain.event;

import java.io.Serializable;
import java.util.Date;

import com.reallifedeveloper.common.domain.DomainObject;

/**
 * A domain-driven design domain event, i.e., something that happened that domain experts care about.
 *
 * @author RealLifeDeveloper
 */
public interface DomainEvent extends DomainObject<DomainEvent>, Serializable {

    /**
     * Gives the time the event occurred.
     *
     * @return the time the event occurred
     */
    Date occurredOn();

    /**
     * Gives the version of this event. This is useful when deserializing an event.
     * <p>
     * The version should start at 1 and be incremented each time the event class is updated in a way
     * that affects serialization/deserialization.
     *
     * @return the version of this event
     */
    int version();

}
