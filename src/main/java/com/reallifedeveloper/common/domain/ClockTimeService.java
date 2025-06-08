package com.reallifedeveloper.common.domain;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * An implementation of the {@link TimeService} interface that uses a {@code java.time.Clock}. The clock is by default
 * {@code Clock.systemUTC()} but can be changed using the {@link #setClock(clock)} method.
 *
 * @author RealLifeDeveloper
 */
public class ClockTimeService implements TimeService {

    private Clock clock = Clock.systemUTC();

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }

    /**
     * Sets the {@code java.time.Clock} used by this {@code ClockTimeService}.
     *
     * @param clock the new {@code Clock} to use
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
