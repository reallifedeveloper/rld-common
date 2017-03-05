package com.reallifedeveloper.common.application.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NotificationPublisherTaskTest {

    private static final String PUBLICATION_CHANNEL = "foo";

    @Test
    public void run() {
        TestNotificationService notificationService = new TestNotificationService();
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL, notificationService);
        Assert.assertEquals("Wrong number of calls to publishNotifications", 0,
                notificationService.publicationChannelsUsed.size());
        task.run();
        Assert.assertEquals("Wrong number of calls to publishNotifications", 1,
                notificationService.publicationChannelsUsed.size());
        Assert.assertEquals("Wrong publication channel: ", PUBLICATION_CHANNEL,
                notificationService.publicationChannelsUsed.get(0));
        task.run();
        Assert.assertEquals("Wrong number of calls to publishNotifications", 2,
                notificationService.publicationChannelsUsed.size());
        Assert.assertEquals("Wrong publication channel: ", PUBLICATION_CHANNEL,
                notificationService.publicationChannelsUsed.get(0));
        Assert.assertEquals("Wrong publication channel: ", PUBLICATION_CHANNEL,
                notificationService.publicationChannelsUsed.get(1));
    }

    @Test
    public void runWithIOException() {
        TestNotificationService notificationService = new TestNotificationService();
        notificationService.setIoException(new IOException("bar"));
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL, notificationService);
        task.run();
        Assert.assertEquals("Wrong number of calls to publishNotifications", 1,
                notificationService.publicationChannelsUsed.size());
    }

    @Test(expected = NullPointerException.class)
    public void runWithNullPointerException() {
        TestNotificationService notificationService = new TestNotificationService();
        notificationService.setRuntimeException(new NullPointerException("baz"));
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL, notificationService);
        task.run();
    }

    @Test(expected = NullPointerException.class)
    public void singleArgumentConstructor() {
        NotificationPublisherTask task = new NotificationPublisherTask(PUBLICATION_CHANNEL);
        task.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorPublicationChannelNull() {
        TestNotificationService notificationService = new TestNotificationService();
        new NotificationPublisherTask(null, notificationService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNotificationServiceNull() {
        new NotificationPublisherTask(PUBLICATION_CHANNEL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void singleArgumentConstructorPublicationChannelNull() {
        new NotificationPublisherTask(null);
    }

    private static class TestNotificationService extends NotificationService {

        private IOException ioException;
        private RuntimeException runtimeException;
        private List<String> publicationChannelsUsed = new ArrayList<>();

        public void setIoException(IOException ioException) {
            this.ioException = ioException;
        }

        public void setRuntimeException(RuntimeException runtimeException) {
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
