package com.reallifedeveloper.common.resource;

import static com.reallifedeveloper.common.domain.LogUtil.removeCRLF;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.WebApplicationException;

import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * A base class for JAX-RS resources.
 *
 * @author RealLifeDeveloper
 */
public class BaseResource {

    /**
     * The optional query parameter that holds the API key of the calling system. If authentication is required, either this query parameter
     * or the HTTP header {@link #API_KEY_HTTP_HEADER} must be included in the request.
     */
    public static final String API_KEY_QUERY_PARAMETER = "apikey";

    /**
     * The optional HTTP header that holds the API key of the calling system. If authentication is required, either this HTTP header or the
     * query parameter {@link #API_KEY_QUERY_PARAMETER} must be included in the request.
     */
    public static final String API_KEY_HTTP_HEADER = "SvkAuthSvc-ApiKey";

    /**
     * The standard format for dates, without time.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * A handy value to provide to the {@link ResourceUtil#cacheControl(int)} method to cache a result for one hour.
     */
    protected static final int CACHE_1_HOUR = 60 * 60;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Creates a new {@code BaseResource}, intended to be used by sub-classes.
     */
    protected BaseResource() {
        // The only constructor is protected, to disallow direct instantiation.
    }

    /**
     * Gives the {@code org.slf4j.Logger} to use for logging.
     *
     * @return the {@code org.slf4j.Logger}
     */
    protected Logger logger() {
        return logger;
    }

    /**
     * Use this method to translate an exception to the appropriate {@code WebApplicationException}. The problem is also logged.
     *
     * @param methodName        the name of the method where the problem occurred
     * @param originalException the exception that should be translated
     *
     * @return an appropriate {@code WebApplicationException}, depending on {@code originalException}
     *
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    protected WebApplicationException handleError(String methodName, Exception originalException) {
        ErrorHandling.checkNull("Arguments must not be null: methodName=%s, originalException=%s", methodName, originalException);
        WebApplicationException webApplicationException;
        if (originalException instanceof IllegalArgumentException) {
            logger().debug("{}: {}", removeCRLF(methodName), removeCRLF(originalException));
            webApplicationException = ResourceUtil.badRequest(originalException.getMessage());
        } else if (originalException instanceof FileNotFoundException) {
            logger().debug("{}: {}", removeCRLF(methodName), removeCRLF(originalException));
            webApplicationException = ResourceUtil.notFound(originalException.getMessage());
        } else {
            logger().error(removeCRLF(methodName), removeCRLF(originalException));
            webApplicationException = ResourceUtil.serverError(originalException.toString());
        }
        return webApplicationException;
    }

    /**
     * Parses a string as a {@code java.time.LocalDate}, using the date format {@value #DATE_FORMAT}.
     *
     * @param date the string to parse
     *
     * @return the {@code java.time.LocalDate} representation of {@code date}
     *
     * @throws IllegalArgumentException if {@code date} is {@code null} or could not be parsed as a date
     */
    protected LocalDate parseDate(String date) {
        ErrorHandling.checkNull("date must not be null", date);
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    /**
     * Parses a string as a {@code java.net.URL}.
     *
     * @param url the string to parse
     *
     * @return the {@code java.net.URL} representation of {@code url}
     *
     * @throws IllegalArgumentException if {@code url} could not be parsed as a URL
     */
    protected URL parseUrl(String url) {
        ErrorHandling.checkNull("url must not be null", url);
        try {
            return new URI(url).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException(String.format("The string '%s' could not be parsed as a url", url), e);
        }
    }

    /**
     * Given a comma-separated list, this methods returns a list containing the constituent strings, with leading and trailing whitespace
     * removed.
     *
     * Example: Given the string " foo ,bar , baz " the result is the list ["foo","bar","baz"].
     *
     * @param s a comma-separated list
     *
     * @return a list with the constituent strings, with whitespace removed
     */
    protected List<String> commaSeparatedStringToList(String s) {
        if (s == null || s.isBlank()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(s.split("\\s*,\\s*")).stream().map(String::trim).collect(Collectors.toList());
        }
    }
}
