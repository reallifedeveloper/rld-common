package com.reallifedeveloper.common.application.notification;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationServiceTest {

    private static final String PUBLICATION_CHANNEL = "FOO_EXCHANGE";

    private InMemoryStoredEventRepository storedEventRepository = new InMemoryStoredEventRepository();
    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
    private EventStore eventStore = new EventStore(objectSerializer, storedEventRepository);
    private InMemoryPublishedMessageTrackerRepository messageTrackerRepository =
            new InMemoryPublishedMessageTrackerRepository();
    private TestNotificationPublisher notificationPublisher = new TestNotificationPublisher();

    private NotificationService service =
            new NotificationService(eventStore, messageTrackerRepository, notificationPublisher);

    @Test
    public void currentNotificationLog() {
        int numEvents = 13;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int batchSize = 5;
        NotificationLog notificationLog = service.currentNotificationLog(batchSize);
        Assert.assertEquals("Wrong current notification id: ", "11,15",
                notificationLog.currentNotificationLogId().externalForm());
        Assert.assertNull("Next notification id should be null", notificationLog.nextNotificationLogId());
        Assert.assertEquals("Wrong previous notification id: ", "6,10",
                notificationLog.previousNotificationLogId().externalForm());
        Assert.assertEquals("Wrong number of notifications: ", numEvents % batchSize,
                notificationLog.notifications().size());
        Assert.assertFalse("Notification log should not be archived", notificationLog.isArchived());
    }

    @Test
    public void currentNotificationLogFullBatch() {
        int numEvents = 15;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int batchSize = 5;
        NotificationLog notificationLog = service.currentNotificationLog(batchSize);
        Assert.assertEquals("Wrong current notification id: ", "11,15",
                notificationLog.currentNotificationLogId().externalForm());
        Assert.assertNull("Next notification id should be null", notificationLog.nextNotificationLogId());
        Assert.assertEquals("Wrong previous notification id: ", "6,10",
                notificationLog.previousNotificationLogId().externalForm());
        Assert.assertEquals("Wrong number of notifications: ", 5, notificationLog.notifications().size());
        Assert.assertTrue("Notification log should be archived", notificationLog.isArchived());
    }

    @Test
    public void currentNotificationLogNoNotifications() {
        int batchSize = 5;
        NotificationLog notificationLog = service.currentNotificationLog(batchSize);
        Assert.assertEquals("Wrong current notification id: ", "1," + batchSize,
                notificationLog.currentNotificationLogId().externalForm());
        Assert.assertNull("Next notification id should be null", notificationLog.nextNotificationLogId());
        Assert.assertNull("Previous notification id should be null", notificationLog.previousNotificationLogId());
        Assert.assertTrue("Notifications should be empty", notificationLog.notifications().isEmpty());
        Assert.assertFalse("Notification log should not be archived", notificationLog.isArchived());
    }

    @Test
    public void notificationLog() {
        int numEvents = 13;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int firstEventToGet = 6;
        int batchSize = 5;
        NotificationLogId notificationLogId = new NotificationLogId(firstEventToGet, firstEventToGet + batchSize - 1);
        NotificationLog notificationLog = service.notificationLog(notificationLogId);
        Assert.assertEquals("Wrong current notification id: ", "6,10",
                notificationLog.currentNotificationLogId().externalForm());
        Assert.assertEquals("Wrong next notification id: ", "11,15",
                notificationLog.nextNotificationLogId().externalForm());
        Assert.assertEquals("Wrong previous notification id: ", "1,5",
                notificationLog.previousNotificationLogId().externalForm());
        Assert.assertEquals("Wrong number of notifications: ", batchSize, notificationLog.notifications().size());
        Assert.assertTrue("Notification log should be archived", notificationLog.isArchived());
    }

    @Test
    public void notificationLogNoNotifications() {
        int firstEventToGet = 6;
        int batchSize = 5;
        NotificationLogId notificationLogId = new NotificationLogId(firstEventToGet, firstEventToGet + batchSize - 1);
        NotificationLog notificationLog = service.notificationLog(notificationLogId);
        Assert.assertEquals("Wrong current notification id: ", "6,10",
                notificationLog.currentNotificationLogId().externalForm());
        Assert.assertNull("Next notification id should be null", notificationLog.nextNotificationLogId());
        Assert.assertEquals("Wrong previous notification id: ", "1,5",
                notificationLog.previousNotificationLogId().externalForm());
        Assert.assertTrue("Notifications should be empty", notificationLog.notifications().isEmpty());
        Assert.assertFalse("Notification log should not be archived", notificationLog.isArchived());
    }

    @Test
    public void publishNotifications() throws Exception {
        int numEvents = 3;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        service.publishNotifications(PUBLICATION_CHANNEL);
        verifyPublishedNotifications(PUBLICATION_CHANNEL, numEvents);
    }

    @Test
    public void publishSameNotificationsTwice() throws Exception  {
        int numEvents = 3;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        service.publishNotifications(PUBLICATION_CHANNEL);
        verifyPublishedNotifications(PUBLICATION_CHANNEL, numEvents);
        service.publishNotifications(PUBLICATION_CHANNEL);
        verifyPublishedNotifications(PUBLICATION_CHANNEL, numEvents);
    }

    @Test
    public void publishNotificatonisTwiceWithSomeNewEvents() throws Exception {
        int numEvents = 3;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        service.publishNotifications(PUBLICATION_CHANNEL);
        verifyPublishedNotifications(PUBLICATION_CHANNEL, numEvents);
        eventStore.add(new TestEvent(numEvents + 1, "foo" + (numEvents + 1)));
        service.publishNotifications(PUBLICATION_CHANNEL);
        verifyPublishedNotifications(PUBLICATION_CHANNEL, numEvents + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullEventStore() {
        new NotificationService(null, messageTrackerRepository, notificationPublisher);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullMessageTrackerRepository() {
        new NotificationService(eventStore, null, notificationPublisher);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullNotificationPublisher() {
        new NotificationService(eventStore, messageTrackerRepository, null);
    }

    @Test
    public void noArgsConstructor() {
        new NotificationService();
    }

    private void verifyPublishedNotifications(String publicationChannel, int numNotifications) {
        List<Notification> notifications = notificationPublisher.publishedNotifications(publicationChannel);
        Assert.assertEquals("Wrong number of notifications: ", numNotifications, notifications.size());
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            Assert.assertEquals("Wrong stored event ID: ", i + 1, notification.storedEventId());
            Assert.assertEquals("Wrong event type: ", TestEvent.class.getName(), notification.eventType());
            Assert.assertEquals("Wrong type of domain event: ", TestEvent.class, notification.event().getClass());
        }

    }
}
