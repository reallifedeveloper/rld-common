package com.reallifedeveloper.common.infrastructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.markdown4j.Markdown4jProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reallifedeveloper.common.resource.documentation.HtmlProducer;

/**
 * An implementation of {@link HtmlProducer} that converts
 * <a href="http://daringfireball.net/projects/markdown/">Markdown</a>
 * documents to HTML using <a href="https://code.google.com/p/markdown4j/">Markdown4j</a>.
 *
 * @author RealLifeDeveloper
 */
public final class Markdown4jHtmlProducer implements HtmlProducer {

    private static final Logger LOG = LoggerFactory.getLogger(Markdown4jHtmlProducer.class);

    private Markdown4jProcessor markdownProcessor = new Markdown4jProcessor();

    @Override
    public String produce(String resourceName) throws IOException {
        LOG.trace("produce: resourceName={}", resourceName);
        if (resourceName == null) {
            throw new IllegalArgumentException("resourceName should not be null");
        }
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            String html = "<html>" + markdownProcessor.process(in) + "</html>";
            LOG.trace("produce: {}", html);
            return html;
        }
    }

    @Override
    public boolean canHandle(String resourceName) {
        return resourceName.endsWith(".md");
    }

}
