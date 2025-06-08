package com.reallifedeveloper.common.resource.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationLog;
import com.reallifedeveloper.common.application.notification.NotificationLogId;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationLogRepresentationTest {

    private final ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @Test
    public void constructor() {
        String self = "foo";
        String next = "bar";
        String previous = "baz";
        List<Notification> notifications = new ArrayList<>();
        notifications.add(Notification.create(new TestEvent(1, "foo"), 4711L));
        NotificationLog notificationLog = createNotificationLog(new ArrayList<>(), 1, 10, true);
        NotificationLogRepresentation notificationLogRepresentation = new NotificationLogRepresentation(notificationLog,
                objectSerializer);

        notificationLogRepresentation.setSelf(self);
        notificationLogRepresentation.setNext(next);
        notificationLogRepresentation.setPrevious(previous);

        assertEquals(self, notificationLogRepresentation.getSelf(), "Wrong self link");
        assertEquals(next, notificationLogRepresentation.getNext(), "Wrong next link");
        assertEquals(previous, notificationLogRepresentation.getPrevious(), "Wrong previous link");
        assertEquals(0, notificationLogRepresentation.notifications().size(),
                "Wrong number of notifications");
        assertTrue(notificationLogRepresentation.isArchived(), "Archived flag should be true");
    }

    @Test
    public void constructorNullNotificationLog() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new NotificationLogRepresentation(null, objectSerializer));
        assertEquals("Arguments must not be null: notificationLog=null, objectSerializer=" + objectSerializer,
                exception.getMessage());
    }

    @Test
    public void constructorNullSerializer() {
        NotificationLog notificationLog = createNotificationLog(new ArrayList<>(), 1, 10, false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new NotificationLogRepresentation(notificationLog, null));
        assertEquals("Arguments must not be null: notificationLog=" + notificationLog + ", objectSerializer=null",
                exception.getMessage());
    }

    private NotificationLog createNotificationLog(List<Notification> notifications, int low, int high,
            boolean isArchived) {
        int batchSize = high - low + 1;
        NotificationLogId currentNotificationLogId = new NotificationLogId(low + "," + high);
        NotificationLogId nextNotificationLogId = new NotificationLogId((low + batchSize) + "," + (high + batchSize));
        NotificationLogId previousNotificationLogId = new NotificationLogId(
                (low - batchSize) + "," + (high - batchSize));
        return new NotificationLog(currentNotificationLogId, nextNotificationLogId, previousNotificationLogId,
                notifications, isArchived);
    }
}
