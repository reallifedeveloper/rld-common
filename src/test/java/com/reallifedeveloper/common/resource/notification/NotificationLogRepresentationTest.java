package com.reallifedeveloper.common.resource.notification;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationLog;
import com.reallifedeveloper.common.application.notification.NotificationLogId;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationLogRepresentationTest {

    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @Test
    public void constructor() {
        String self = "foo";
        String next = "bar";
        String previous = "baz";
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(new TestEvent(1, "foo"), 4711L));
        NotificationLog notificationLog = createNotificationLog(new ArrayList<>(), 1, 10, true);
        NotificationLogRepresentation notificationLogRepresentation =
                new NotificationLogRepresentation(notificationLog, objectSerializer);
        notificationLogRepresentation.setSelf(self);
        notificationLogRepresentation.setNext(next);
        notificationLogRepresentation.setPrevious(previous);
        Assert.assertEquals("Wrong self link: ", self, notificationLogRepresentation.getSelf());
        Assert.assertEquals("Wrong next link: ", next, notificationLogRepresentation.getNext());
        Assert.assertEquals("Wrong previous link: ", previous, notificationLogRepresentation.getPrevious());
        Assert.assertEquals("Wrong number of notifications: ", 0, notificationLogRepresentation.notifications().size());
        Assert.assertTrue("Archived flag should be true", notificationLogRepresentation.isArchived());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullNotificationLog() {
        new NotificationLogRepresentation(null, objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullSerializer() {
        NotificationLog notificationLog = createNotificationLog(new ArrayList<>(), 1, 10, false);
        new NotificationLogRepresentation(notificationLog, null);
    }

    private NotificationLog createNotificationLog(List<Notification> notifications, int low, int high,
            boolean isArchived) {
        int batchSize = high - low + 1;
        NotificationLogId currentNotificationLogId = new NotificationLogId(low + "," + high);
        NotificationLogId nextNotificationLogId = new NotificationLogId((low + batchSize) + "," + (high + batchSize));
        NotificationLogId previousNotificationLogId =
                new NotificationLogId((low - batchSize) + "," + (high - batchSize));
        return new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId,
                notifications, isArchived);
    }
}
