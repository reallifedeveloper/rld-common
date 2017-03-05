package com.reallifedeveloper.common.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

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

    @Test(expected = IllegalArgumentException.class)
    public void webApplicationExceptionStringNullStatus() {
        ResourceUtil.webApplicationException("foo", null);
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

    @Test(expected = IllegalArgumentException.class)
    public void webApplicationExceptionThrowableNullStatus() {
        ResourceUtil.webApplicationException(new IllegalArgumentException(), null);
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
        Assert.assertEquals("Wrong max age: ", 42, cacheControl.getMaxAge());
    }

    @Test
    public void noCache() {
        CacheControl cacheControl = ResourceUtil.noCache();
        Assert.assertEquals("Wrong max age: ", -1, cacheControl.getMaxAge());
        Assert.assertTrue("No-cache should be true", cacheControl.isNoCache());
        Assert.assertTrue("No-store should be true", cacheControl.isNoStore());
        Assert.assertTrue("Must-revalidate should be true", cacheControl.isMustRevalidate());
    }

    private void verifyException(WebApplicationException e, String entity, Status status) {
        Assert.assertNotNull("Exception should not be null", e);
        Assert.assertEquals("Wrong entity: ", entity, e.getResponse().getEntity());
        Assert.assertEquals("Wrong status: ", status.getStatusCode(), e.getResponse().getStatus());
        Assert.assertEquals("Wrong content type: ", MediaType.TEXT_PLAIN_TYPE, e.getResponse().getMediaType());
    }
}
