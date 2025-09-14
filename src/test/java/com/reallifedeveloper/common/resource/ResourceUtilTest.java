package com.reallifedeveloper.common.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

public class ResourceUtilTest {

    @Test
    public void webApplicationExceptionString() {
        WebApplicationException e = ResourceUtil.webApplicationException("foo", Status.BAD_REQUEST);
        verifyException(e, "foo", Status.BAD_REQUEST);
    }

    @Test
    public void webApplicationExceptionNullString() {
        WebApplicationException e = ResourceUtil.webApplicationException((String) null, Status.BAD_REQUEST);
        verifyException(e, null, Status.BAD_REQUEST);
    }

    @Test
    @SuppressWarnings("NullAway")
    public void webApplicationExceptionStringNullStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            ResourceUtil.webApplicationException("foo", null);
        });
    }

    @Test
    public void webApplicationExceptionThrowable() {
        Throwable t = new IllegalArgumentException("foo");
        WebApplicationException e = ResourceUtil.webApplicationException(t, Status.BAD_REQUEST);
        verifyException(e, t.toString(), Status.BAD_REQUEST);
    }

    @Test
    public void webApplicationExceptionNullThrowable() {
        WebApplicationException e = ResourceUtil.webApplicationException((Throwable) null, Status.BAD_REQUEST);
        verifyException(e, null, Status.BAD_REQUEST);
    }

    @Test
    @SuppressWarnings("NullAway")
    public void webApplicationExceptionThrowableNullStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            ResourceUtil.webApplicationException(new IllegalArgumentException(), null);
        });
    }

    @Test
    public void badRequest() {
        WebApplicationException e = ResourceUtil.badRequest("foo");
        verifyException(e, "foo", Status.BAD_REQUEST);
    }

    @Test
    public void badRequestNull() {
        WebApplicationException e = ResourceUtil.badRequest(null);
        verifyException(e, null, Status.BAD_REQUEST);
    }

    @Test
    public void notFound() {
        WebApplicationException e = ResourceUtil.notFound("foo");
        verifyException(e, "foo", Status.NOT_FOUND);
    }

    @Test
    public void notFoundNull() {
        WebApplicationException e = ResourceUtil.notFound(null);
        verifyException(e, null, Status.NOT_FOUND);
    }

    @Test
    public void serverError() {
        WebApplicationException e = ResourceUtil.serverError("foo");
        verifyException(e, "foo", Status.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void serverErrorNull() {
        WebApplicationException e = ResourceUtil.serverError(null);
        verifyException(e, null, Status.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void cacheControl() {
        CacheControl cacheControl = ResourceUtil.cacheControl(42);
        assertEquals(42, cacheControl.getMaxAge(), "Wrong max age: ");
    }

    @Test
    public void noCache() {
        CacheControl cacheControl = ResourceUtil.noCache();
        assertEquals(-1, cacheControl.getMaxAge(), "Wrong max age: ");
        assertTrue(cacheControl.isNoCache(), "No-cache should be true");
        assertTrue(cacheControl.isNoStore(), "No-store should be true");
        assertTrue(cacheControl.isMustRevalidate(), "Must-revalidate should be true");
    }

    private void verifyException(WebApplicationException e, @Nullable String entity, Status status) {
        assertNotNull(e, "Exception should not be null");
        assertEquals(entity, e.getResponse().getEntity(), "Wrong entity: ");
        assertEquals(status.getStatusCode(), e.getResponse().getStatus(), "Wrong status: ");
        assertEquals(MediaType.TEXT_PLAIN_TYPE, e.getResponse().getMediaType(), "Wrong content type: ");
    }
}
