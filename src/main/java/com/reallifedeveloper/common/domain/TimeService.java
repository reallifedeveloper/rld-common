package com.reallifedeveloper.common.domain;

import java.util.Date;

/**
 * A service for working with "current" time. The idea is to make it possible to switch
 * in different implementations, e.g., for testing.
 *
 * @author RealLifeDeveloper
 */
public interface TimeService {

    /**
     * Gives the current time as a <code>java.util.Date</code> object.
     *
     * @return the current time
     */
    Date now();
}
