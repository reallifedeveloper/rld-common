package com.reallifedeveloper.common.domain;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTimeServiceTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(TestTimeService.DATE_TIME_FORMAT);

    private static final String DATE_1 = "2015-01-07 13:04:00Z";
    private static final String DATE_2 = "2015-01-07 13:11:00Z";

    private TestTimeService timeService = new TestTimeService();

    @Test
    public void nowDateTimes() throws Exception {
        ZonedDateTime[] dateTimes = dateTimes(DATE_1, DATE_2);
        timeService.setDateTimes(dateTimes);
        Assertions.assertEquals(dateTimes[0], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(dateTimes[1], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(dateTimes[0], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(dateTimes[1], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(Arrays.asList(dateTimes), timeService.dateTimes(), "Wrong dates: ");
    }

    @Test
    public void nowDateTimeStrings() throws Exception {
        String[] dateStrings = { DATE_1, DATE_2 };
        ZonedDateTime[] dates = dateTimes(dateStrings);
        timeService.setDateTimes(dateStrings);
        Assertions.assertEquals(dates[0], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(dates[1], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(dates[0], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(dates[1], timeService.now(), "Wrong timestamp: ");
        Assertions.assertEquals(Arrays.asList(dates), timeService.dateTimes(), "Wrong dates: ");
    }

    @Test
    public void nowBeforeCallingSetDates() {
        Assertions.assertThrows(IllegalStateException.class, timeService::now);
    }

    @Test
    @SuppressWarnings("NullAway")
    public void setNullDates() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> timeService.setDateTimes((ZonedDateTime[]) null));
    }

    @Test
    public void setEmptyDates() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> timeService.setDateTimes(new ZonedDateTime[0]));
    }

    @Test
    public void setMalformedDateString() {
        String[] dateStrings = { DATE_1, DATE_2, "foo" };
        Assertions.assertThrows(DateTimeParseException.class, () -> timeService.setDateTimes(dateStrings));
    }

    @Test
    @SuppressWarnings("NullAway")
    public void setNullDateStrings() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> timeService.setDateTimes((String[]) null));
    }

    @Test
    public void setEmptyDateStrings() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> timeService.setDateTimes(new String[0]));
    }

    private ZonedDateTime[] dateTimes(String... dateTimeStrings) throws ParseException {
        ZonedDateTime[] dateTimes = new ZonedDateTime[dateTimeStrings.length];
        for (int i = 0; i < dateTimeStrings.length; i++) {
            String dateString = dateTimeStrings[i];
            dateTimes[i] = ZonedDateTime.parse(dateString, DATE_TIME_FORMATTER);
        }
        return dateTimes;
    }
}
