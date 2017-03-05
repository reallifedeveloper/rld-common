package com.reallifedeveloper.common.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.reallifedeveloper.common.resource.TestResource.RequestInfo;

public class TestResourceTest {

    private static final String TEST_URL = "local://testresource";

    private static TestResource resource;
    private static Server server;

    @BeforeClass
    public static void init() {
        startServer();
    }

    private static void startServer() {
        resource = new TestResource();
        JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
        serverFactoryBean.setResourceClasses(TestResource.class);
        serverFactoryBean.setProvider(new JacksonJaxbJsonProvider());
        serverFactoryBean.setResourceProvider(TestResource.class, new SingletonResourceProvider(resource));
        serverFactoryBean.setAddress(TEST_URL);
        server = serverFactoryBean.create();
    }

    @AfterClass
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

    private void verifyRequest(RequestInfo request, String path, String method, String postData) {
        Assert.assertEquals("Wrong request path: ", path, request.path());
        Assert.assertEquals("Wrong request method: ", method, request.method());
        Assert.assertEquals("Wrong post data: ", postData, request.data());
    }

    private void verifyResponse(Response response, Object entity, int status) {
        Assert.assertNotNull("Response should not be null", response);
        if (entity == null) {
            Assert.assertNull("Response entity should be null", response.getEntity());
        } else {
            Assert.assertEquals("Wrong reponse entity: ", entity, response.readEntity(entity.getClass()));
        }
        Assert.assertEquals("Wrong response HTTP status code: ", status, response.getStatus());
    }
}
