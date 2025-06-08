package com.reallifedeveloper.common.infrastructure.jmx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Log4jConfiguratorTest {

    // From src/test/resources/log4j2-test.xml
    private static final String[] EXPECTED_LOGGERS = { "com.reallifedeveloper", "org.dbunit", "org.dbunit.dataset", "org.hibernate.SQL",
            "org.hibernate.type", "org.springframework", "org.springframework.orm.jpa", };

    private static final String[] EXPECTED_LOG_LEVELS = { "TRACE", "INFO", "INFO", "DEBUG", "INFO", "WARN", "INFO", };

    /**
     * The default log level used by Log4j2 if we look up loggers that have not been configured.
     */
    private static final String DEFAULT_LOG_LEVEL = Level.ERROR.name();

    private Log4jConfigurator config = new Log4jConfigurator();

    @BeforeAll
    public static void checkTestData() {
        if (EXPECTED_LOGGERS.length != EXPECTED_LOG_LEVELS.length) {
            fail("There should be the same number of loggers and log levels: loggers=" + Arrays.asList(EXPECTED_LOGGERS) + ", log levels="
                    + Arrays.asList(EXPECTED_LOG_LEVELS));
        }
        System.setProperty("log4j2.debug", "true");
    }

    @BeforeEach
    public void reloadLog4jConfiguration() throws Exception {
        // Configurator.initialize("test-config", getClass().getClassLoader(), "log4j2-test.xml");
        // Configurator.reconfigure();
        // Logger logger = LoggerContext.getContext().getLogger(getClass().getName());
        // logger.trace("===== foo");
    }

    @Test
    public void getLoggers() {
        List<String> loggers = config.getLoggers().stream().sorted().toList();
        assertNotNull(loggers, "Loggers should not be null");
        assertEquals(EXPECTED_LOGGERS.length, loggers.size(), "Wrong number of loggers: ");
        for (int i = 0; i < loggers.size(); i++) {
            assertEquals(EXPECTED_LOGGERS[i] + " = " + EXPECTED_LOG_LEVELS[i], loggers.get(i), "Wrong logger name: ");
        }
    }

    @Test
    public void getLogLevel() {
        for (int i = 0; i < EXPECTED_LOGGERS.length; i++) {
            String logger = EXPECTED_LOGGERS[i];
            String logLevel = config.getLogLevel(logger);
            assertEquals(EXPECTED_LOG_LEVELS[i], logLevel, "Wrong log level for logger " + logger + ": ");
        }
    }

    @Test
    public void getLogLevelNonExistingLoggerName() {
        getLogger("foo").error("===== errror 1");
        assertEquals(DEFAULT_LOG_LEVEL, config.getLogLevel("foo"), "Unexpected log level for non-existing logger");
    }

    @Test
    public void getLogLevelNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->config.getLogLevel(null));
        assertEquals("loggerName must not be null", e.getMessage());
    }

    @Test
    public void setLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = config.getLogLevel(loggerName);
        String newLevel = "INFO";
        assertTrue(!oldLevel.equals(newLevel), "Old and new levels should be different (error in test data)");
        getLogger(loggerName).trace("===== trace 1");
        getLogger(loggerName).info("===== info 1");
        config.setLogLevel(loggerName, newLevel);
        getLogger(loggerName).trace("===== trace 2");
        getLogger(loggerName).info("===== info 2");
        assertEquals(newLevel, config.getLogLevel(loggerName), "Wrong log level config after set: ");
        Logger logger = getLogger(loggerName);
        assertEquals(newLevel, logger.getLevel().toString(), "Wrong Log4j log level after set: ");
    }

    // @Test
    public void setLevelNonExistingLoggerName() {
        String loggerName = "foo";
        String oldLevel = config.getLogLevel(loggerName);
        assertEquals("unavailable", oldLevel, "Wrong level before set: ");
        String newLevel = "TRACE";
        config.setLogLevel(loggerName, newLevel);
        assertEquals(EXPECTED_LOGGERS.length + 1, config.getLoggers().size(), "Expected one logger to have been created: ");
        assertEquals(newLevel, config.getLogLevel(loggerName), "Wrong level after set: ");
    }

    // @Test
    public void setLevelNullLoggerName() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = config.getLogLevel(loggerName);
        config.setLogLevel(loggerName, null);
        assertEquals(oldLevel, config.getLogLevel(loggerName), "Wrong log level after set: ");
    }

    // @Test
    public void setLevelIncorrectLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        config.setLogLevel(loggerName, "foo");
        assertEquals("DEBUG", config.getLogLevel(loggerName), "Wrong log level after set: ");
    }

    // @Test
    public void setLevelNullLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = config.getLogLevel(loggerName);
        config.setLogLevel(loggerName, null);
        assertEquals(oldLevel, config.getLogLevel(loggerName), "Wrong log level after set: ");
    }

    private static Logger getLogger(String name) {
        return LoggerContext.getContext().getLogger(name);
    }
}
