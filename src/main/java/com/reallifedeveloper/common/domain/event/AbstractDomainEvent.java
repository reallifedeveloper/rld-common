package com.reallifedeveloper.common.domain.event;

import java.util.Date;

import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;

/**
 * An abstract base class for domain events.
 *
 * @author RealLifeDeveloper
 */
public abstract class AbstractDomainEvent implements DomainEvent {

    private static final long serialVersionUID = 1L;

    private Date occurredOn;
    private int version;

    /**
     * Creates a new <code>AbstractDomainEvent</code> that occurred now and has a version of 1.
     * <p>
     * The time of occurrence is taken from calling the
     * {@link com.reallifedeveloper.common.domain.TimeService#now()} method on the
     * {@link CommonDomainRegistry#timeService()}.
     */
    public AbstractDomainEvent() {
        this(CommonDomainRegistry.timeService().now(), 1);
    }

    /**
     * Creates a new <code>AbstractDomainEvent</code> that occurred now and has the given version.
     * <p>
     * The time of occurrence is taken from calling the
     * {@link com.reallifedeveloper.common.domain.TimeService#now()} method on the
     * {@link CommonDomainRegistry#timeService()}.
     *
     * @param version the version of the event
     */
    public AbstractDomainEvent(int version) {
        this(CommonDomainRegistry.timeService().now(), version);
    }

    /**
     * Creates a new <code>AbstractDomainEvent</code> that occurred at the given time and has a version of 1.
     *
     * @param occurredOn the date and time the event occurred
     */
    public AbstractDomainEvent(Date occurredOn) {
        this(occurredOn, 1);
    }

    /**
     * Creates a new <code>AbstractDomainEvent</code> that occurred at the given time and has the given version.
     *
     * @param occurredOn the time the event occurred
     * @param version the version of the event
     */
    public AbstractDomainEvent(Date occurredOn, int version) {
        if (occurredOn == null) {
            throw new IllegalArgumentException("occurredOn must not be null");
        }
        this.occurredOn = new Date(occurredOn.getTime());
        this.version = version;
    }

    @Override
    public Date occurredOn() {
        return new Date(occurredOn.getTime());
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{occurredOn=" + occurredOn() + ", version=" + version() + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + occurredOn.hashCode();
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DomainEvent other = (DomainEvent) obj;
        if (!occurredOn.equals(other.occurredOn())) {
            return false;
        }
        if (version != other.version()) {
            return false;
        }
        return true;
    }
}
