package com.reallifedeveloper.common.infrastructure.jmx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

public class ServletContextSystemInfoTest {

    @Test
    public void testSetServletContext() {
        MockServletContext servletContext = new MockServletContext("file:src/test/resources");
        ServletContextSystemInfo systemInfo = new ServletContextSystemInfo();
        systemInfo.setServletContext(servletContext);

        assertEquals("2014-08-27T14:20:05+0000", systemInfo.getBuildTime(), "Unexpected build time");
        assertEquals("4711", systemInfo.getScmRevision(), "Unexpected SCM revision");
        assertEquals("1.0-SNAPSHOT", systemInfo.getVersion(), "Unexpected version");
    }

    @Test
    public void testSetServletContextWithNullContext() {
        ServletContextSystemInfo systemInfo = new ServletContextSystemInfo();
        systemInfo.setServletContext(null);

        assertNull(systemInfo.getBuildTime(), "Build time should be null");
        assertNull(systemInfo.getScmRevision(), "SCM revision should be null");
        assertNull(systemInfo.getVersion(), "Version should be null");
    }
}
