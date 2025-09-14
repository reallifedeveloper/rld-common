package com.reallifedeveloper.common.domain.event;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.registry.CommonDomainRegistry;

/**
 * An abstract base class for domain events.
 *
 * @author RealLifeDeveloper
 */
public abstract class AbstractDomainEvent implements DomainEvent {

    private static final long serialVersionUID = 1L;

    /**
     * The timestamp when this event occurred.
     */
    private final ZonedDateTime eventOccurredOn;

    /**
     * The version of this event. In a long-lived system, it may be necessary to work with old versions of domaim events and keeping track
     * of the version makes this easier.
     */
    private final int eventVersion;

    /**
     * Creates a new {@code AbstractDomainEvent} that occurred now and has a version of 1.
     * <p>
     * The time of occurrence is taken from calling the {@link com.reallifedeveloper.common.domain.TimeService#now()} method on the
     * {@link CommonDomainRegistry#timeService()}.
     */
    public AbstractDomainEvent() {
        this(CommonDomainRegistry.timeService().now(), 1);
    }

    /**
     * Creates a new {@code AbstractDomainEvent} that occurred now and has the given version.
     * <p>
     * The time of occurrence is taken from calling the {@link com.reallifedeveloper.common.domain.TimeService#now()} method on the
     * {@link CommonDomainRegistry#timeService()}.
     *
     * @param eventVersion the version of the event
     */
    public AbstractDomainEvent(int eventVersion) {
        this(CommonDomainRegistry.timeService().now(), eventVersion);
    }

    /**
     * Creates a new {@code AbstractDomainEvent} that occurred at the given time and has a version of 1.
     *
     * @param eventOccurredOn the date and time the event occurred
     */
    public AbstractDomainEvent(ZonedDateTime eventOccurredOn) {
        this(eventOccurredOn, 1);
    }

    /**
     * Creates a new {@code AbstractDomainEvent} that occurred at the given time and has the given version.
     *
     * @param eventOccurredOn the time the event occurred
     * @param eventVersion    the version of the event
     */
    public AbstractDomainEvent(ZonedDateTime eventOccurredOn, int eventVersion) {
        ErrorHandling.checkNull("eventOccurredOn must not be null", eventOccurredOn);
        this.eventOccurredOn = eventOccurredOn;
        this.eventVersion = eventVersion;
    }

    @Override
    public ZonedDateTime eventOccurredOn() {
        return eventOccurredOn;
    }

    @Override
    public int eventVersion() {
        return eventVersion;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{eventOccurredOn=" + eventOccurredOn() + ", eventVersion=" + eventVersion() + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventOccurredOn, eventVersion);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractDomainEvent other) {
            return Objects.equals(eventOccurredOn, other.eventOccurredOn) && eventVersion == other.eventVersion;
        } else {
            return false;
        }
    }

    /**
     * Make finalize method final to avoid "Finalizer attacks" and corresponding SpotBugs warning (CT_CONSTRUCTOR_THROW).
     *
     * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/OBJ11-J.+Be+wary+of+letting+constructors+throw+exceptions">
     *      Explanation of finalizer attack</a>
     */
    @Override
    @SuppressWarnings({ "deprecation", "removal", "Finalize", "checkstyle:NoFinalizer", "PMD.EmptyFinalizer",
            "PMD.EmptyMethodInAbstractClassShouldBeAbstract" })
    protected final void finalize() throws Throwable {
        // Do nothing
    }
}
