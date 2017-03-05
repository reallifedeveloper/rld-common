package com.reallifedeveloper.common.infrastructure.jmx;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TomcatConnectionPoolConfiguratorTest {

    private static final String JDBC_URL = "jdbc:hsqldb:mem:.";
    private static final String JDBC_DRIVER_CLASSNAME = "org.hsqldb.jdbcDriver";
    private static final int INITIAL_SIZE = 5;
    private static final int MAX_ACTIVE = 10;
    private static final int MAX_IDLE = 5;
    private static final int MIN_IDLE = 2;

    private TomcatConnectionPoolConfigurator connectionPoolconfigurator;

    @Before
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
        Assert.assertEquals("Wrong URL: ", JDBC_URL, connectionPoolconfigurator.getUrl());
    }

    @Test
    public void getDriverClassName() {
        Assert.assertEquals("Wrong driver classname: ", JDBC_DRIVER_CLASSNAME,
                connectionPoolconfigurator.getDriverClassName());
    }

    @Test
    public void getSize() {
        Assert.assertEquals("Wrong pool size: ", INITIAL_SIZE, connectionPoolconfigurator.getSize());
    }

    @Test
    public void getIdle() {
        Assert.assertEquals("Wrong number of idle connections: ", INITIAL_SIZE, connectionPoolconfigurator.getIdle());
    }

    @Test
    public void getActive() {
        Assert.assertEquals("Wrong number of active connections: ", 0, connectionPoolconfigurator.getActive());
    }

    @Test
    public void getWaitCount() {
        Assert.assertEquals("Wrong number of waiting threads: ", 0, connectionPoolconfigurator.getWaitCount());
    }

    @Test
    public void checkIdle() {
        connectionPoolconfigurator.checkIdle();
        Assert.assertEquals("Wrong pool size: ", INITIAL_SIZE, connectionPoolconfigurator.getSize());
    }

    @Test
    public void checkAbandoned() {
        connectionPoolconfigurator.checkAbandoned();
        Assert.assertEquals("Wrong pool size: ", INITIAL_SIZE, connectionPoolconfigurator.getSize());
        Assert.assertEquals("Wrong number of idle connections: ", INITIAL_SIZE, connectionPoolconfigurator.getIdle());
    }

    @Test
    public void testIdle() {
        connectionPoolconfigurator.testIdle();
        Assert.assertEquals("Wrong pool size: ", INITIAL_SIZE, connectionPoolconfigurator.getSize());
        Assert.assertEquals("Wrong number of idle connections: ", INITIAL_SIZE, connectionPoolconfigurator.getIdle());
    }

    @Test
    public void purge() {
        connectionPoolconfigurator.purge();
        Assert.assertEquals("Wrong pool size: ", 0, connectionPoolconfigurator.getSize());
        Assert.assertEquals("Wrong number of idle connections: ", 0, connectionPoolconfigurator.getIdle());
    }

    @Test
    public void purgeOnReturn() {
        connectionPoolconfigurator.purgeOnReturn();
        Assert.assertEquals("Wrong pool size: ", INITIAL_SIZE, connectionPoolconfigurator.getSize());
        Assert.assertEquals("Wrong number of idle connections: ", INITIAL_SIZE, connectionPoolconfigurator.getIdle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDataSource() {
        new TomcatConnectionPoolConfigurator(null);
    }
}
