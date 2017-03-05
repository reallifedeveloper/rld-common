package com.reallifedeveloper.common.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TestTimeServiceTest {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_1 = "2015-01-07 13:04:00";
    private static final String DATE_2 = "2015-01-07 13:11:00";

    private TestTimeService timeService = new TestTimeService();

    @Test
    public void nowDates() throws Exception {
        Date[] dates = dates(DATE_1, DATE_2);
        timeService.setDates(dates);
        Assert.assertEquals("Wrong timestamp: ", dates[0], timeService.now());
        Assert.assertEquals("Wrong timestamp: ", dates[1], timeService.now());
        Assert.assertEquals("Wrong timestamp: ", dates[0], timeService.now());
        Assert.assertEquals("Wrong timestamp: ", dates[1], timeService.now());
        Assert.assertEquals("Wrong dates: ", Arrays.asList(dates), timeService.dates());
    }

    @Test
    public void nowDateStrings() throws Exception {
        String[] dateStrings = { DATE_1, DATE_2 };
        Date[] dates = dates(dateStrings);
        timeService.setDates(dateStrings);
        Assert.assertEquals("Wrong timestamp: ", dates[0], timeService.now());
        Assert.assertEquals("Wrong timestamp: ", dates[1], timeService.now());
        Assert.assertEquals("Wrong timestamp: ", dates[0], timeService.now());
        Assert.assertEquals("Wrong timestamp: ", dates[1], timeService.now());
        Assert.assertEquals("Wrong dates: ", Arrays.asList(dates), timeService.dates());
    }

    @Test(expected = IllegalStateException.class)
    public void nowBeforeCallingSetDates() {
        timeService.now();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNullDates() {
        timeService.setDates((Date[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyDates() {
        timeService.setDates(new Date[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMalformedDateString() {
        String[] dateStrings = { DATE_1, DATE_2, "foo" };
        timeService.setDates(dateStrings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNullDateStrings() {
        timeService.setDates((String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyDateStrings() {
        timeService.setDates(new String[0]);
    }

    private Date[] dates(String... dateStrings) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date[] dates = new Date[dateStrings.length];
        for (int i = 0; i < dateStrings.length; i++) {
            String dateString = dateStrings[i];
            dates[i] = dateFormat.parse(dateString);
        }
        return dates;
    }
}
