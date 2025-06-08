package com.reallifedeveloper.common.resource;

import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

import com.reallifedeveloper.common.domain.ErrorHandling;

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
     * Gives a {@code WebApplicationException} with the given HTTP status and the given message as plain text in the response body. If
     * {@code message} is {@code null}, the entity in the response body will also be {@code null}.
     *
     * @param message the message to send back to the client, or {@code null}
     * @param status  the HTTP status to send back to the client
     *
     * @return a {@code WebApplicationException}
     *
     * @throws IllegalArgumentException if {@code status} is {@code null}
     */
    public static WebApplicationException webApplicationException(@Nullable String message, Status status) {
        ErrorHandling.checkNull("status must not be null", status);
        ResponseBuilder responseBuilder = Response.status(status).type(MediaType.TEXT_PLAIN);
        if (message != null) {
            responseBuilder.entity(message);
        }
        return new WebApplicationException(responseBuilder.build());
    }

    /**
     * Gives a {@code WebApplicationException} with the given HTTP status and the given {@code Throwable} as plain text in the response
     * body. If {@code cause} is {@code null}, the entity in the response body will also be {@code null}.
     *
     * @param cause  the cause of the error, or {@code null}
     * @param status the HTTP status to send back to the client
     *
     * @return a {@code WebApplicationException}
     *
     * @throws IllegalArgumentException if {@code status} is {@code null}
     */
    public static WebApplicationException webApplicationException(@Nullable Throwable cause, Status status) {
        ErrorHandling.checkNull("status must not be null", status);
        String message = cause == null ? null : cause.toString();
        return webApplicationException(message, status);
    }

    /**
     * Gives a {@code WebApplicationException} with HTTP status 400 and the given message as plain text in the body.
     *
     * @param message the message to send back to the client
     *
     * @return a {@code WebApplicationException}
     */
    public static WebApplicationException badRequest(@Nullable String message) {
        return webApplicationException(message, Status.BAD_REQUEST);
    }

    /**
     * Gives a {@code WebApplicationException} with HTTP status 404 and the given message as plain text in the body.
     *
     * @param message the message to send back to the client
     *
     * @return a {@code WebApplicationException}
     */
    public static WebApplicationException notFound(@Nullable String message) {
        return webApplicationException(message, Status.NOT_FOUND);
    }

    /**
     * Gives a {@code WebApplicationException} with HTTP status 500 and the given message as plain text in the body.
     *
     * @param message the message to send back to the client
     *
     * @return a {@code WebApplicationException}
     */
    public static WebApplicationException serverError(@Nullable String message) {
        return webApplicationException(message, Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gives a {@code CacheControl} object with the {@code max-age} directive set to the given value.
     * <p>
     * You would normally use this method like this:
     *
     * <pre>
     * Response response = Response.ok(representation).cacheControl(cacheControl(10)).build();
     * </pre>
     *
     * @param maxAgeInSeconds the value of the {@code max-age} directive
     *
     * @return a {@code CacheControl} object with the {@code max-age} directive set
     */
    public static CacheControl cacheControl(int maxAgeInSeconds) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAgeInSeconds);
        return cacheControl;
    }

    /**
     * Gives a {@code CacheControl} object with directives set to prevent caching. The directives a set as follows:
     * <ul>
     * <li>{@code no-cache=true}
     * <li>{@code no-store=true}
     * <li>{@code must-revalidate=true}
     * </ul>
     *
     * @return a {@code CacheControl} object with directives set to prevent caching
     */
    public static CacheControl noCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        cacheControl.setMustRevalidate(true);
        return cacheControl;
    }

}
