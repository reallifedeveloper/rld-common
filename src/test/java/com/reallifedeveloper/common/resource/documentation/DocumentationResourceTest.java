package com.reallifedeveloper.common.resource.documentation;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.DatatypeConverter;

import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.reallifedeveloper.common.infrastructure.Markdown4jHtmlProducer;

public class DocumentationResourceTest {

    private static final String ENDPOINT_ADDRESS = "http://localhost/api";

    private Markdown4jHtmlProducer htmlProducer = new Markdown4jHtmlProducer();
    private DocumentationResource resource = new DocumentationResource("/markdown", htmlProducer);

    private UriInfo uriInfo;

    @Before
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
        Assert.assertNotNull("Response should not be null", response);
        Assert.assertEquals("Wrong status: ", Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content type: ", "text/html;charset=UTF-8", response.getMediaType().toString());
        String html = (String) response.getEntity();
        Assert.assertNotNull("Html should not be null", html);
        String expectedHtml = "<html><h1>README</h1>\n<p>Hello, <em>World</em>!</p>\n</html>";
        Assert.assertEquals("Wrong html produced: ", expectedHtml, html);
    }

    @Test
    public void getDocumentationDefaultDocumentDoesNotExist() {
        DocumentationResource r = new DocumentationResource("/dbunit", htmlProducer);
        try {
            r.getDocumentation();
            Assert.fail("Loading a non-existing document should fail");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong status: ", Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void getDocumentationNamedDocument() {
        Response response = resource.getDocumentation("foo.md");
        Assert.assertNotNull("Response should not be null", response);
        Assert.assertEquals("Wrong status: ", Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content type: ", "text/html;charset=UTF-8", response.getMediaType().toString());
        String html = (String) response.getEntity();
        Assert.assertNotNull("Html should not be null", html);
        String expectedHtml = "<html><h1>Foo</h1>\n<p>Bar!</p>\n</html>";
        Assert.assertEquals("Wrong html produced: ", expectedHtml, html);
    }

    @Test
    public void getDocumentationNamedBinaryDocument() throws Exception {
        Response response = resource.getDocumentation("foo.zip");
        Assert.assertEquals("Wrong status: ", Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content type: ", "application/octet-stream", response.getMediaType().toString());
        StreamingOutput stream = (StreamingOutput) response.getEntity();
        Assert.assertNotNull("Stream should not be null", stream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.write(out);
        byte[] data = out.toByteArray();
        // Check size of foo.zip on Cygwin: "ls -l foo.zip"
        Assert.assertEquals("Wrong number of bytes in zip file: ", 164, data.length);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(data);
        // Get hash of foo.zip on Cygwin: "sha1sum foo.zip"
        Assert.assertEquals("Wrong hash for zip file: ", "5d5b27f8e20c3db498f8fcc305ad34acd4fb6236",
                DatatypeConverter.printHexBinary(hash).toLowerCase());
    }

    @Test
    public void getDocumentationNamedDocumentDoesNotExist() {
        try {
            resource.getDocumentation("bar.txt");
            Assert.fail("Loading a non-existing document should fail");
        } catch (WebApplicationException e) {
            Assert.assertEquals("Wrong status: ", Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void redirect() {
        Response response = resource.redirect();
        Assert.assertNotNull("Response should not be null", response);
        Assert.assertEquals("Wrong status: ", Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong location header: ", ENDPOINT_ADDRESS + "/doc/",
                response.getHeaderString("Location"));
        Assert.assertEquals("Wrong body: ", ENDPOINT_ADDRESS + "/doc/", response.getEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNonExistingResourceDir() {
        new DocumentationResource("foo", htmlProducer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullResourceDir() {
        new DocumentationResource(null, htmlProducer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHtmlProducer() {
        new DocumentationResource("/markdown", null);
    }

}
