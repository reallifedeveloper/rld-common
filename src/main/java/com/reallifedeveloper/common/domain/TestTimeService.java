package com.reallifedeveloper.common.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
     * The format used when parsing date and time strings ({@value #DATE_FORMAT}).
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private int currentDateIndex;
    private Date[] dates;

    /**
     * {@inheritDoc}
     * <p>
     * This method returns the dates given when the service was created. If it is called more times than
     * there are test dates defined, it starts over again from the first test date.
     */
    @Override
    public Date now() {
        if (dates == null) {
            throw new IllegalStateException("Cannot call now method before calling setDates");
        }
        if (currentDateIndex == dates.length) {
            currentDateIndex = 0;
        }
        return dates[currentDateIndex++];
    }

    /**
     * Gives the list of dates that are returned by this service.
     *
     * @return the list of dates returned by this service
     */
    public List<Date> dates() {
        return Arrays.asList(dates);
    }

    /**
     * Sets the dates returned from the {@link #now()} method.
     *
     * @param dates the dates the <code>now()</code> method should return
     */
    public void setDates(Date... dates) {
        if (dates == null) {
            throw new IllegalArgumentException("dates must not be null");
        }
        if (dates.length == 0) {
            throw new IllegalArgumentException("dates must not be empty");
        }
        this.dates = dates;
    }

    /**
     * Sets the dates returned from the {@link #now()} method.
     *
     * @param dateStrings the dates the <code>now()</code> method should return, on the format {@value #DATE_FORMAT}
     */
    public void setDates(String... dateStrings) {
        if (dateStrings == null) {
            throw new IllegalArgumentException("dateStrings must not be null");
        }
        if (dateStrings.length == 0) {
            throw new IllegalArgumentException("dateStrings must not be empty");
        }
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        this.dates = new Date[dateStrings.length];
        for (int i = 0; i < dateStrings.length; i++) {
            String dateString = dateStrings[i];
            try {
                this.dates[i] = dateFormat.parse(dateString);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Failed to parse '" + dateString + "' as a date on the format '"
                        + DATE_FORMAT + "'");
            }
        }
    }
}
