package com.reallifedeveloper.common.domain;

import java.time.ZonedDateTime;

/**
 * A service for working with "current" time. The idea is to make it possible to switch in different implementations, e.g., for testing.
 *
 * @author RealLifeDeveloper
 */
@FunctionalInterface
public interface TimeService {

    /**
     * Gives the current time as a {@code java.time.ZonedDateTime} object.
     *
     * @return the current time
     */
    ZonedDateTime now();
}
