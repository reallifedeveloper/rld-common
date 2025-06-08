package com.reallifedeveloper.common.test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;

import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public final class TestUtil {
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(GsonObjectSerializer.LOCAL_DATE_TIME_FORMAT);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(GsonObjectSerializer.DATE_TIME_FORMAT);

    private TestUtil() {
    }

    /**
     * Asserts that two {@code LocalDateTime} objects are equal "enough", rounding to milliseconds and comparing the time zone offset.
     *
     * @param expected the expected value
     * @param actual the actual value
     * @param message the message to display if the assertion fails
     */
    public static void assertEquals(LocalDateTime expected, LocalDateTime actual, String message) {
        Assertions.assertEquals(format(expected), format(actual), message);
    }

    /**
     * Asserts that two {@code ZonedDateTime} objects are equal "enough", rounding to milliseconds and comparing the time zone offset.
     *
     * @param expected the expected value
     * @param actual the actual value
     * @param message the message to display if the assertion fails
     */
    public static void assertEquals(ZonedDateTime expected, ZonedDateTime actual, String message) {
        Assertions.assertEquals(format(expected), format(actual), message);
    }

    /**
     * Formats a {@code LocalDateTime} object as a string using the pattern {@value GsonObjectSerializer#LOCAL_DATE_TIME_FORMAT}.
     *
     * @param localDateTime the {@code LocalDateTime} object to format
     *
     * @return the formatted string
     */
    public static String format(LocalDateTime localDateTime) {
        return localDateTime.format(LOCAL_DATE_TIME_FORMATTER);
    }

    /**
     * Formats a {@code ZonedDateTime} object as a string using the pattern {@value GsonObjectSerializer#DATE_TIME_FORMAT}.
     *
     * @param zonedDateTime the {@code ZonedDateTime} object to format
     *
     * @return the formatted string
     */
    public static String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DATE_TIME_FORMATTER);
    }

}
