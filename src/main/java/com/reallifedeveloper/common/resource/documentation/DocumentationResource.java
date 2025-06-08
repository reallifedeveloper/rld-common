package com.reallifedeveloper.common.resource.documentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.resource.BaseResource;
import com.reallifedeveloper.common.resource.ResourceUtil;

/**
 * A JAX-RS resource that produces HTML documentation from resources on the classpath.
 *
 * @author RealLifeDeveloper
 */
public final class DocumentationResource extends BaseResource {

    private static final String CONTENT_TYPE_HTML = MediaType.TEXT_HTML + "; charset=UTF-8";
    private static final String CONTENT_TYPE_BINARY = MediaType.APPLICATION_OCTET_STREAM;

    private static final int BUFFER_SIZE = 4096;

    private static final String[] DEFAULT_DOCUMENTS = { "index.md", "readme.md" };

    private final String resourceDir;
    private final HtmlProducer htmlProducer;

    @Context
    private @Nullable HttpHeaders httpHeaders;

    @Context
    private @Nullable UriInfo uriInfo;

    /**
     * Creates a new {@code DocumentationResource} that reads documents from the specified resource directory, converting them to HTML using
     * the given {@link HtmlProducer}.
     *
     * @param resourceDir  the directory from which to read documents
     * @param htmlProducer the {@code HtmlProducer} to use to convert documents to HTML
     *
     * @throws IllegalArgumentException if any argument is {@code null}, of if {@code resourceDir} does not exist
     */
    public DocumentationResource(String resourceDir, HtmlProducer htmlProducer) {
        ErrorHandling.checkNull("Arguments must not be null: resourceDir=%s, htmlProducer=%s", resourceDir, htmlProducer);
        this.resourceDir = resourceDir;
        this.htmlProducer = htmlProducer;
        if (!resourceExists(resourceDir)) {
            throw new IllegalArgumentException("resourceDir does not exist: " + resourceDir);
        }
    }

    /**
     * Redirects the client to the proper documentation URL.
     *
     * @return a response with a status of 301 (Moved Permanently) and the correct location
     */
    @GET
    @Path("/")
    public Response redirect() {
        if (uriInfo == null) {
            throw new IllegalStateException("uriInfo field has not been set, it is assumed to be injected by Jakarta");
        }
        URI uri = uriInfo.getAbsolutePathBuilder().path("/doc/").build();
        return Response.status(Status.MOVED_PERMANENTLY).location(uri).entity(uri.toString()).build();
    }

    /**
     * Reads a default document (index.md or readme.md) from the resource directory and converts it to HTML.
     *
     * @return a response containing the HTML produced
     *
     * @throws jakarta.ws.rs.WebApplicationException with status 404 if no default document was found
     */
    @GET
    @Path("/doc")
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Response getDocumentation() {
        try {
            return handleResource(getDefaultDocument());
        } catch (Exception e) {
            throw handleError("getDocumentation", e);
        }
    }

    /**
     * Reads a document from the resource directory and converts it to HTML.
     *
     * @param document the document to read
     *
     * @return a response containing the HTML produced
     *
     * @throws jakarta.ws.rs.WebApplicationException with status 404 if {@code document} was not found
     */
    @GET
    @Path("/doc/{document}")
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Response getDocumentation(@PathParam("document") String document) {
        try {
            return handleResource(resourceDir + "/" + document);
        } catch (Exception e) {
            throw handleError("getDocumentation", e);
        }
    }

    @SuppressWarnings("PMD.UseTryWithResources")
    private Response handleResource(String resourceName) throws IOException {
        Response response;
        if (htmlProducer.canHandle(resourceName)) {
            String html = htmlProducer.produce(resourceName);
            response = Response.ok(html).header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_HTML)
                    .cacheControl(ResourceUtil.cacheControl(CACHE_1_HOUR)).build();
        } else {
            InputStream input = getClass().getResourceAsStream(resourceName);
            if (input == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException {
                    try {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int len;
                        while ((len = input.read(buffer)) != -1) {
                            output.write(buffer, 0, len);
                        }
                        output.flush();
                    } finally {
                        input.close();
                    }
                }
            };
            response = Response.ok(stream).header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_BINARY).build();
        }
        return response;
    }

    private String getDefaultDocument() throws IOException {
        for (String defaultDocument : DEFAULT_DOCUMENTS) {
            String resourceName = resourceDir + "/" + defaultDocument;
            if (resourceExists(resourceName)) {
                return resourceName;
            }
        }
        throw new FileNotFoundException("Resource not found: " + resourceDir + "/" + Arrays.asList(DEFAULT_DOCUMENTS));
    }

    private boolean resourceExists(String resourceName) {
        return getClass().getResource(resourceName) != null;
    }
}
