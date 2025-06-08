package com.reallifedeveloper.common.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class Markdown4jHtmlProducerTest {

    private final Markdown4jHtmlProducer htmlProducer = new Markdown4jHtmlProducer();

    @Test
    public void produce() throws Exception {
        String html = htmlProducer.produce("/markdown/foo.md");
        assertNotNull(html, "Html should not be null");
        String expectedHtml = "<html><h1>Foo</h1>\n<p>Bar!</p>\n</html>";
        assertEquals(expectedHtml, html, "Wrong HTML produced");
    }

    @Test
    public void produceResourceNotFound() {
        Exception e = assertThrows(FileNotFoundException.class, () -> htmlProducer.produce("/foo/bar.md"));
        assertEquals("Resource not found: /foo/bar.md", e.getMessage());
    }

    @Test
    public void produceNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> htmlProducer.produce(null));
        assertEquals("resourceName must not be null", e.getMessage());
    }

    @Test
    public void canHandle() {
        assertTrue(htmlProducer.canHandle("/foo.md"), "The producer should be able to handle resource /foo.md");
        assertFalse(htmlProducer.canHandle("/foo.doc"), "The producer should not be able to handle resource /foo.doc");
    }
}
