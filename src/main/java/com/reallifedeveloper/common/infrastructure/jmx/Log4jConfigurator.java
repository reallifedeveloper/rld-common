package com.reallifedeveloper.common.infrastructure.jmx;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.reallifedeveloper.common.domain.ErrorHandling;

/**
 * An implementation of the JMX {@link LogConfiguratorMXBean} interface using Log4j.
 *
 * @author RealLifeDeveloper
 */
@ManagedResource(description = "Log4j Configuration")
public class Log4jConfigurator implements LogConfiguratorMXBean {

    @Override
    @ManagedAttribute(description = "The available loggers")
    public List<String> getLoggers() {
        return LoggerContext.getContext().getConfiguration().getLoggers().entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue().getLevel()).toList();
        // return LoggerContext.getContext().getLoggers().stream().map(logger -> logger.getName() + "=" + logger.getLevel()).toList();

        // List<String> list = new ArrayList<String>();
        // @SuppressWarnings("rawtypes")
        // Enumeration loggers = LogManager.getCurrentLoggers();
        // while (loggers.hasMoreElements()) {
        // Logger logger = (Logger) loggers.nextElement();

        // if (logger.getLevel() != null) {
        // list.add(logger.getName() + " = " + logger.getLevel().toString());
        // }
        // }
        // return list;
    }

    @Override
    @ManagedOperation(description = "Gives the log level for a logger")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "logger", description = "The name of the logger") })
    public String getLogLevel(String loggerName) {
        return getLogger(loggerName).getLevel().name();

        // String levelName = "unavailable";

        // if (isNotBlank(loggerName)) {
        // Logger logger = Logger.getLogger(loggerName);

        // if (logger != null) {
        // Level level = logger.getLevel();
        // if (level != null) {
        // levelName = level.toString();
        // }
        // }
        // }
        // return levelName;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@code level} cannot be parsed as a log level, the level is assumed to be {@code DEBUG}.
     */
    @Override
    @ManagedOperation(description = "Sets the log level for a logger")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "logger", description = "The name of the logger"),
            @ManagedOperationParameter(name = "level", description = "The new log level") })
    public void setLogLevel(String loggerName, String level) {
        if (isNotBlank(loggerName) && isNotBlank(level)) {
            getLogger(loggerName).setLevel(Level.getLevel(level));
        }

        // if (isNotBlank(loggerName) && isNotBlank(level)) {
        // Logger logger = Logger.getLogger(loggerName);

        // if (logger != null) {
        // logger.setLevel(Level.toLevel(level.toUpperCase(Locale.ROOT)));
        // }
        // }
    }

    private static Logger getLogger(String loggerName) {
        ErrorHandling.checkNull("loggerName must not be null", loggerName);
        return LoggerContext.getContext().getLogger(loggerName);
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
