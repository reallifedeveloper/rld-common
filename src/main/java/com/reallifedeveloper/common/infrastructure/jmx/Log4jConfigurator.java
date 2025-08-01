package com.reallifedeveloper.common.infrastructure.jmx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
        List<String> configLoggers = getContext().getConfiguration().getLoggers().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().getLevel()).toList();
        List<String> contextLoggers = getContext().getLoggers().stream().map(l -> l.getName() + "=" + l.getLevel()).toList();
        return Stream.concat(configLoggers.stream(), contextLoggers.stream()).distinct().toList();
    }

    @Override
    @ManagedOperation(description = "Gives the log level for a logger")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "logger", description = "The name of the logger") })
    public String getLogLevel(String loggerName) {
        return getLoggerIfExists(loggerName).map(l -> l.getLevel().name()).orElse("unavailable");
    }

    @Override
    @ManagedOperation(description = "Sets the log level for a logger")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "logger", description = "The name of the logger"),
            @ManagedOperationParameter(name = "level", description = "The new log level") })
    public void setLogLevel(String loggerName, String level) {
        if (isNotBlank(loggerName) && isNotBlank(level)) {
            Level logLevel = Level.getLevel(level);
            if (logLevel != null) {
                Logger logger = getOrCreateLogger(loggerName);
                logger.setLevel(logLevel);
            }
        }
    }

    @SuppressWarnings("PMD.CloseResource") // Closing the LoggerContext shuts down logging.
    private static Optional<Logger> getLoggerIfExists(String loggerName) {
        ErrorHandling.checkNull("loggerName must not be null", loggerName);
        LoggerContext context = getContext();
        if (context.hasLogger(loggerName) || context.getConfiguration().getLoggers().containsKey(loggerName)) {
            return Optional.of(context.getLogger(loggerName));
        } else {
            return Optional.empty();
        }
    }

    private static Logger getOrCreateLogger(String loggerName) {
        return getContext().getLogger(loggerName);
    }

    private static LoggerContext getContext() {
        return LoggerContext.getContext();
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
