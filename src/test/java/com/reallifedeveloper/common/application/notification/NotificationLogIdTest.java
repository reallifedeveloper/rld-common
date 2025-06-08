package com.reallifedeveloper.common.application.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NotificationLogIdTest {

    @Test
    public void constructorLongs() {
        NotificationLogId id = new NotificationLogId(42, 4711);
        assertEquals(42, id.low(), "Incorrect low: ");
        assertEquals(4711, id.high(), "Incorrect high: ");
    }

    @Test
    public void constructorLongsLowEqualToHigh() {
        NotificationLogId id = new NotificationLogId(42, 42);
        assertEquals(42, id.low(), "Incorrect low: ");
        assertEquals(42, id.high(), "Incorrect high: ");
    }

    @Test
    public void constructorLongsLowGreaterThanHigh() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationLogId(42, 41));
        assertEquals("low must not be greater than high: low=42, high=41", e.getMessage());
    }

    @Test
    public void constructorString() {
        NotificationLogId id = new NotificationLogId("42,4711");
        assertEquals(42, id.low(), "Incorrect low: ");
        assertEquals(4711, id.high(), "Incorrect high: ");
    }

    @Test
    public void constructorStringNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationLogId(null));
        assertEquals("notificationLogId must not be null", e.getMessage());

    }

    @Test
    public void constructorStringNotIntegers() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationLogId("foo,bar"));
        assertEquals(
                "notificationLogId should be on the form '<low>,<high>', where <low> and <high> are integers: notificationLogId=foo,bar",
                e.getMessage());
    }

    @Test
    public void constructorStringTooFewIntegers() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationLogId("42"));
        assertEquals("notificationLogId should be on the form '<low>,<high>', where <low> and <high> are integers: notificationLogId=42",
                e.getMessage());
    }

    @Test
    public void constructorStringTooManyIntegers() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationLogId("42,4711,10000"));
        assertEquals("notificationLogId should be on the form '<low>,<high>', where <low> and <high> are integers: "
                + "notificationLogId=42,4711,10000", e.getMessage());
    }

    @Test
    public void constructorStringLowEqualToHigh() {
        NotificationLogId id = new NotificationLogId("42,42");
        assertEquals(42, id.low(), "Incorrect low: ");
        assertEquals(42, id.high(), "Incorrect high: ");
    }

    @Test
    public void constructorStringLowGreaterThanHigh() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationLogId("42,41"));
        assertEquals("low must not be greater than high: low=42, high=41", e.getMessage());
    }

    @Test
    public void externalForm() {
        NotificationLogId id = new NotificationLogId(42, 4711);
        assertEquals("42,4711", id.externalForm(), "Incorrect external form: ");
    }

    @Test
    public void next() {
        int low = 41;
        int high = 60;
        int count = high - low + 1;
        NotificationLogId id = new NotificationLogId(low, high);
        NotificationLogId nextId = id.next();
        assertEquals(low + count, nextId.low(), "Next id has wrong low: ");
        assertEquals(high + count, nextId.high(), "Next id has wrong high: ");
    }

    @Test
    public void previous() {
        int low = 41;
        int high = 60;
        int count = high - low + 1;
        NotificationLogId id = new NotificationLogId(low, high);
        NotificationLogId previousId = id.previous();
        assertEquals(low - count, previousId.low(), "Previous id has wrong low: ");
        assertEquals(high - count, previousId.high(), "Previous id has wrong high: ");
    }

    @Test
    public void toStringMethod() {
        NotificationLogId id = new NotificationLogId(42, 4711);
        assertEquals("42,4711", id.toString(), "Incorrect toString: ");
    }
}
