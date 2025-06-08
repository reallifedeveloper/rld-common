package com.reallifedeveloper.common.domain;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of the {@link TimeService} interface useful for testing.
 * <p>
 * The {@link #now()} method returns a predefined set of dates.
 *
 * @author RealLifeDeveloper
 */
public class TestTimeService implements TimeService {

    /**
     * The format used when parsing date and time strings ({@value #DATE_TIME_FORMAT}).
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ssX";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    private int currentDateTimeIndex;
    private List<ZonedDateTime> dateTimes;

    /**
     * {@inheritDoc}
     * <p>
     * This method returns the DateTimes set using the {@link #setDateTimes(String...)} method. If it is called more times than
     * there are test dates defined, it starts over again from the first test date.
     */
    @Override
    public ZonedDateTime now() {
        if (dateTimes == null) {
            throw new IllegalStateException("Cannot call now method before calling setDates");
        }
        if (currentDateTimeIndex == dateTimes.size()) {
            currentDateTimeIndex = 0;
        }
        return dateTimes.get(currentDateTimeIndex++);
    }

    /**
     * Gives the list of dates that are returned by this service.
     *
     * @return the list of dates returned by this service
     */
    public List<ZonedDateTime> dateTimes() {
        return Collections.unmodifiableList(dateTimes);
    }

    /**
     * Sets the DateTimes returned from the {@link #now()} method.
     *
     * @param dateTimes the dates the {@code now()} method should return
     */
    public void setDateTimes(ZonedDateTime... dateTimes) {
        if (dateTimes == null) {
            throw new IllegalArgumentException("dateTimes must not be null");
        }
        if (dateTimes.length == 0) {
            throw new IllegalArgumentException("dateTimes must not be empty");
        }
        this.dateTimes = Arrays.asList(dateTimes);
    }

    /**
     * Sets the DateTimes returned from the {@link #now()} method.
     *
     * @param dateTimeStrings the dates the {@code now()} method should return, on the format {@value #DATE_TIME_FORMAT}
     */
    public void setDateTimes(String... dateTimeStrings) {
        if (dateTimeStrings == null) {
            throw new IllegalArgumentException("dateTimeStrings must not be null");
        }
        if (dateTimeStrings.length == 0) {
            throw new IllegalArgumentException("dateTimeStrings must not be empty");
        }
        this.dateTimes = new ArrayList<>();
        for (String dateString : dateTimeStrings) {
            this.dateTimes.add(ZonedDateTime.parse(dateString, DATE_TIME_FORMATTER));
        }
    }
}
