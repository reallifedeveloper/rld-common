package com.reallifedeveloper.common.domain;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateTimeServiceTest {

    @Test
    public void now() {
        DateTimeService timeService = new DateTimeService();
        Date before = new Date();
        Date now = timeService.now();
        long diffMillis = now.getTime() - before.getTime();
        Assert.assertTrue("Difference in times between now and before is too large: " + diffMillis, diffMillis <= 1);
    }
}
