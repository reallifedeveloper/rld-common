package com.reallifedeveloper.common.infrastructure.jmx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Log4jConfigurer;

public class Log4jConfiguratorTest {

    // From src/test/resources/log4j.xml
    private static final String[] EXPECTED_LOGGERS = {
        "com.reallifedeveloper",
        "org.dbunit",
        "org.dbunit.dataset",
        "org.hibernate.SQL",
        "org.hibernate.type",
        "org.springframework",
        "org.springframework.orm.jpa",
    };

    private static final String[] EXPECTED_LOG_LEVELS = {
        "TRACE",
        "INFO",
        "INFO",
        "DEBUG",
        "INFO",
        "WARN",
        "INFO",
    };

    private Log4jConfigurator config = new Log4jConfigurator();

    @BeforeClass
    public static void checkTestData() {
        if (EXPECTED_LOGGERS.length != EXPECTED_LOG_LEVELS.length) {
            Assert.fail("There should be the same number of loggers and log levels: loggers="
                    + Arrays.asList(EXPECTED_LOGGERS) + ", log levels=" + Arrays.asList(EXPECTED_LOG_LEVELS));
        }
    }

    @Before
    public void reloadLog4jConfiguration() throws Exception {
        LogManager.resetConfiguration();
        Log4jConfigurer.initLogging("classpath:log4j.xml");
    }

    @Test
    public void getLoggers() {
        List<String> loggers = config.getLoggers();
        Assert.assertNotNull("Loggers should not be null", loggers);
        Assert.assertEquals("Wrong number of loggers: ", EXPECTED_LOGGERS.length, loggers.size());
        Collections.sort(loggers);
        for (int i = 0; i < loggers.size(); i++) {
            Assert.assertEquals("Wrong logger name: ", EXPECTED_LOGGERS[i] + " = " + EXPECTED_LOG_LEVELS[i],
                    loggers.get(i));
        }
    }

    @Test
    public void getLogLevel() {
        for (int i = 0; i < EXPECTED_LOGGERS.length; i++) {
            String logger = EXPECTED_LOGGERS[i];
            String logLevel = config.getLogLevel(logger);
            Assert.assertEquals("Wrong log level for logger " + logger + ": ", EXPECTED_LOG_LEVELS[i], logLevel);
        }
    }

    @Test
    public void getLogLevelNonExistingLoggerName() {
        Assert.assertEquals("Unexpected log level for non-existing logger: ", "unavailable", config.getLogLevel("foo"));
    }

    @Test
    public void getLogLevelNull() {
        Assert.assertEquals("Unexpected log level for null logger: ", "unavailable", config.getLogLevel(null));
    }

    @Test
    public void setLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = config.getLogLevel(loggerName);
        String newLevel = "ERROR";
        Assert.assertTrue("Old and new levels should be different (error in test data)", !oldLevel.equals(newLevel));
        config.setLogLevel(loggerName, newLevel);
        Assert.assertEquals("Wrong log level config after set: ", newLevel, config.getLogLevel(loggerName));
        Logger logger = Logger.getLogger(loggerName);
        Assert.assertEquals("Wrong Log4j log level after set: ", newLevel, logger.getLevel().toString());
    }

    @Test
    public void setLevelNonExistingLoggerName() {
        String loggerName = "foo";
        String oldLevel = config.getLogLevel(loggerName);
        Assert.assertEquals("Wrong level before set: ", "unavailable", oldLevel);
        String newLevel = "TRACE";
        config.setLogLevel(loggerName, newLevel);
        Assert.assertEquals("Expected one logger to have been created: ", EXPECTED_LOGGERS.length + 1,
                config.getLoggers().size());
        Assert.assertEquals("Wrong level after set: ", newLevel, config.getLogLevel(loggerName));
    }

    @Test
    public void setLevelNullLoggerName() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = config.getLogLevel(loggerName);
        config.setLogLevel(loggerName, null);
        Assert.assertEquals("Wrong log level after set: ", oldLevel, config.getLogLevel(loggerName));
    }

    @Test
    public void setLevelIncorrectLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        config.setLogLevel(loggerName, "foo");
        Assert.assertEquals("Wrong log level after set: ", "DEBUG", config.getLogLevel(loggerName));
    }

    @Test
    public void setLevelNullLevel() {
        String loggerName = EXPECTED_LOGGERS[0];
        String oldLevel = config.getLogLevel(loggerName);
        config.setLogLevel(loggerName, null);
        Assert.assertEquals("Wrong log level after set: ", oldLevel, config.getLogLevel(loggerName));
    }
}
