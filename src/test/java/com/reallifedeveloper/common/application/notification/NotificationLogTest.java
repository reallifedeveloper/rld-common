package com.reallifedeveloper.common.application.notification;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.event.TestEvent;

public class NotificationLogTest {

    private NotificationLogId currentNotificationLogId = new NotificationLogId("6,10");
    private NotificationLogId nextNotificationLogId = new NotificationLogId("11,15");
    private NotificationLogId previousNotificationLogId = new NotificationLogId("1,5");
    private List<Notification> notifications = new ArrayList<>();

    @Test
    public void constructor() {
        NotificationLog notificationLog = new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId,
                notifications, true);
        Assertions.assertEquals(currentNotificationLogId, notificationLog.current(), "Wrong current notification log ID");
        Assertions.assertEquals(nextNotificationLogId, notificationLog.next().get(), "Wrong next notification log ID");
        Assertions.assertEquals(previousNotificationLogId, notificationLog.previous().get(), "Wrong previous notification log ID");
        Assertions.assertEquals(notifications, notificationLog.notifications(), "Wrong notifications");
        Assertions.assertTrue(notificationLog.isArchived(), "isArchived should be true");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullCurrentNotificationLogId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotificationLog(null, nextNotificationLogId, previousNotificationLogId, notifications, true));
    }

    @Test
    public void constructorNullNextNotificationLogId() {
        // No exception should be thrown
        new NotificationLog(currentNotificationLogId, null, previousNotificationLogId, notifications, true);
    }

    @Test
    public void constructorNullPreviousNotificationLogId() {
        // No exception should be thrown
        new NotificationLog(currentNotificationLogId, nextNotificationLogId, null, notifications, true);
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullNotifications() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId, null, true));
    }

    @Test
    public void constructorDefensiveCopyOfNotifications() {
        notifications.add(Notification.create(new TestEvent(42, "foo"), 4711L));
        NotificationLog notificationLog = new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId,
                notifications, false);
        notifications.clear();
        Assertions.assertFalse(notificationLog.notifications().isEmpty(), "Notifications in NotificationLog should not be empty");
    }

    @Test
    public void testToString() {
        NotificationLog notificationLog = new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId,
                notifications, true);
        Assertions.assertEquals(
                "NotificationLog[current=%s, nullableNext=%s, nullablePrevious=%s, notifications=[], isArchived=true]"
                        .formatted(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId),
                notificationLog.toString(), "Incorrect toString representation");
    }
}
