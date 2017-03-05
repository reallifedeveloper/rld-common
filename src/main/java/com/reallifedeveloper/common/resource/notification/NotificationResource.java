package com.reallifedeveloper.common.resource.notification;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.reallifedeveloper.common.application.notification.NotificationLog;
import com.reallifedeveloper.common.application.notification.NotificationLogId;
import com.reallifedeveloper.common.application.notification.NotificationService;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.resource.AbstractResource;
import com.reallifedeveloper.common.resource.ResourceUtil;

/**
 * A JAX-RS resource to give access to
 * {@link com.reallifedeveloper.common.application.notification.Notification Notifications} in the form of a
 * {@link NotificationLog}.
 *
 * @author RealLifeDeveloper
 */
@Path("/notifications")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class NotificationResource extends AbstractResource {

    /**
     * The maximum number of notifications returned by {@link #getCurrentNotificationLog(UriInfo)}.
     */
    public static final int BATCH_SIZE = 20;

    private static final int CACHE_1_MINUTE = 60;
    private static final int CACHE_1_HOUR = 60 * 60;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectSerializer<String> objectSerializer;

    @Context
    private HttpHeaders httpHeaders;

    /**
     * Creates a new <code>NotificationResource</code> using the given {@link NotificationService}
     * and {@link ObjectSerializer}.
     *
     * @param notificationService the <code>NotificationService</code> to use
     * @param objectSerializer the <code>ObjectSerializer</code> to use
     */
    public NotificationResource(NotificationService notificationService, ObjectSerializer<String> objectSerializer) {
        if (notificationService == null || objectSerializer == null) {
            throw new IllegalArgumentException("Arguments must not be null: notificationService=" + notificationService
                    + ", objectSerializer=" + objectSerializer);
        }
        this.notificationService = notificationService;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Used by CXF to create a new resource.
     * <p>
     * The objects that this resource depend on are assumed to be injected by Spring.
     */
    NotificationResource() {
        super();
    }

    /**
     * Gives the most recent notifications.
     *
     * @param uriInfo provides access to application and request URI information, injected by JAX-RS
     *
     * @return a <code>Response</code> containing a {@link NotificationLogRepresentation}
     */
    @GET
    public Response getCurrentNotificationLog(@Context UriInfo uriInfo) {
        try {
            logger().debug("getCurrentNotificationLog");
            NotificationLog currentNotificationLog = notificationService.currentNotificationLog(BATCH_SIZE);
            Links links = new Links(currentNotificationLog, uriInfo);
            NotificationLogRepresentation representation = buildRepresentation(currentNotificationLog, links);
            Response response = Response.ok(representation).links(links.links)
                    .cacheControl(ResourceUtil.cacheControl(CACHE_1_MINUTE)).build();
            return response;
        } catch (Exception e) {
            throw handleError("getCurrentNotificationLog", e);
        }
    }

    /**
     * Gives a specific set of notifications.
     * <p>
     * The notifications are identified by their stored event IDs. As an argument, you provide the ID of
     * the first and the last notification you are interested in, in the form "&lt;low&gt;,&lt;high&gt;"
     * where &lt;low&gt; is the ID of the first notification, and &lt;high&gt; is the ID of the last
     * notification.
     * <p>
     * For example, given the string "89661,89680", this method will return the notifications
     * with IDs between 89661 and 89680, inclusive, if available.
     *
     * @param notificationLogIdString a string on the form "&lt;low&gt;,&lt;high&gt;"
     * @param uriInfo provides access to application and request URI information, injected by JAX-RS
     *
     * @return a <code>Response</code> containing a {@link NotificationLogRepresentation}
     */
    @GET
    @Path("{notificationLogId}")
    public Response getNotificationLog(@PathParam("notificationLogId") String notificationLogIdString,
            @Context UriInfo uriInfo) {
        try {
            logger().debug("getNotificationLog: notificationLogIdString={}", notificationLogIdString);
            NotificationLogId notificationLogId = new NotificationLogId(notificationLogIdString);
            NotificationLog notificationLog = notificationService.notificationLog(notificationLogId);
            Links links = new Links(notificationLog, uriInfo);
            NotificationLogRepresentation representation = buildRepresentation(notificationLog, links);
            Response response = Response.ok(representation).links(links.links)
                    .cacheControl(ResourceUtil.cacheControl(CACHE_1_HOUR)).build();
            return response;
        } catch (Exception e) {
            throw handleError("getNotificationLog", e);
        }
    }

    private NotificationLogRepresentation buildRepresentation(NotificationLog notificationLog, Links links) {
        NotificationLogRepresentation representation = new NotificationLogRepresentation(notificationLog,
                objectSerializer);
        representation.setSelf(links.self.getUri().toString());
        if (links.next != null) {
            representation.setNext(links.next.getUri().toString());
        }
        if (links.previous != null) {
            representation.setPrevious(links.previous.getUri().toString());
        }
        return representation;
    }

    private static class Links {

        private final Link self;
        private final Link next;
        private final Link previous;
        private final Link[] links;

        Links(NotificationLog notificationLog, UriInfo uriInfo) {
            UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(NotificationResource.class)
                    .path(NotificationResource.class, "getNotificationLog");
            List<Link> linkList = new ArrayList<>();

            this.self = Link.fromUriBuilder(uriBuilder).rel("self")
                    .build(notificationLog.currentNotificationLogId().externalForm());
            linkList.add(this.self);

            if (notificationLog.nextNotificationLogId() == null) {
                this.next = null;
            } else {
                this.next = Link.fromUriBuilder(uriBuilder).rel("next")
                        .build(notificationLog.nextNotificationLogId().externalForm());
                linkList.add(this.next);
            }

            if (notificationLog.previousNotificationLogId() == null) {
                this.previous = null;
            } else {
                this.previous = Link.fromUriBuilder(uriBuilder).rel("previous")
                        .build(notificationLog.previousNotificationLogId().externalForm());
                linkList.add(this.previous);
            }

            this.links = new Link[linkList.size()];
            linkList.toArray(this.links);
        }
    }
}
