package com.reallifedeveloper.common.resource.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import com.reallifedeveloper.common.application.notification.NotificationLog;
import com.reallifedeveloper.common.application.notification.NotificationLogId;
import com.reallifedeveloper.common.application.notification.NotificationService;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.resource.BaseResource;
import com.reallifedeveloper.common.resource.ResourceUtil;

/**
 * A JAX-RS resource to give access to {@link com.reallifedeveloper.common.application.notification.Notification Notifications} in the form
 * of a {@link NotificationLog}.
 *
 * @author RealLifeDeveloper
 */
@Path("/notifications")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public final class NotificationResource extends BaseResource {

    /**
     * The maximum number of notifications returned by {@link #getCurrentNotificationLog(UriInfo)}.
     */
    public static final int BATCH_SIZE = 20;

    private static final int CACHE_1_MINUTE = 60;
    private static final int CACHE_1_HOUR = 60 * 60;

    private final NotificationService notificationService;

    private final ObjectSerializer<String> objectSerializer;

    /**
     * Creates a new {@code NotificationResource} using the given {@link NotificationService} and {@link ObjectSerializer}.
     *
     * @param notificationService the {@code NotificationService} to use
     * @param objectSerializer    the {@code ObjectSerializer} to use
     */
    public NotificationResource(NotificationService notificationService, ObjectSerializer<String> objectSerializer) {
        if (notificationService == null || objectSerializer == null) {
            throw new IllegalArgumentException(
                    "Arguments must not be null: notificationService=" + notificationService + ", objectSerializer=" + objectSerializer);
        }
        this.notificationService = notificationService;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Gives the most recent notifications.
     *
     * @param uriInfo provides access to application and request URI information, injected by JAX-RS
     *
     * @return a {@code Response} containing a {@link NotificationLogRepresentation}
     */
    @GET
    public Response getCurrentNotificationLog(@Context UriInfo uriInfo) {
        try {
            logger().debug("getCurrentNotificationLog");
            NotificationLog currentNotificationLog = notificationService.currentNotificationLog(BATCH_SIZE);
            Links links = new Links(currentNotificationLog, uriInfo);
            NotificationLogRepresentation representation = buildRepresentation(currentNotificationLog, links);
            return Response.ok(representation).links(links.allLinks).cacheControl(ResourceUtil.cacheControl(CACHE_1_MINUTE)).build();
        } catch (Exception e) {
            throw handleError("getCurrentNotificationLog", e);
        }
    }

    /**
     * Gives a specific set of notifications.
     * <p>
     * The notifications are identified by their stored event IDs. As an argument, you provide the ID of the first and the last notification
     * you are interested in, in the form "&lt;low&gt;,&lt;high&gt;" where &lt;low&gt; is the ID of the first notification, and &lt;high&gt;
     * is the ID of the last notification.
     * <p>
     * For example, given the string "89661,89680", this method will return the notifications with IDs between 89661 and 89680, inclusive,
     * if available.
     *
     * @param notificationLogIdString a string on the form "&lt;low&gt;,&lt;high&gt;"
     * @param uriInfo                 provides access to application and request URI information, injected by JAX-RS
     *
     * @return a {@code Response} containing a {@link NotificationLogRepresentation}
     */
    @GET
    @Path("{notificationLogId}")
    public Response getNotificationLog(@PathParam("notificationLogId") String notificationLogIdString, @Context UriInfo uriInfo) {
        try {
            logger().debug("getNotificationLog: notificationLogIdString={}", notificationLogIdString);
            NotificationLogId notificationLogId = new NotificationLogId(notificationLogIdString);
            NotificationLog notificationLog = notificationService.notificationLog(notificationLogId);
            Links links = new Links(notificationLog, uriInfo);
            NotificationLogRepresentation representation = buildRepresentation(notificationLog, links);
            return Response.ok(representation).links(links.allLinks).cacheControl(ResourceUtil.cacheControl(CACHE_1_HOUR)).build();
        } catch (Exception e) {
            throw handleError("getNotificationLog", e);
        }
    }

    private NotificationLogRepresentation buildRepresentation(NotificationLog notificationLog, Links links) {
        NotificationLogRepresentation representation = new NotificationLogRepresentation(notificationLog, objectSerializer);
        representation.setSelf(links.self.getUri().toString());
        if (links.next.isPresent()) {
            representation.setNext(links.next.get().getUri().toString());
        }
        if (links.previous.isPresent()) {
            representation.setPrevious(links.previous.get().getUri().toString());
        }
        return representation;
    }

    private static class Links {

        private final Link self;
        private final Optional<Link> next;
        private final Optional<Link> previous;
        private final Link[] allLinks;

        /* package-private */ Links(NotificationLog notificationLog, UriInfo uriInfo) {
            UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(NotificationResource.class).path(NotificationResource.class,
                    "getNotificationLog");
            List<Link> linkList = new ArrayList<>();

            this.self = Link.fromUriBuilder(uriBuilder).rel("self").build(notificationLog.current().externalForm());
            linkList.add(this.self);

            this.next = notificationLog.next().map(link -> Link.fromUriBuilder(uriBuilder).rel("next").build(link.externalForm()));
            this.next.ifPresent(linkList::add);

            this.previous = notificationLog.previous()
                    .map(link -> Link.fromUriBuilder(uriBuilder).rel("previous").build(link.externalForm()));
            this.previous.ifPresent(linkList::add);

            this.allLinks = new Link[linkList.size()];
            linkList.toArray(this.allLinks);
        }
    }
}
