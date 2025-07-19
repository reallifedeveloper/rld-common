package com.reallifedeveloper.common.infrastructure.jmx;

import static com.reallifedeveloper.common.domain.LogUtil.removeCRLF;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.context.ServletContextAware;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.ServletContext;

/**
 * An implementation of the JMX {@link SystemInfoMXBean} interface that reads the system information from a manifest file in a WAR file.
 * <p>
 * This class expects to be configured as a Spring bean in a web application so that the {@link #setServletContext(ServletContext)} method
 * is called automatically. If you want to use this class outside of Spring, you are responsible for calling this method.
 *
 * @author RealLifeDeveloper
 */
@ManagedResource(description = "General System Information")
public class ServletContextSystemInfo implements SystemInfoMXBean, ServletContextAware {

    private static final String VERSION_MANIFEST_ENTRY = "Implementation-Version";
    private static final String BUILD_TIME_MANIFEST_ENTRY = "Build-Time";
    private static final String SCM_REVISION_MANIFEST_ENTRY = "SCM-Revision";

    private static final Logger LOG = LoggerFactory.getLogger(ServletContextSystemInfo.class);

    private @Nullable String version;
    private @Nullable String buildTime;
    private @Nullable String scmRevision;

    @Override
    @ManagedAttribute(description = "System version")
    public @Nullable String getVersion() {
        return version;
    }

    @Override
    @ManagedAttribute(description = "Date and time that the system was built")
    public @Nullable String getBuildTime() {
        return buildTime;
    }

    @Override
    @ManagedAttribute(description = "System revision number in version control")
    public @Nullable String getScmRevision() {
        return scmRevision;
    }

    @Override
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LooseCoupling" })
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    public void setServletContext(ServletContext servletContext) {
        try (InputStream in = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            Manifest manifest = new Manifest(in);
            Attributes attributes = manifest.getMainAttributes();
            version = attributes.getValue(VERSION_MANIFEST_ENTRY);
            LOG.info("version={}", removeCRLF(version));
            buildTime = attributes.getValue(BUILD_TIME_MANIFEST_ENTRY);
            LOG.info("buildTime={}", removeCRLF(buildTime));
            scmRevision = attributes.getValue(SCM_REVISION_MANIFEST_ENTRY);
            LOG.info("scmRevision={}", removeCRLF(scmRevision));
        } catch (Exception e) {
            LOG.error("Failed to read META-INF/MANIFEST.MF: ", e);
        }
    }

}
