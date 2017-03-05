package com.reallifedeveloper.common.infrastructure.jmx;

/**
 * A JMX MBean interface for monitoring and controlling database connection pools.
 *
 * @author RealLifeDeveloper
 */
public interface ConnectionPoolConfiguratorMXBean {

    /**
     * Gives the URL used to connect to the database.
     *
     * @return the configured URL for this connection pool
     */
    String getUrl();

    /**
     * Gives the fully qualified Java class name of the JDBC driver used.
     *
     * @return the fully qualified JDBC driver name
     */
    String getDriverClassName();

    /**
     * Gives the current size of the pool.
     *
     * @return the current size of the pool
     */
    int getSize();

    /**
     * Gives the number of established but idle connections.
     *
     * @return the number of established but idle connections
     */
    int getIdle();

    /**
     * Gives the number of connections in use by the application.
     *
     * @return the number of connections in use by the application
     */
    int getActive();

    /**
     * Gives the number of threads waiting for a connection.
     *
     * @return the number of threads waiting for a connection
     */
    int getWaitCount();

    /**
     * Forces a check for resizing of the idle connections.
     */
    void checkIdle();

    /**
     * Forces an abandon check on the connection pool.
     * <p>
     * If connections that have been abandoned exists, they will be closed during this run.
     */
    void checkAbandoned();

    /**
     * Performs a validation on idle connections.
     */
    void testIdle();

    /**
     * Purges all connections in the pool. For connections currently in use, these connections will be
     * purged when returned on the pool. This call also purges connections that are idle and in the pool.
     * <p>
     * To only purge used/active connections see {@link #purgeOnReturn()}
     */
    void purge();

    /**
     * Purges connections when they are returned from the pool. This call does not purge idle connections
     * until they are used.
     * <p>
     * To purge idle connections see {@link #purge()}
     */
    void purgeOnReturn();

}
