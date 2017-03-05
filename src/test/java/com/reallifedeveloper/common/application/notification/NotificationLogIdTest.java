package com.reallifedeveloper.common.application.notification;

import org.junit.Assert;
import org.junit.Test;

public class NotificationLogIdTest {

    @Test
    public void constructorLongs() {
        NotificationLogId id = new NotificationLogId(42, 4711);
        Assert.assertEquals("Incorrect low: ", 42, id.low());
        Assert.assertEquals("Incorrect high: ", 4711, id.high());
    }

    @Test
    public void constructorLongsLowEqualToHigh() {
        NotificationLogId id = new NotificationLogId(42, 42);
        Assert.assertEquals("Incorrect low: ", 42, id.low());
        Assert.assertEquals("Incorrect high: ", 42, id.high());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorLongsLowGreaterThanHigh() {
        new NotificationLogId(42, 41);
    }

    @Test
    public void constructorString() {
        NotificationLogId id = new NotificationLogId("42,4711");
        Assert.assertEquals("Incorrect low: ", 42, id.low());
        Assert.assertEquals("Incorrect high: ", 4711, id.high());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorStringNull() {
        new NotificationLogId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorStringNotIntegers() {
        new NotificationLogId("foo,bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorStringTooFewIntegers() {
        new NotificationLogId("42");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorStringTooManyIntegers() {
        new NotificationLogId("42,4711,10000");
    }

    @Test
    public void constructorStringLowEqualToHigh() {
        NotificationLogId id = new NotificationLogId("42,42");
        Assert.assertEquals("Incorrect low: ", 42, id.low());
        Assert.assertEquals("Incorrect high: ", 42, id.high());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorStringLowGreaterThanHigh() {
        new NotificationLogId("42,41");
    }

    @Test
    public void externalForm() {
        NotificationLogId id = new NotificationLogId(42, 4711);
        Assert.assertEquals("Incorrect external form: ", "42,4711", id.externalForm());
    }

    @Test
    public void next() {
        int low = 41;
        int high = 60;
        int count = high - low + 1;
        NotificationLogId id = new NotificationLogId(low, high);
        NotificationLogId nextId = id.next();
        Assert.assertEquals("Next id has wrong low: ", low + count, nextId.low());
        Assert.assertEquals("Next id has wrong high: ", high + count, nextId.high());
    }

    @Test
    public void previous() {
        int low = 41;
        int high = 60;
        int count = high - low + 1;
        NotificationLogId id = new NotificationLogId(low, high);
        NotificationLogId previousId = id.previous();
        Assert.assertEquals("Previous id has wrong low: ", low - count, previousId.low());
        Assert.assertEquals("Previous id has wrong high: ", high - count, previousId.high());
    }

    @Test
    public void toStringMethod() {
        NotificationLogId id = new NotificationLogId(42, 4711);
        Assert.assertEquals("Incorrect toString: ", "42,4711", id.toString());
    }
}
