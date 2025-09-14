package com.reallifedeveloper.common.resource;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.reallifedeveloper.common.resource.TestResource.RequestInfo;

public class TestResourceTest {

    private static final String TEST_URL = "local://testresource";

    @SuppressWarnings("NullAway") // Initialized by init method called by JUnit
    private static TestResource resource;

    @SuppressWarnings("NullAway") // Initialized by init method called by JUnit
    private static Server server;

    @BeforeAll
    public static void init() {
        startServer();
    }

    private static void startServer() {
        resource = new TestResource();
        JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
        serverFactoryBean.setResourceClasses(TestResource.class);
        serverFactoryBean.setProvider(new JacksonJsonProvider());
        serverFactoryBean.setResourceProvider(TestResource.class, new SingletonResourceProvider(resource));
        serverFactoryBean.setAddress(TEST_URL);
        server = serverFactoryBean.create();
    }

    @AfterAll
    public static void destroy() {
        server.stop();
        server.destroy();
    }

    @Test
    public void getRequestRoot() {
        resource.reset(Response.ok().build());
        WebClient client = WebClient.create(TEST_URL);
        Response response = client.get();
        verifyRequest(resource.requests().get(0), "", "GET", null);
        verifyResponse(response, null, 200);
    }

    @Test
    public void postRequestWithPath() {
        resource.reset(Response.ok("foo").build());
        WebClient client = WebClient.create(TEST_URL).path("/foo/bar/baz");
        Response response = client.post("foo");
        verifyRequest(resource.requests().get(0), "foo/bar/baz", "POST", "foo");
        verifyResponse(response, "foo", 200);
    }

    @Test
    public void postRequestWithMuchData() {
        resource.reset(Response.ok("foo").build());
        WebClient client = WebClient.create(TEST_URL).path("/foo/bar/baz");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("1234567890");
        }
        Response response = client.post(sb.toString());
        verifyRequest(resource.requests().get(0), "foo/bar/baz", "POST", sb.toString());
        verifyResponse(response, "foo", 200);
    }

    @Test
    public void putRequestNotFound() {
        resource.reset(Response.status(Status.NOT_FOUND).build());
        WebClient client = WebClient.create(TEST_URL).path("/foo");
        Response response = client.put("foo");
        verifyRequest(resource.requests().get(0), "foo", "PUT", "foo");
        verifyResponse(response, null, 404);
    }

    @Test
    public void multipleRequests() {
        resource.reset(Response.ok("foo").build(), Response.ok("bar").build(), Response.ok("baz").build());
        WebClient client = WebClient.create(TEST_URL).path("/foo");
        Response response1 = client.head();
        client = WebClient.create(TEST_URL).path("/bar");
        Response response2 = client.delete();
        client = WebClient.create(TEST_URL).path("/baz");
        Response response3 = client.options();
        verifyRequest(resource.requests().get(0), "foo", "HEAD", null);
        verifyResponse(response1, null, 200);
        verifyRequest(resource.requests().get(1), "bar", "DELETE", null);
        verifyResponse(response2, "bar", 200);
        verifyRequest(resource.requests().get(2), "baz", "OPTIONS", null);
        verifyResponse(response3, "baz", 200);
    }

    @Test
    public void getWithoutProperlyConfiguredResponse() {
        WebClient client = WebClient.create(TEST_URL).path("/foo");
        Response response = client.get();
        verifyResponse(response, null, 500);
    }

    private void verifyRequest(RequestInfo request, String path, String method, @Nullable String postData) {
        Assertions.assertEquals(path, request.path(), "Wrong request path: ");
        Assertions.assertEquals(method, request.method(), "Wrong request method: ");
        Assertions.assertEquals(postData, request.data(), "Wrong post data: ");
    }

    private void verifyResponse(Response response, @Nullable Object entity, int status) {
        Assertions.assertNotNull(response, "Response should not be null");
        if (entity == null) {
            Assertions.assertNull(response.getEntity(), "Response entity should be null");
        } else {
            Assertions.assertEquals(entity, response.readEntity(entity.getClass()), "Wrong response entity: ");
        }
        Assertions.assertEquals(status, response.getStatus(), "Wrong response HTTP status code: ");
    }
}
