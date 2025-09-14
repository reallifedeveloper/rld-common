package com.reallifedeveloper.common.application.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationPublisherTaskTest {

    private static final String PUBLICATION_CHANNEL = "foo";

    @Test
    public void run() {
        TestNotificationService notificationService = new TestNotificationService();
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL, notificationService);
        assertEquals(0, notificationService.publicationChannelsUsed.size(), "Wrong number of calls to publishNotifications");
        task.run();
        assertEquals(1, notificationService.publicationChannelsUsed.size(), "Wrong number of calls to publishNotifications");
        assertEquals(PUBLICATION_CHANNEL, notificationService.publicationChannelsUsed.get(0), "Wrong publication channel");
        task.run();
        assertEquals(2, notificationService.publicationChannelsUsed.size(), "Wrong number of calls to publishNotifications");
        assertEquals(PUBLICATION_CHANNEL, notificationService.publicationChannelsUsed.get(0), "Wrong publication channel");
        assertEquals(PUBLICATION_CHANNEL, notificationService.publicationChannelsUsed.get(1), "Wrong publication channel");
    }

    @Test
    public void runWithIOException() {
        TestNotificationService notificationService = new TestNotificationService();
        notificationService.setIoException(new IOException("bar"));
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL, notificationService);
        task.run();
        assertEquals(1, notificationService.publicationChannelsUsed.size(), "Wrong number of calls to publishNotifications");
    }

    @Test
    public void runWithNullPointerException() {
        TestNotificationService notificationService = new TestNotificationService();
        notificationService.setRuntimeException(new NullPointerException("baz"));
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL, notificationService);
        assertThrows(NullPointerException.class, task::run, "Expected NullPointerException to be thrown");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorPublicationChannelNull() {
        TestNotificationService notificationService = new TestNotificationService();
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationPublisherTask(null, notificationService),
                "Expected IllegalArgumentException for null publication channel");
        assertEquals("Arguments must not be null: publicationChannel=null, notificationService=" + notificationService, e.getMessage());
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNotificationServiceNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new NotificationPublisherTask(PUBLICATION_CHANNEL, null),
                "Expected IllegalArgumentException for null notification service");
        assertEquals("Arguments must not be null: publicationChannel=" + PUBLICATION_CHANNEL + ", notificationService=null",
                e.getMessage());

    }

    private static class TestNotificationService extends NotificationService {

        private @Nullable IOException ioException;
        private @Nullable RuntimeException runtimeException;
        private List<String> publicationChannelsUsed = new ArrayList<>();

        TestNotificationService() {
            super(new EventStore(new GsonObjectSerializer(), new InMemoryStoredEventRepository()),
                    new InMemoryPublishedMessageTrackerRepository(), new TestNotificationPublisher());
        }

        void setIoException(IOException ioException) {
            this.ioException = ioException;
        }

        void setRuntimeException(RuntimeException runtimeException) {
            this.runtimeException = runtimeException;
        }

        @Override
        public void publishNotifications(String publicationChannel) throws IOException {
            publicationChannelsUsed.add(publicationChannel);
            if (ioException != null) {
                throw ioException;
            }
            if (runtimeException != null) {
                throw runtimeException;
            }
        }
    }
}
