package com.reallifedeveloper.common.resource;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base class for JAX-RS resources.
 *
 * @author RealLifeDeveloper
 */
public abstract class AbstractResource {

    /**
     * The optional query parameter that holds the API key of the calling system. If authentication is
     * required, either this query parameter or the HTTP header {@link #API_KEY_HTTP_HEADER} must be
     * included in the request.
     */
    public static final String API_KEY_QUERY_PARAMETER = "apikey";

    /**
     * The optional HTTP header that holds the API key of the calling system. If authentication is
     * required, either this HTTP header or the query parameter {@link #API_KEY_QUERY_PARAMETER} must be
     * included in the request.
     */
    public static final String API_KEY_HTTP_HEADER = "SvkAuthSvc-ApiKey";

    /**
     * The standard format for dates, without time.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * A handy value to provide to the {@link ResourceUtil#cacheControl(int)} method to cache a result
     * for one hour.
     */
    protected static final int CACHE_1_HOUR = 60 * 60;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Gives the <code>org.slf4j.Logger</code> to use for logging.
     *
     * @return the <code>org.slf4j.Logger</code>
     */
    protected Logger logger() {
        return logger;
    }

    /**
     * Use this method to translate an exception to the appropriate <code>WebApplicationException</code>.
     * The problem is also logged.
     *
     * @param methodName the name of the method where the problem occurred
     * @param originalException the exception that should be translated
     *
     * @return an appropriate <code>WebApplicationException</code>, depending on <code>originalException</code>
     *
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    protected WebApplicationException handleError(String methodName, Exception originalException) {
        if (methodName == null || originalException == null) {
            throw new IllegalArgumentException("Arguments must not be null: methodName=" + methodName
                    + ", originalException=" + originalException);
        }
        WebApplicationException webApplicationException;
        if (originalException instanceof IllegalArgumentException) {
            logger().debug("{}: {}", methodName, originalException);
            webApplicationException = ResourceUtil.badRequest(originalException.getMessage());
        } else if (originalException instanceof FileNotFoundException) {
            logger().debug("{}: {}", methodName, originalException);
            webApplicationException = ResourceUtil.notFound(originalException.getMessage());
        } else {
            logger().error(methodName, originalException);
            webApplicationException = ResourceUtil.serverError(originalException.toString());
        }
        return webApplicationException;
    }

    /**
     * Parses a string as a <code>java.util.Date</code>, using the date format {@value #DATE_FORMAT}.
     *
     * @param date the string to parse
     *
     * @return the <code>java.util.Date</code> representation of <code>date</code>
     *
     * @throws IllegalArgumentException if <code>date</code> is <code>null</code> or could not be parsed as a date
     */
    protected Date parseDate(String date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("The string '%s' could not be parsed as a date (%s)",
                    date, DATE_FORMAT), e);
        }
    }

    /**
     * Parses a string as a <code>java.net.URL</code>.
     *
     * @param url the string to parse
     *
     * @return the <code>java.net.URL</code> representation of <code>url</code>
     *
     * @throws IllegalArgumentException if <code>url</code> could not be parsed as a URL
     */
    protected URL parseUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("The string '%s' could not be parsed as a url", url), e);
        }
    }

    /**
     * Given a comma-separated list, this methods returns a list containing the constituent strings, with
     * leading and trailing whitespace removed.
     *
     * Example: Given the string "  foo ,bar  ,  baz " the result is the list ["foo","bar","baz"].
     *
     * @param s a comma-separated list
     *
     * @return a list with the constituent strings, with whitespace removed
     */
    protected List<String> commaSeparatedStringToList(String s) {
        if (s == null || s.trim().isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(s.split("\\s*,\\s*")).stream().map(String::trim).collect(Collectors.toList());
        }
    }
}
