package com.reallifedeveloper.common.infrastructure.jmx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TomcatConnectionPoolConfiguratorTest {

    private static final String JDBC_URL = "jdbc:hsqldb:mem:.";
    private static final String JDBC_DRIVER_CLASSNAME = "org.hsqldb.jdbcDriver";
    private static final int INITIAL_SIZE = 5;
    private static final int MAX_ACTIVE = 10;
    private static final int MAX_IDLE = 5;
    private static final int MIN_IDLE = 2;

    private TomcatConnectionPoolConfigurator connectionPoolconfigurator;

    @BeforeEach
    public void init() {
        PoolConfiguration poolConfiguration = new PoolProperties();
        poolConfiguration.setUrl(JDBC_URL);
        poolConfiguration.setDriverClassName(JDBC_DRIVER_CLASSNAME);
        poolConfiguration.setInitialSize(INITIAL_SIZE);
        poolConfiguration.setMaxActive(MAX_ACTIVE);
        poolConfiguration.setMaxIdle(MAX_IDLE);
        poolConfiguration.setMinIdle(MIN_IDLE);
        poolConfiguration.setTestWhileIdle(true);
        poolConfiguration.setTestOnBorrow(true);
        DataSource ds = new DataSource(poolConfiguration);
        connectionPoolconfigurator = new TomcatConnectionPoolConfigurator(ds);
    }

    @Test
    public void getUrl() {
        assertEquals(JDBC_URL, connectionPoolconfigurator.getUrl(), "Wrong URL: ");
    }

    @Test
    public void getDriverClassName() {
        assertEquals(JDBC_DRIVER_CLASSNAME, connectionPoolconfigurator.getDriverClassName(),
                "Wrong driver classname: ");
    }

    @Test
    public void getSize() {
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getSize(), "Wrong pool size: ");
    }

    @Test
    public void getIdle() {
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getIdle(), "Wrong number of idle connections: ");
    }

    @Test
    public void getActive() {
        assertEquals(0, connectionPoolconfigurator.getActive(), "Wrong number of active connections: ");
    }

    @Test
    public void getWaitCount() {
        assertEquals(0, connectionPoolconfigurator.getWaitCount(), "Wrong number of waiting threads: ");
    }

    @Test
    public void checkIdle() {
        connectionPoolconfigurator.checkIdle();
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getSize(), "Wrong pool size: ");
    }

    @Test
    public void checkAbandoned() {
        connectionPoolconfigurator.checkAbandoned();
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getSize(), "Wrong pool size: ");
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getIdle(), "Wrong number of idle connections: ");
    }

    @Test
    public void testIdle() {
        connectionPoolconfigurator.testIdle();
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getSize(), "Wrong pool size: ");
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getIdle(), "Wrong number of idle connections: ");
    }

    @Test
    public void purge() {
        connectionPoolconfigurator.purge();
        assertEquals(0, connectionPoolconfigurator.getSize(), "Wrong pool size: ");
        assertEquals(0, connectionPoolconfigurator.getIdle(), "Wrong number of idle connections: ");
    }

    @Test
    public void purgeOnReturn() {
        connectionPoolconfigurator.purgeOnReturn();
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getSize(), "Wrong pool size: ");
        assertEquals(INITIAL_SIZE, connectionPoolconfigurator.getIdle(), "Wrong number of idle connections: ");
    }

    @Test
    public void constructorNullDataSource() {
        assertThrows(IllegalArgumentException.class, () -> new TomcatConnectionPoolConfigurator(null),
                "Expected constructor to throw IllegalArgumentException for null DataSource");
    }
}
