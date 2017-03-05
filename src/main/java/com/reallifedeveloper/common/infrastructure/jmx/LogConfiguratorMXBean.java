package com.reallifedeveloper.common.infrastructure.jmx;

import java.util.List;

/**
 * A JMX MBean interface for monitoring and controlling logging.
 *
 * @author RealLifeDeveloper
 */
public interface LogConfiguratorMXBean {

    /**
     * Gives a list with the names of the loggers defined.
     *
     * @return a list with the logger names
     */
    List<String> getLoggers();

    /**
     * Gives the log level of the named logger.
     *
     * @param loggerName the name of the logger
     *
     * @return the log level of <code>logger</code>
     */
    String getLogLevel(String loggerName);

    /**
     * Sets the log level of the named logger.
     *
     * @param loggerName the name of the logger
     * @param level the new log level
     */
    void setLogLevel(String loggerName, String level);

}
