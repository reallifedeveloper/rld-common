package com.reallifedeveloper.common.infrastructure.jmx;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * An implementation of the JMX {@link ConnectionPoolConfiguratorMXBean} interface that delegates to an
 * <code>org.apache.tomcat.jdbc.pool.DataSource</code> object.
 *
 * @author RealLifeDeveloper
 */
@ManagedResource(description = "Connection Pool Configuration")
public class TomcatConnectionPoolConfigurator implements ConnectionPoolConfiguratorMXBean {

    private DataSource ds;

    /**
     * Creates a new <code>TomcatConnectionPoolConfigurator</code> that delegates to the given data source.
     *
     * @param ds the data source to delegate to
     */
    public TomcatConnectionPoolConfigurator(DataSource ds) {
        if (ds == null) {
            throw new IllegalArgumentException("ds must not be null");
        }
        this.ds = ds;
    }

    @Override
    @ManagedAttribute(description = "The URL used to connect to the database")
    public String getUrl() {
        return ds.getUrl();
    }

    @Override
    @ManagedAttribute(description = "The fully qualified JDBC driver name")
    public String getDriverClassName() {
        return ds.getDriverClassName();
    }

    @Override
    @ManagedAttribute(description = "The current size of the pool")
    public int getSize() {
        return ds.getSize();
    }

    @Override
    @ManagedAttribute(description = "The number of established but idle connections")
    public int getIdle() {
        return ds.getIdle();
    }

    @Override
    @ManagedAttribute(description = "The number of connections in use by the application")
    public int getActive() {
        return ds.getActive();
    }

    @Override
    @ManagedAttribute(description = "The number of threads waiting for a connection")
    public int getWaitCount() {
        return ds.getWaitCount();
    }

    @Override
    @ManagedOperation(description = "Forces a check for resizing of the idle connections")
    public void checkIdle() {
        ds.checkIdle();
    }

    @Override
    @ManagedOperation(description = "Forces an abandon check on the connection pool")
    public void checkAbandoned() {
        ds.checkAbandoned();
    }

    @Override
    @ManagedOperation(description = "Performs a validation on idle connections")
    public void testIdle() {
        ds.testIdle();
    }

    @Override
    @ManagedOperation(description = "Purges all connections in the pool")
    public void purge() {
        ds.purge();
    }

    @Override
    @ManagedOperation(description = "Purges connections when they are returned from the pool")
    public void purgeOnReturn() {
        ds.purgeOnReturn();
    }

}
