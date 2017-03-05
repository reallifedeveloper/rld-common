package com.reallifedeveloper.common.infrastructure.jmx;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

public class ServletContextSystemInfoTest {

    @Test
    public void setServletContext() {
        MockServletContext servletContext = new MockServletContext("file:src/test/resources");
        ServletContextSystemInfo systemInfo = new ServletContextSystemInfo();
        systemInfo.setServletContext(servletContext);
        Assert.assertEquals("Wrong build time: ", "2014-08-27T14:20:05+0000", systemInfo.getBuildTime());
        Assert.assertEquals("Wrong SCM revision: ", "4711", systemInfo.getScmRevision());
        Assert.assertEquals("Wrong version: ", "1.0-SNAPSHOT", systemInfo.getVersion());
    }

    @Test
    public void setServletContextNullContext() {
        ServletContextSystemInfo systemInfo = new ServletContextSystemInfo();
        // Make sure we don't get an exception
        systemInfo.setServletContext(null);
        Assert.assertNull("Build time should be null", systemInfo.getBuildTime());
        Assert.assertNull("SCM revision should be null", systemInfo.getScmRevision());
        Assert.assertNull("Version should be null", systemInfo.getVersion());
    }
}
