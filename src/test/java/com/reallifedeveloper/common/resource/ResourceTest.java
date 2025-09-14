package com.reallifedeveloper.common.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class ResourceTest {

    private TestResource resource = new TestResource();

    @Test
    @SuppressWarnings("NullAway")
    public void handleErrorMethodNameNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> resource.handleError(null, new NullPointerException()));
        assertEquals("Arguments must not be null: methodName=null, originalException=java.lang.NullPointerException", e.getMessage());
    }

    @Test
    @SuppressWarnings("NullAway")
    public void handleErrorOriginalExceptionNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> resource.handleError("foo", null));
        assertEquals("Arguments must not be null: methodName=foo, originalException=null", e.getMessage());
    }

    @Test
    public void handleErrorIllegalArgumentException() {
        WebApplicationException exception = resource.handleError("foo", new IllegalArgumentException());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code: ");
    }

    @Test
    public void handleErrorFileNotFoundException() {
        WebApplicationException exception = resource.handleError("foo", new FileNotFoundException());
        assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code: ");
    }

    @Test
    public void handleErrorNullPointerException() {
        WebApplicationException exception = resource.handleError("foo", new NullPointerException());
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus(), "Wrong HTTP status code: ");
    }

    @Test
    public void parseDate() throws Exception {
        String dateString = "2015-01-07";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, dateFormatter);
        assertEquals(date, resource.parseDate(dateString), "Wrong result from parseDate: ");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void parseNullDate() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> resource.parseDate(null));
        assertEquals("date must not be null", e.getMessage());
    }

    @Test
    public void parseMalformedDate() {
        Exception e = assertThrows(DateTimeParseException.class, () -> resource.parseDate("foo"));
        assertEquals("Text 'foo' could not be parsed at index 0", e.getMessage());
    }

    @Test
    public void parseUrl() throws Exception {
        String urlString = "http://www.google.com/foo";
        URL url = new URI(urlString).toURL();
        assertEquals(url, resource.parseUrl(urlString), "Wrong result from parseUrl: ");
    }

    @Test
    @SuppressWarnings("NullAway")
    public void parseNullUrl() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> resource.parseUrl(null));
        assertEquals("url must not be null", e.getMessage());
    }

    @Test
    public void parseMalformedUrl() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> resource.parseUrl("foo:bar"));
        assertEquals("The string 'foo:bar' could not be parsed as a url", e.getMessage());
    }

    @Test
    public void commaSeparatedStringToList() {
        List<String> strings = resource.commaSeparatedStringToList("  foo ,bar  ,  baz ");
        assertEquals(Arrays.asList("foo", "bar", "baz"), strings, "Wrong strings in list: ");
    }

    @Test
    public void commaSeparatedStringToListWithOnlyWhitespaceGivesEmptyList() {
        List<String> strings = resource.commaSeparatedStringToList("   ");
        assertTrue(strings.isEmpty(), "List should be empty");
    }

    @Test
    public void commaSeparatedStringToListWithOnlyCommaGivesEmptyList() {
        List<String> strings = resource.commaSeparatedStringToList(",");
        assertTrue(strings.isEmpty(), "List should be empty");
    }

    @Test
    public void nullCommaSeparatedStringToListGivesEmptyList() {
        List<String> strings = resource.commaSeparatedStringToList(null);
        assertTrue(strings.isEmpty(), "List should be empty");
    }

    private static class TestResource extends BaseResource {

    }

}
