package com.reallifedeveloper.common.resource.documentation;

import java.io.IOException;

/**
 * A producer of HTML.
 *
 * @author RealLifeDeveloper
 */
public interface HtmlProducer {

    /**
     * Produces HTML from the named resource on classpath.
     *
     * @param resourceName the name of the resource to convert to HTML
     *
     * @return the HTML produced
     *
     * @throws IOException if converting the resource failed
     */
    String produce(String resourceName) throws IOException;

    /**
     * Shows if this {@code HtmlProducer} can handle the given resource or not.
     *
     * @param resourceName the name of the resource to check
     *
     * @return {@code true} if the resource can be handled, {@code false} otherwise
     */
    boolean canHandle(String resourceName);
}
