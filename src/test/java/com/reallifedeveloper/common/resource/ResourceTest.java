package com.reallifedeveloper.common.resource;

import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

public class ResourceTest {

    private TestResource resource = new TestResource();

    @Test(expected = IllegalArgumentException.class)
    public void handleErrorMethodNameNull() {
        resource.handleError(null, new NullPointerException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleErrorOriginalExceptionNull() {
        resource.handleError("foo", null);
    }

    @Test
    public void handleErrorIllegalArgumentException() {
        WebApplicationException exception = resource.handleError("foo", new IllegalArgumentException());
        Assert.assertEquals("Wrong HTTP status code: ", Status.BAD_REQUEST.getStatusCode(),
                exception.getResponse().getStatus());
    }

    @Test
    public void handleErrorFileNotFoundException() {
        WebApplicationException exception = resource.handleError("foo", new FileNotFoundException());
        Assert.assertEquals("Wrong HTTP status code: ", Status.NOT_FOUND.getStatusCode(),
                exception.getResponse().getStatus());
    }

    @Test
    public void handleErrorNullPointerException() {
        WebApplicationException exception = resource.handleError("foo", new NullPointerException());
        Assert.assertEquals("Wrong HTTP status code: ", Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                exception.getResponse().getStatus());
    }

    @Test
    public void parseDate() throws Exception {
        String dateString = "2015-01-07";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateString);
        Assert.assertEquals("Wrong result from parseDate: ", date, resource.parseDate(dateString));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNullDate() {
        resource.parseDate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMalformedDate() {
        resource.parseDate("foo");
    }

    @Test
    public void parseUrl() throws Exception {
        String urlString = "http://www.google.com/foo";
        URL url = new URL(urlString);
        Assert.assertEquals("Wrong result from parseUrl: ", url, resource.parseUrl(urlString));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNullUrl() {
        resource.parseUrl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMalformedUrl() {
        resource.parseUrl("foo");
    }

    @Test
    public void commaSeparatedStringToList() {
        List<String> strings = resource.commaSeparatedStringToList("  foo ,bar  ,  baz ");
        Assert.assertEquals("Wrong strings in list: ", Arrays.asList("foo", "bar", "baz"), strings);
    }

    @Test
    public void commaSeparatedStringToListWithOnlyWhitespaceGivesEmptyList() {
        List<String> strings = resource.commaSeparatedStringToList("   ");
        Assert.assertTrue("List should be empty", strings.isEmpty());
    }

    @Test
    public void commaSeparatedStringToListWithOnlyCommaGivesEmptyList() {
        List<String> strings = resource.commaSeparatedStringToList(",");
        Assert.assertTrue("List should be empty", strings.isEmpty());
    }

    @Test
    public void nullCommaSeparatedStringToListGivesEmptyList() {
        List<String> strings = resource.commaSeparatedStringToList(null);
        Assert.assertTrue("List should be empty", strings.isEmpty());
    }

    private static class TestResource extends AbstractResource {

    }

}
