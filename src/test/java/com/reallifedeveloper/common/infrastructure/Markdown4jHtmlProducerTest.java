package com.reallifedeveloper.common.infrastructure;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

public class Markdown4jHtmlProducerTest {

    private Markdown4jHtmlProducer htmlProducer = new Markdown4jHtmlProducer();

    @Test
    public void produce() throws Exception {
        String html = htmlProducer.produce("/markdown/foo.md");
        Assert.assertNotNull("Html should not be null", html);
        String expectedHtml = "<html><h1>Foo</h1>\n<p>Bar!</p>\n</html>";
        Assert.assertEquals("Wrong HTML produced: ", expectedHtml, html);
    }

    @Test(expected = FileNotFoundException.class)
    public void produceResourceNotFound() throws Exception {
        htmlProducer.produce("/foo/bar.md");
    }

    @Test(expected = IllegalArgumentException.class)
    public void produceNull() throws Exception {
        htmlProducer.produce(null);
    }

    @Test
    public void canHandle() {
        Assert.assertTrue("The producer should be able to handle resource /foo.md",
                htmlProducer.canHandle("/foo.md"));
        Assert.assertFalse("The producer should not be able to handle resource /foo.doc",
                htmlProducer.canHandle("/foo.doc"));
    }
}
