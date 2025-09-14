package com.reallifedeveloper.common.application.notification;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    private InMemoryPublishedMessageTrackerRepository messageTrackerRepository = new InMemoryPublishedMessageTrackerRepository();
    private TestNotificationPublisher notificationPublisher = new TestNotificationPublisher();

    private NotificationService service = new NotificationService(eventStore, messageTrackerRepository, notificationPublisher);

    @Test
    public void currentNotificationLog() {
        int numEvents = 13;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int batchSize = 5;
        NotificationLog notificationLog = service.currentNotificationLog(batchSize);
        Assertions.assertEquals("11,15", notificationLog.current().externalForm(), "Wrong current notification id");
        Assertions.assertFalse(notificationLog.next().isPresent(), "Next notification id should not be present");
        Assertions.assertEquals("6,10", notificationLog.previous().get().externalForm(), "Wrong previous notification id");
        Assertions.assertEquals(numEvents % batchSize, notificationLog.notifications().size(), "Wrong number of notifications");
        Assertions.assertFalse(notificationLog.isArchived(), "Notification log should not be archived");
    }

    @Test
    public void currentNotificationLogFullBatch() {
        int numEvents = 15;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int batchSize = 5;
        NotificationLog notificationLog = service.currentNotificationLog(batchSize);
        Assertions.assertEquals("11,15", notificationLog.current().externalForm(), "Wrong current notification id");
        Assertions.assertFalse(notificationLog.next().isPresent(), "Next notification id should not be present");
        Assertions.assertEquals("6,10", notificationLog.previous().get().externalForm(), "Wrong previous notification id");
        Assertions.assertEquals(5, notificationLog.notifications().size(), "Wrong number of notifications");
        Assertions.assertTrue(notificationLog.isArchived(), "Notification log should be archived");
    }

    @Test
    public void currentNotificationLogNoNotifications() {
        int batchSize = 5;
        NotificationLog notificationLog = service.currentNotificationLog(batchSize);
        Assertions.assertEquals("1," + batchSize, notificationLog.current().externalForm(), "Wrong current notification id");
        Assertions.assertFalse(notificationLog.next().isPresent(), "Next notification id should not be present");
        Assertions.assertFalse(notificationLog.previous().isPresent(), "Previous notification id should not be present");
        Assertions.assertTrue(notificationLog.notifications().isEmpty(), "Notifications should be empty");
        Assertions.assertFalse(notificationLog.isArchived(), "Notification log should not be archived");
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
        Assertions.assertEquals("6,10", notificationLog.current().externalForm(), "Wrong current notification id");
        Assertions.assertEquals("11,15", notificationLog.next().get().externalForm(), "Wrong next notification id");
        Assertions.assertEquals("1,5", notificationLog.previous().get().externalForm(), "Wrong previous notification id");
        Assertions.assertEquals(batchSize, notificationLog.notifications().size(), "Wrong number of notifications");
        Assertions.assertTrue(notificationLog.isArchived(), "Notification log should be archived");
    }

    @Test
    public void notificationLogNoNotifications() {
        int firstEventToGet = 6;
        int batchSize = 5;
        NotificationLogId notificationLogId = new NotificationLogId(firstEventToGet, firstEventToGet + batchSize - 1);
        NotificationLog notificationLog = service.notificationLog(notificationLogId);
        Assertions.assertEquals("6,10", notificationLog.current().externalForm(), "Wrong current notification id");
        Assertions.assertFalse(notificationLog.next().isPresent(), "Next notification id should not be present");
        Assertions.assertEquals("1,5", notificationLog.previous().get().externalForm(), "Wrong previous notification id");
        Assertions.assertTrue(notificationLog.notifications().isEmpty(), "Notifications should be empty");
        Assertions.assertFalse(notificationLog.isArchived(), "Notification log should not be archived");
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
    public void publishSameNotificationsTwice() throws Exception {
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
    public void publishNotificationsTwiceWithSomeNewEvents() throws Exception {
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

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullEventStore() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NotificationService(null, messageTrackerRepository, notificationPublisher),
            "Expected IllegalArgumentException for null EventStore");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullMessageTrackerRepository() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NotificationService(eventStore, null, notificationPublisher),
            "Expected IllegalArgumentException for null MessageTrackerRepository");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullNotificationPublisher() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NotificationService(eventStore, messageTrackerRepository, null),
            "Expected IllegalArgumentException for null NotificationPublisher");
    }

    private void verifyPublishedNotifications(String publicationChannel, int numNotifications) {
        List<Notification> notifications = notificationPublisher.publishedNotifications(publicationChannel);
        Assertions.assertEquals(numNotifications, notifications.size(), "Wrong number of notifications");
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            Assertions.assertEquals(i + 1, notification.storedEventId().longValue(), "Wrong stored event ID");
            Assertions.assertEquals(TestEvent.class.getName(), notification.eventType(), "Wrong event type");
            Assertions.assertEquals(TestEvent.class, notification.event().getClass(), "Wrong type of domain event");
        }
    }
}
