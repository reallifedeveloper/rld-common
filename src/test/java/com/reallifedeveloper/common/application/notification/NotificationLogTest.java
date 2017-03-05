package com.reallifedeveloper.common.application.notification;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NotificationLogTest {

    private NotificationLogId currentNotificationLogId = new NotificationLogId("6,10");
    private NotificationLogId nextNotificationLogId = new NotificationLogId("11,15");
    private NotificationLogId previousNotificationLogId = new NotificationLogId("1,5");
    private List<Notification> notifications = new ArrayList<>();

    @Test
    public void constructor() {
        NotificationLog notificationLog = new NotificationLog(currentNotificationLogId, nextNotificationLogId,
                previousNotificationLogId, notifications, true);
        Assert.assertEquals("Wrong current notification log ID: ", currentNotificationLogId,
                notificationLog.currentNotificationLogId());
        Assert.assertEquals("Wrong next notification log ID: ", nextNotificationLogId,
                notificationLog.nextNotificationLogId());
        Assert.assertEquals("Wrong previous notification log ID: ", previousNotificationLogId,
                notificationLog.previousNotificationLogId());
        Assert.assertEquals("Wrong notifications: ", notifications, notificationLog.notifications());
        Assert.assertTrue("isArchived should be true", notificationLog.isArchived());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullCurrentNotificationLogId() {
        new NotificationLog(null, nextNotificationLogId, previousNotificationLogId, notifications, true);
    }

    @Test
    public void constructorNullNextNotificationLogId() {
        // There should be no exception
        new NotificationLog(currentNotificationLogId, null, previousNotificationLogId, notifications, true);
    }

    @Test
    public void constructorNullPreviousNotificationLogId() {
        // There should be no exception
        new NotificationLog(currentNotificationLogId, nextNotificationLogId, null, notifications, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullNotifications() {
        new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId, null, true);
    }

    @Test
    public void testToString() {
        NotificationLog notificationLog = new NotificationLog(currentNotificationLogId, nextNotificationLogId,
                previousNotificationLogId, notifications, true);
        Assert.assertEquals("NotificationLog{current=" + currentNotificationLogId
                + ", next=" + nextNotificationLogId
                + ", previous=" + previousNotificationLogId
                + ", notifications=[]"
                + "}", notificationLog.toString());
    }
}
