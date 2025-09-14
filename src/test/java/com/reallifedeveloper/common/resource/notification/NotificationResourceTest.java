package com.reallifedeveloper.common.resource.notification;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.application.notification.InMemoryPublishedMessageTrackerRepository;
import com.reallifedeveloper.common.application.notification.NotificationLog;
import com.reallifedeveloper.common.application.notification.NotificationLogId;
import com.reallifedeveloper.common.application.notification.NotificationPublisher;
import com.reallifedeveloper.common.application.notification.NotificationService;
import com.reallifedeveloper.common.application.notification.PublishedMessageTrackerRepository;
import com.reallifedeveloper.common.application.notification.TestNotificationPublisher;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class NotificationResourceTest {

    private static final String ENDPOINT_ADDRESS = "http://localhost/api";

    private InMemoryStoredEventRepository storedEventRepository = new InMemoryStoredEventRepository();
    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
    private EventStore eventStore = new EventStore(objectSerializer, storedEventRepository);
    private InMemoryPublishedMessageTrackerRepository messageTrackerRepository = new InMemoryPublishedMessageTrackerRepository();
    private TestNotificationPublisher notificationPublisher = new TestNotificationPublisher();
    private NotificationService notificationService = new NotificationService(eventStore, messageTrackerRepository, notificationPublisher);
    private NotificationResource resource = new NotificationResource(notificationService, objectSerializer);

    private UriInfo uriInfo;

    @BeforeEach
    public void init() {
        MessageImpl message = new MessageImpl();
        message.setExchange(new ExchangeImpl());
        message.put(Message.ENDPOINT_ADDRESS, ENDPOINT_ADDRESS);
        uriInfo = new UriInfoImpl(message);
    }

    @Test
    public void getCurrentNotificationLogFullBatch() {
        for (int i = 0; i < NotificationResource.BATCH_SIZE; i++) {
            TestEvent event = new TestEvent(i + 1, "foo" + (i + 1));
            eventStore.add(event);
        }
        Response response = resource.getCurrentNotificationLog(uriInfo);
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assertions.assertEquals(1, links.size(), "Wrong number of links:");
        Optional<Link> self = getLink("self", links);
        Assertions.assertTrue(self.isPresent(), "Links should contain self link");
        Assertions.assertEquals("self", self.get().getRel(), "Wrong self link rel:");
        Assertions.assertEquals(selfUrl, self.get().getUri().toString(), "Wrong self link URL:");
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assertions.assertEquals(NotificationResource.BATCH_SIZE, representation.notifications().size(), "Wrong number of notifications:");
        Assertions.assertTrue(representation.isArchived(), "Notification log should be archived");
    }

    @Test
    public void getCurrentNotificationLogFullBatchPlusOne() {
        for (int i = 0; i < NotificationResource.BATCH_SIZE + 1; i++) {
            TestEvent event = new TestEvent(i + 1, "foo" + (i + 1));
            eventStore.add(event);
        }
        Response response = resource.getCurrentNotificationLog(uriInfo);
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/" + (NotificationResource.BATCH_SIZE + 1) + ","
                + NotificationResource.BATCH_SIZE * 2;
        String previousUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assertions.assertEquals(2, links.size(), "Wrong number of links:");
        Optional<Link> self = getLink("self", links);
        Assertions.assertTrue(self.isPresent(), "Links should contain self link");
        Assertions.assertEquals(selfUrl, self.get().getUri().toString(), "Wrong self link URL:");
        Optional<Link> previous = getLink("previous", links);
        Assertions.assertTrue(previous.isPresent(), "Links should contain previous link");
        Assertions.assertEquals(previousUrl, previous.get().getUri().toString(), "Wrong previous link URL:");
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assertions.assertEquals(1, representation.notifications().size(), "Wrong number of notifications:");
        Assertions.assertFalse(representation.isArchived(), "Notification log should not be archived");
    }

    @Test
    public void getCurrentNotificationLogNoNotifications() {
        Response response = resource.getCurrentNotificationLog(uriInfo);
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assertions.assertEquals(1, links.size(), "Wrong number of links:");
        Optional<Link> self = getLink("self", links);
        Assertions.assertTrue(self.isPresent(), "Links should contain self link");
        Assertions.assertEquals(selfUrl, self.get().getUri().toString(), "Wrong self link URL:");
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assertions.assertTrue(representation.notifications().isEmpty(), "Notifications should be empty");
        Assertions.assertFalse(representation.isArchived(), "Notification log should not be archived");
    }

    @Test
    public void getCurrentNotificationLogServiceThrowsIllegalArgumentException() {
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> failingResource(new IllegalArgumentException()).getCurrentNotificationLog(uriInfo));
        Assertions.assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code:");
    }

    @Test
    public void getCurrentNotificationLogServiceThrowsNullPointerException() {
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> failingResource(new NullPointerException()).getCurrentNotificationLog(uriInfo));
        Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus(),
                "Wrong HTTP status code:");
    }

    @Test
    public void getNotificationLog() {
        int numEvents = NotificationResource.BATCH_SIZE * 2 + 3;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int firstEventToGet = NotificationResource.BATCH_SIZE + 1;
        String notificationLogIdString = firstEventToGet + "," + (firstEventToGet + NotificationResource.BATCH_SIZE - 1);
        Response response = resource.getNotificationLog(notificationLogIdString, uriInfo);
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/" + firstEventToGet + ","
                + (firstEventToGet + NotificationResource.BATCH_SIZE - 1);
        String nextUrl = ENDPOINT_ADDRESS + "/notifications/" + (firstEventToGet + NotificationResource.BATCH_SIZE) + ","
                + (firstEventToGet + NotificationResource.BATCH_SIZE * 2 - 1);
        String previousUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assertions.assertEquals(3, links.size(), "Wrong number of links:");
        Optional<Link> self = getLink("self", links);
        Assertions.assertTrue(self.isPresent(), "Links should contain self link");
        Assertions.assertEquals(selfUrl, self.get().getUri().toString(), "Wrong self link URL:");
        Optional<Link> next = getLink("next", links);
        Assertions.assertTrue(next.isPresent(), "Links should contain next link");
        Assertions.assertEquals(nextUrl, next.get().getUri().toString(), "Wrong next link URL:");
        Optional<Link> previous = getLink("previous", links);
        Assertions.assertTrue(previous.isPresent(), "Links should contain previous link");
        Assertions.assertEquals(previousUrl, previous.get().getUri().toString(), "Wrong previous link URL:");
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assertions.assertEquals(NotificationResource.BATCH_SIZE, representation.notifications().size(), "Wrong number of notifications:");
        Assertions.assertTrue(representation.isArchived(), "Notification log should be archived");
    }

    @Test
    public void getNotificationLogMalformedNotificationLogString() {
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> resource.getNotificationLog("foo", uriInfo));
        Assertions.assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code:");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void getNotificationLogNullNotificationLogString() {
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> resource.getNotificationLog(null, uriInfo));
        Assertions.assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code:");
    }

    @Test
    public void getNotificationLogServiceThrowsIllegalArgumentException() {
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> failingResource(new IllegalArgumentException()).getNotificationLog("1,20", uriInfo));
        Assertions.assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code:");
    }

    @Test
    public void getNotificationLogServiceThrowsNullPointerException() {
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> failingResource(new NullPointerException()).getNotificationLog("1,20", uriInfo));
        Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus(),
                "Wrong HTTP status code:");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullNotificationService() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotificationResource(null, objectSerializer));
    }

    @Test
    @SuppressWarnings("NullAway")
    public void constructorNullObjectSerializer() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotificationResource(notificationService, null));
    }

    private Optional<Link> getLink(String rel, Set<Link> links) {
        return links.stream().filter(l -> l.getRel().equals(rel)).findFirst();
    }

    private NotificationResource failingResource(RuntimeException exceptionToThrow) {
        NotificationService failingService = new FailingNotificationService(eventStore, messageTrackerRepository, notificationPublisher,
                exceptionToThrow);
        return new NotificationResource(failingService, objectSerializer);
    }

    private static final class FailingNotificationService extends NotificationService {

        private RuntimeException exceptionToThrow;

        FailingNotificationService(EventStore eventStore, PublishedMessageTrackerRepository messageTrackerRepository,
                NotificationPublisher notificationPublisher, RuntimeException exceptionToThrow) {
            super(eventStore, messageTrackerRepository, notificationPublisher);
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        public NotificationLog currentNotificationLog(int batchSize) {
            throw exceptionToThrow;
        }

        @Override
        public NotificationLog notificationLog(NotificationLogId notificationLogId) {
            throw exceptionToThrow;
        }

        @Override
        public void publishNotifications(String publicationChannel) throws IOException {
            throw exceptionToThrow;
        }

    }
}
