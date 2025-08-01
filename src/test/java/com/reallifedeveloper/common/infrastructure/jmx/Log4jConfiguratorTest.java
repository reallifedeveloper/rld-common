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
    private static final String[] EXPECTED_LOGGERS = { "com.reallifedeveloper", "org.dbunit.dataset", "org.dbunit", "org.hibernate.SQL",
            "org.hibernate.type", "org.springframework.orm.jpa", "org.springframework", };

    private static final String[] EXPECTED_LOG_LEVELS = { "TRACE", "INFO", "INFO", "DEBUG", "INFO", "INFO", "WARN", };

    /**
     * The default log level used by Log4j2 if we look up loggers that have not been configured.
     */
    private static final String DEFAULT_LOG_LEVEL = Level.ERROR.name();

    private Log4jConfigurator configurator = new Log4jConfigurator();

    @BeforeAll
    public static void initClass() {
        checkTestData();
        System.setProperty("log4j2.debug", "false");
    }

    /**
     * Checks that the test data provided has the expected format.
     */
    private static void checkTestData() {
        if (EXPECTED_LOGGERS.length != EXPECTED_LOG_LEVELS.length) {
            fail("There should be the same number of loggers and log levels: loggers=" + Arrays.asList(EXPECTED_LOGGERS) + ", log levels="
                    + Arrays.asList(EXPECTED_LOG_LEVELS));
        }
    }

    @BeforeEach
    public void init() {
        reloadLog4jConfiguration();
    }

    /**
     * Reloads the log configuration from file.
     * <p>
     * Note that this does <em>not</em> remove any loggers, so loggers that have been added dynamically during the testing will still be
     * there.
     */
    private static void reloadLog4jConfiguration() {
        LoggerContext.getContext().reconfigure();
    }

    @Test
    public void getLoggers() {
        // We filter out the "foo" logger that may or may not have been added yet by other tests.
        List<String> loggers = configurator.getLoggers().stream().filter(s -> !s.startsWith("foo")).sorted().toList();
        assertNotNull(loggers, "Loggers should not be null");
        assertEquals(EXPECTED_LOGGERS.length, loggers.size(), "Wrong number of loggers: ");
        for (int i = 0; i < loggers.size(); i++) {
            assertEquals(EXPECTED_LOGGERS[i] + "=" + EXPECTED_LOG_LEVELS[i], loggers.get(i), "Wrong logger name: ");
        }
    }

    @Test
    public void getLogLevel() {
        for (int i = 0; i < EXPECTED_LOGGERS.length; i++) {
            String logger = EXPECTED_LOGGERS[i];
            String logLevel = configurator.getLogLevel(logger);
            assertEquals(EXPECTED_LOG_LEVELS[i], logLevel, "Wrong log level for logger " + logger + ": ");
        }
    }

    @Test
    public void getLogLevelNonExistingLoggerName() {
        getLogger("foo").error("===== errror 1");
        assertEquals(DEFAULT_LOG_LEVEL, configurator.getLogLevel("foo"), "Unexpected log level for non-existing logger");
    }

    @Test
    public void getLogLevelNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> configurator.getLogLevel(null));
        assertEquals("loggerName must not be null", e.getMessage());
    }

    @Test
    public void setLogLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = configurator.getLogLevel(loggerName);
        String newLevel = "INFO";
        assertTrue(!oldLevel.equals(newLevel), "Old and new levels should be different (error in test data)");
        getLogger(loggerName).trace("===== trace 1");
        getLogger(loggerName).info("===== info 1");
        configurator.setLogLevel(loggerName, newLevel);
        getLogger(loggerName).trace("===== trace 2");
        getLogger(loggerName).info("===== info 2");
        assertEquals(newLevel, configurator.getLogLevel(loggerName), "Wrong log level config after set: ");
        Logger logger = getLogger(loggerName);
        assertEquals(newLevel, logger.getLevel().toString(), "Wrong Log4j log level after set: ");
    }

    @Test
    public void setLogLevelNonExistingLoggerName() {
        String loggerName = "foo";
        String oldLevel = configurator.getLogLevel(loggerName);
        assertEquals("unavailable", oldLevel, "Wrong level before set: ");
        String newLevel = "INFO";
        configurator.setLogLevel(loggerName, newLevel);
        assertEquals(EXPECTED_LOGGERS.length + 1, configurator.getLoggers().size(), "Expected one logger to have been created: ");
        assertEquals(newLevel, configurator.getLogLevel(loggerName), "Wrong level after set: ");
    }

    @Test
    public void setLogLevelNullLoggerName() {
        configurator.setLogLevel(null, EXPECTED_LOG_LEVELS[0]);
        // Verify that no log level has been changed:
        getLogLevel();
    }

    @Test
    public void setLogLevelBlankLoggerName() {
        configurator.setLogLevel("    ", EXPECTED_LOG_LEVELS[0]);
        // Verify that no log level has been changed:
        getLogLevel();
    }

    @Test
    public void setLogLevelIncorrectLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        configurator.setLogLevel(loggerName, "foo");
        assertEquals(EXPECTED_LOG_LEVELS[0], getLogger(loggerName).getLevel().toString());
        assertEquals(EXPECTED_LOG_LEVELS[0], configurator.getLogLevel(loggerName), "Wrong log level after set: ");
    }

    @Test
    public void setLogLevelNullLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = configurator.getLogLevel(loggerName);
        configurator.setLogLevel(loggerName, null);
        assertEquals(oldLevel, configurator.getLogLevel(loggerName), "Wrong log level after set: ");
    }

    private static Logger getLogger(String name) {
        return LoggerContext.getContext().getLogger(name);
    }
}
