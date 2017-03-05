package com.reallifedeveloper.common.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Utility class with helper methods for JAX-RS resources.
 *
 * @author RealLifeDeveloper
 */
public final class ResourceUtil {

    /**
     * Since this is a utility class with only static methods, we hide the only constructor.
     */
    private ResourceUtil() {
    }

    /**
     * Gives a <code>WebApplicationException</code> with the given HTTP status and the given message as plain
     * text in the response body. If <code>message</code> is <code>null</code>, the entity in the response
     * body will also be <code>null</code>.
     *
     * @param message the message to send back to the client, or <code>null</code>
     * @param status the HTTP status to send back to the client
     *
     * @return a <code>WebApplicationException</code>
     *
     * @throws IllegalArgumentException if <code>status</code> is <code>null</code>
     */
    public static WebApplicationException webApplicationException(String message, Status status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        return new WebApplicationException(Response.status(status).type(MediaType.TEXT_PLAIN).entity(message)
                .build());
    }

    /**
     * Gives a <code>WebApplicationException</code> with the given HTTP status and the given <code>Throwable</code>
     * as plain text in the response body. If <code>cause</code> is <code>null</code>, the entity in the response
     * body will also be <code>null</code>.
     *
     * @param cause the cause of the error, or <code>null</code>
     * @param status the HTTP status to send back to the client
     *
     * @return a <code>WebApplicationException</code>
     *
     * @throws IllegalArgumentException if <code>status</code> is <code>null</code>
     */
    public static WebApplicationException webApplicationException(Throwable cause, Status status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        String message = cause == null ? null : cause.toString();
        return webApplicationException(message, status);
    }

    /**
     * Gives a <code>WebApplicationException</code> with HTTP status 400 and the given message as plain text
     * in the body.
     *
     * @param message the message to send back to the client
     *
     * @return a <code>WebApplicationException</code>
     */
    public static WebApplicationException badRequest(String message) {
        return webApplicationException(message, Status.BAD_REQUEST);
    }

    /**
     * Gives a <code>WebApplicationException</code> with HTTP status 404 and the given message as plain text
     * in the body.
     *
     * @param message the message to send back to the client
     *
     * @return a <code>WebApplicationException</code>
     */
    public static WebApplicationException notFound(String message) {
        return webApplicationException(message, Status.NOT_FOUND);
    }

    /**
     * Gives a <code>WebApplicationException</code> with HTTP status 500 and the given message as plain text
     * in the body.
     *
     * @param message the message to send back to the client
     *
     * @return a <code>WebApplicationException</code>
     */
    public static WebApplicationException serverError(String message) {
        return webApplicationException(message, Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gives a <code>CacheControl</code> object with the <code>max-age</code> directive set to the
     * given value.
     * <p>
     * You would normally use this method like this:
     *
     * <pre>
     *     Response response = Response.ok(representation).cacheControl(cacheControl(10)).build();
     * </pre>
     *
     * @param maxAgeInSeconds the value of the <code>max-age</code> directive
     *
     * @return a <code>CacheControl</code> object with the <code>max-age</code> directive set
     */
    public static CacheControl cacheControl(int maxAgeInSeconds) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAgeInSeconds);
        return cacheControl;
    }

    /**
     * Gives a <code>CacheControl</code> object with directives set to prevent caching.
     * The directives a set as follows:
     * <ul>
     * <li><code>no-cache=true</code>
     * <li><code>no-store=true</code>
     * <li><code>must-revalidate=true</code>
     * </ul>
     *
     * @return a <code>CacheControl</code> object with directives set to prevent caching
     */
    public static CacheControl noCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        cacheControl.setMustRevalidate(true);
        return cacheControl;
    }

}
