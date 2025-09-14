package com.reallifedeveloper.common.domain;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.tools.test.TestUtil;

public class ClockTimeServiceTest {

    @Test
    public void now() {
        ClockTimeService timeService = new ClockTimeService();
        ZonedDateTime before = TestUtil.utcNow();
        ZonedDateTime now = timeService.now();
        long diffMillis = now.toInstant().toEpochMilli() - before.toInstant().toEpochMilli();
        Assertions.assertTrue(diffMillis <= 1, "Difference in times between now and before is too large: " + diffMillis);
    }

    @Test
    public void setClock() {
        ClockTimeService timeService = new ClockTimeService();
        ZonedDateTime testDateTime = ZonedDateTime.parse("2023-11-16T21:20:00Z");
        timeService.setClock(Clock.fixed(testDateTime.toInstant(), testDateTime.getZone()));
        Assertions.assertEquals(testDateTime, timeService.now());
        // Verify that the next call to now() gives the same time:
        Assertions.assertEquals(testDateTime, timeService.now());
    }
}
