package com.reallifedeveloper.common.infrastructure.jmx;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A JMX MBean interface for getting basic system information, such as version number.
 *
 * @author RealLifeDeveloper
 */
public interface SystemInfoMXBean {

    /**
     * Gives the system version.
     *
     * @return the system version
     */
    @Nullable String getVersion();

    /**
     * Gives the date and time the system was built.
     *
     * @return the date and time the system was built
     */
    @Nullable String getBuildTime();

    /**
     * Gives the revision number of the system in version control.
     *
     * @return the revision number of the system
     */
    @Nullable String getScmRevision();

}
