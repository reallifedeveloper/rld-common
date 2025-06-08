package com.reallifedeveloper.common.resource.documentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;

import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.DatatypeConverter;

import com.reallifedeveloper.common.infrastructure.Markdown4jHtmlProducer;

public class DocumentationResourceTest {

    private static final String ENDPOINT_ADDRESS = "http://localhost/api";

    private final Markdown4jHtmlProducer htmlProducer = new Markdown4jHtmlProducer();
    private final DocumentationResource resource = new DocumentationResource("/markdown", htmlProducer);

    private UriInfo uriInfo;

    @BeforeEach
    public void init() throws Exception {
        MessageImpl message = new MessageImpl();
        message.setExchange(new ExchangeImpl());
        message.put(Message.ENDPOINT_ADDRESS, ENDPOINT_ADDRESS);
        uriInfo = new UriInfoImpl(message);

        Field uriInfoField = DocumentationResource.class.getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(resource, uriInfo);
    }

    @Test
    public void getDocumentationDefaultDocument() {
        Response response = resource.getDocumentation();
        assertNotNull(response, "Response should not be null");
        assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Wrong status");
        assertEquals("text/html;charset=UTF-8", response.getMediaType().toString(), "Wrong content type");
        String html = (String) response.getEntity();
        assertNotNull(html, "Html should not be null");
        String expectedHtml = "<html><h1>README</h1>\n<p>Hello, <em>World</em>!</p>\n</html>";
        assertEquals(expectedHtml, html, "Wrong html produced");
    }

    @Test
    public void getDocumentationDefaultDocumentDoesNotExist() {
        DocumentationResource r = new DocumentationResource("/dbunit", htmlProducer);
        WebApplicationException exception = assertThrows(WebApplicationException.class, r::getDocumentation);
        assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus(), "Wrong status");
    }

    @Test
    public void getDocumentationNamedDocument() {
        Response response = resource.getDocumentation("foo.md");
        assertNotNull(response, "Response should not be null");
        assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Wrong status");
        assertEquals("text/html;charset=UTF-8", response.getMediaType().toString(), "Wrong content type");
        String html = (String) response.getEntity();
        assertNotNull(html, "Html should not be null");
        String expectedHtml = "<html><h1>Foo</h1>\n<p>Bar!</p>\n</html>";
        assertEquals(expectedHtml, html, "Wrong html produced");
    }

    @Test
    public void getDocumentationNamedBinaryDocument() throws Exception {
        Response response = resource.getDocumentation("foo.zip");
        assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Wrong status");
        assertEquals("application/octet-stream", response.getMediaType().toString(), "Wrong content type");
        StreamingOutput stream = (StreamingOutput) response.getEntity();
        assertNotNull(stream, "Stream should not be null");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.write(out);
        byte[] data = out.toByteArray();
        assertEquals(164, data.length, "Wrong number of bytes in zip file");
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(data);
        assertEquals("5d5b27f8e20c3db498f8fcc305ad34acd4fb6236",
                DatatypeConverter.printHexBinary(hash).toLowerCase(), "Wrong hash for zip file");
    }

    @Test
    public void getDocumentationNamedDocumentDoesNotExist() {
        WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> resource.getDocumentation("bar.txt"));
        assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus(), "Wrong status");
    }

    @Test
    public void redirect() {
        Response response = resource.redirect();
        assertNotNull(response, "Response should not be null");
        assertEquals(Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus(), "Wrong status");
        assertEquals(ENDPOINT_ADDRESS + "/doc/", response.getHeaderString("Location"), "Wrong location header");
        assertEquals(ENDPOINT_ADDRESS + "/doc/", response.getEntity(), "Wrong body");
    }

    @Test
    public void constructorNonExistingResourceDir() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new DocumentationResource("foo", htmlProducer));
        assertEquals("resourceDir does not exist: foo", exception.getMessage());
    }

    @Test
    public void constructorNullResourceDir() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new DocumentationResource(null, htmlProducer));
        assertEquals("Arguments must not be null: resourceDir=null, htmlProducer=" + htmlProducer, exception.getMessage());
    }

    @Test
    public void constructorNullHtmlProducer() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new DocumentationResource("/markdown", null));
        assertEquals("Arguments must not be null: resourceDir=/markdown, htmlProducer=null", exception.getMessage());
    }
}
