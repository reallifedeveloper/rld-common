package com.reallifedeveloper.common.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

/**
 * A JAX-RS resource that can be used for testing REST clients. This resource accepts all requests,
 * and you can specify the responses given using the {@link #reset(Response...)} method.
 * <p>
 * See {@link TestResourceTest} for an example of how to use this class.
 *
 * @author RealLifeDeveloper
 */
@Path("/{path:.*}")
public class TestResource extends BaseResource {

    private List<RequestInfo> requests = new ArrayList<>();
    private List<Response> responses;
    private int responseNum = 0;

    @Context
    private Request request;

    @GET
    public Response get(@PathParam("path") String path) {
        return handleRequest(path);
    }

    @POST
    public Response post(@PathParam("path") String path, String postData) {
        Response response = handleRequest(path, postData);
        logger().debug("postData={}", postData);
        return response;
    }

    @PUT
    public Response put(@PathParam("path") String path, String putData) {
        return handleRequest(path, putData);
    }

    @DELETE
    public Response delete(@PathParam("path") String path) {
        return handleRequest(path);
    }

    @HEAD
    public Response head(@PathParam("path") String path) {
        return handleRequest(path);
    }

    @OPTIONS
    public Response options(@PathParam("path") String path) {
        return handleRequest(path);
    }

    private Response handleRequest(String path) {
        return handleRequest(path, null);
    }

    private Response handleRequest(String path, String data) {
        logger().info("path={}, method={}", path, request.getMethod());
        requests.add(new RequestInfo(path, request.getMethod(), data));
        return responses.get(responseNum++);
    }

    /**
     * Resets the resource so that it starts to respond with the given responses.
     *
     * @param newResponses the responses to give
     */
    public void reset(Response... newResponses) {
        this.responses = Arrays.asList(newResponses);
        this.requests.clear();
        responseNum = 0;
    }

    /**
     * Gives a list of {@link RequestInfo} objects representing the requests that have reached
     * this resource.
     *
     * @return a list of {@code RequestInfo} objects
     */
    public List<RequestInfo> requests() {
        return requests;
    }

    /**
     * Information about an HTTP request.
     */
    public static class RequestInfo {
        private String path;
        private String method;
        private String data;

        /**
         * Creates a new {@code RequestInfo} object using the given path and method.
         *
         * @param path the path of the request, not including the base URI
         * @param method the HTTP method of the request, e.g., "GET", "POST", "PUT" and so on
         * @param data the data that was sent in the body of this request if it is a POST or PUT request,
         * otherwise {@code null}
         */
        public RequestInfo(String path, String method, String data) {
            this.path = path;
            this.method = method;
            this.data = data;
        }

        /**
         * Gives the path of the request, not including the base URI.
         *
         * @return the path of the request
         */
        public String path() {
            return path;
        }

        /**
         * Gives the HTTP method of the request, e.g., "GET, "POST", "PUT" and so on.
         *
         * @return the HTTP method of the request
         */
        public String method() {
            return method;
        }

        /**
         * Gives the data that was sent in the body of this request if it is a POST or PUT request,
         * otherwise {@code null}.
         *
         * @return the data that was sent in the body of this request, or {@code null}
         */
        public String data() {
            return data;
        }
    }
}
