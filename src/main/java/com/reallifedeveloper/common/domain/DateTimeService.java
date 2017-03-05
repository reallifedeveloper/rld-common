package com.reallifedeveloper.common.domain;

import java.util.Date;

/**
 * An implementation of the {@link TimeService} interface that works with the system clock.
 *
 * @author RealLifeDeveloper
 */
public class DateTimeService implements TimeService {

    @Override
    public Date now() {
        // CHECKSTYLE:OFF
        return new Date();
        // CHECKSTYLE:ON
    }

}
