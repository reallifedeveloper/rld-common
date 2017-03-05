package com.reallifedeveloper.common.resource.notification;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    private InMemoryPublishedMessageTrackerRepository messageTrackerRepository =
            new InMemoryPublishedMessageTrackerRepository();
    private TestNotificationPublisher notificationPublisher = new TestNotificationPublisher();
    private NotificationService notificationService =
            new NotificationService(eventStore, messageTrackerRepository, notificationPublisher);
    private NotificationResource resource = new NotificationResource(notificationService, objectSerializer);

    private UriInfo uriInfo;

    @Before
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
        // The event store contains events for exactly one full batch of notifications,
        // so the current notification log should be the first (so there is no previous link)
        // and the last (archived and no next link).
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assert.assertEquals("Wrong number of links: ", 1, links.size());
        Link self = getLink("self", links);
        Assert.assertNotNull("Links should contain self link", self);
        Assert.assertEquals("Wrong self link rel: ", "self", self.getRel());
        Assert.assertEquals("Wrong self link URL: ", selfUrl, self.getUri().toString());
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assert.assertEquals("Wrong number of notifications: ", NotificationResource.BATCH_SIZE,
                representation.notifications().size());
        Assert.assertTrue("Notification log should be archived", representation.isArchived());
    }

    @Test
    public void getCurrentNotificationLogFullBatchPlusOne() {
        for (int i = 0; i < NotificationResource.BATCH_SIZE + 1; i++) {
            TestEvent event = new TestEvent(i + 1, "foo" + (i + 1));
            eventStore.add(event);
        }
        Response response = resource.getCurrentNotificationLog(uriInfo);
        // The event store contains events for one full batch of notifications,
        // plus one more, so the current notification log should be the second (there is a previous link)
        // and the last (archived and no next link).
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/" + (NotificationResource.BATCH_SIZE + 1) + ","
                + NotificationResource.BATCH_SIZE * 2;
        String previousUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assert.assertEquals("Wrong number of links: ", 2, links.size());
        Link self = getLink("self", links);
        Assert.assertNotNull("Links should contain self link", self);
        Assert.assertEquals("Wrong self link URL: ", selfUrl, self.getUri().toString());
        Link previous = getLink("previous", links);
        Assert.assertNotNull("Links should contain previous link", previous);
        Assert.assertEquals("Wrong previous link URL: ", previousUrl, previous.getUri().toString());
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assert.assertEquals("Wrong number of notifications: ", 1, representation.notifications().size());
        Assert.assertFalse("Notification log should not be archived", representation.isArchived());
    }

    @Test
    public void getCurrentNotificationLogNoNotifications() {
        Response response = resource.getCurrentNotificationLog(uriInfo);
        // The event store contains no events, so the current (empty) notification log should be
        // the first (so there is no previous link) and the last (no next link), but not archived.
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assert.assertEquals("Wrong number of links: ", 1, links.size());
        Link self = getLink("self", links);
        Assert.assertNotNull("Links should contain self link", self);
        Assert.assertEquals("Wrong self link URL: ", selfUrl, self.getUri().toString());
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assert.assertTrue("Notifications should be empty", representation.notifications().isEmpty());
        Assert.assertFalse("Notification log should not be archived", representation.isArchived());
    }

    @Test
    public void getCurrentNotificationLogServiceThrowsIllegalArgumentException() {
        try {
            failingResource(new IllegalArgumentException()).getCurrentNotificationLog(uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.BAD_REQUEST.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getCurrentNotificationLogServiceThrowsNullPointerException() {
        try {
            failingResource(new NullPointerException()).getCurrentNotificationLog(uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getCurrentNotificationLogResourceNotInitialized() {
        try {
            new NotificationResource().getCurrentNotificationLog(uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getNotificationLog() {
        int numEvents = NotificationResource.BATCH_SIZE * 2 + 3;
        for (int i = 0; i < numEvents; i++) {
            eventStore.add(new TestEvent(i + 1, "foo" + (i + 1)));
        }
        int firstEventToGet = NotificationResource.BATCH_SIZE + 1;
        String notificationLogIdString =
                firstEventToGet + "," + (firstEventToGet + NotificationResource.BATCH_SIZE - 1);
        Response response = resource.getNotificationLog(notificationLogIdString, uriInfo);
        // The notification log is in the middle of the events, so there should be both a previous
        // and a next link, and the notification log should be archived.
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/" + firstEventToGet + ","
                + (firstEventToGet + NotificationResource.BATCH_SIZE - 1);
        String nextUrl = ENDPOINT_ADDRESS + "/notifications/" + (firstEventToGet + NotificationResource.BATCH_SIZE)
                + "," + (firstEventToGet + NotificationResource.BATCH_SIZE * 2 - 1);
        String previousUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assert.assertEquals("Wrong number of links: ", 3, links.size());
        Link self = getLink("self", links);
        Assert.assertNotNull("Links should contain self link", self);
        Assert.assertEquals("Wrong self link URL: ", selfUrl, self.getUri().toString());
        Link next = getLink("next", links);
        Assert.assertNotNull("Links should contain next link", next);
        Assert.assertEquals("Wrong next link URL: ", nextUrl, next.getUri().toString());
        Link previous = getLink("previous", links);
        Assert.assertNotNull("Links should contain previous link", previous);
        Assert.assertEquals("Wrong previous link URL: ", previousUrl, previous.getUri().toString());
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assert.assertEquals("Wrong number of notifications: ", NotificationResource.BATCH_SIZE,
                representation.notifications().size());
        Assert.assertTrue("Notification log should be archived", representation.isArchived());
    }

    @Test
    public void getNotificationLogNoNotifications() {
        int firstEventToGet = NotificationResource.BATCH_SIZE + 1;
        String notificationLogIdString =
                firstEventToGet + "," + (firstEventToGet + NotificationResource.BATCH_SIZE - 1);
        Response response = resource.getNotificationLog(notificationLogIdString, uriInfo);
        // The notification log is in the middle of an empty set of events, so there should be a
        // previous link but not next link, and the notification log should not be archived.
        String selfUrl = ENDPOINT_ADDRESS + "/notifications/" + firstEventToGet + ","
                + (firstEventToGet + NotificationResource.BATCH_SIZE - 1);
        String previousUrl = ENDPOINT_ADDRESS + "/notifications/1," + NotificationResource.BATCH_SIZE;
        Set<Link> links = response.getLinks();
        Assert.assertEquals("Wrong number of links: ", 2, links.size());
        Link self = getLink("self", links);
        Assert.assertNotNull("Links should contain self link", self);
        Assert.assertEquals("Wrong self link URL: ", selfUrl, self.getUri().toString());
        Link previous = getLink("previous", links);
        Assert.assertNotNull("Links should contain previous link", previous);
        Assert.assertEquals("Wrong previous link URL: ", previousUrl, previous.getUri().toString());
        NotificationLogRepresentation representation = (NotificationLogRepresentation) response.getEntity();
        Assert.assertTrue("Notifications should be empty", representation.notifications().isEmpty());
        Assert.assertFalse("Notification log should not be archived", representation.isArchived());
    }

    @Test
    public void getNotificationLogMalformedNotificationLogString() {
        try {
            resource.getNotificationLog("foo", uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.BAD_REQUEST.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getNotificationLogNullNotificationLogString() {
        try {
            resource.getNotificationLog(null, uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.BAD_REQUEST.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getNotificationLogServiceThrowsIllegalArgumentException() {
        try {
            failingResource(new IllegalArgumentException()).getNotificationLog("1,20", uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.BAD_REQUEST.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getNotificationLogServiceThrowsNullPointerException() {
        try {
            failingResource(new NullPointerException()).getNotificationLog("1,20", uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void getNotificationLogResourceNotInitialized() {
        try {
            new NotificationResource().getNotificationLog("1,20", uriInfo);
            Assert.fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong HTTP status code: ", Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullNotificationService() {
        new NotificationResource(null, objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullObjectSerializer() {
        new NotificationResource(notificationService, null);
    }

    private Link getLink(String rel, Set<Link> links) {
        for (Link link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }

    private NotificationResource failingResource(RuntimeException exceptionToThrow) {
        NotificationService failingService = new FailingNotificationService(eventStore, messageTrackerRepository,
                notificationPublisher, exceptionToThrow);
        NotificationResource failingResource = new NotificationResource(failingService, objectSerializer);
        return failingResource;
    }

    private static final class FailingNotificationService extends NotificationService {

        private RuntimeException exceptionToThrow;

        FailingNotificationService(EventStore eventStore,
                PublishedMessageTrackerRepository messageTrackerRepository,
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
