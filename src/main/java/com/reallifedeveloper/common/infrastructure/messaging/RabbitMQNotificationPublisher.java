package com.reallifedeveloper.common.infrastructure.messaging;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationPublisher;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * An implementation of the {@link NotificationPublisher} interface which uses
 * <a href="http://www.rabbitmq.com/">RabbiMQ</a>.
 *
 * @author RealLifeDeveloper
 */
public class RabbitMQNotificationPublisher implements NotificationPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQNotificationPublisher.class);

    private String host;
    private int port;
    private String username;
    private String password;

    @Autowired
    private ObjectSerializer<String> objectSerializer;

    /**
     * Creates a new <code>RabbitMQNotificationPublisher</code> that connects to RabbitMQ on the given host,
     * and uses the given {@link ObjectSerializer} to serialize notifications.
     *
     * @param host the name of the host where RabbitMQ runs
     * @param objectSerializer the <code>ObjectSerializer</code> to use to serialize notifications
     */
    public RabbitMQNotificationPublisher(String host, ObjectSerializer<String> objectSerializer) {
        this(host, ConnectionFactory.DEFAULT_AMQP_PORT, objectSerializer);
    }

    /**
     * Creates a new <code>RabbitMQNotificationPublisher</code> that connects to RabbitMQ on the given host
     * and port, and uses the given {@link ObjectSerializer} to serialize notifications.
     *
     * @param host the name of the host where RabbitMQ runs
     * @param port the port number that RabbitMQ listens to
     * @param objectSerializer the <code>ObjectSerializer</code> to use to serialize notifications
     */
    public RabbitMQNotificationPublisher(String host, int port, ObjectSerializer<String> objectSerializer) {
        if (host == null || objectSerializer == null) {
            throw new IllegalArgumentException("Arguments must not be null: host=" + host + ", port=" + port
                    + ", objectSerializer=" + objectSerializer);
        }
        this.host = host;
        this.port = port;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Creates a new <code>RabbitMQNotificationPublisher</code> that connects to RabbitMQ on the given host
     * and port using the given username and password, and uses the given {@link ObjectSerializer} to
     * serialize notifications.
     *
     * @param host the name of the host where RabbitMQ runs
     * @param port the port number that RabbitMQ listens to
     * @param username the username to use when connecting to RabbitMQ
     * @param password the password to use when connecting to RabbitMQ
     * @param objectSerializer the <code>ObjectSerializer</code> to use to serialize notifications
     */
    public RabbitMQNotificationPublisher(String host, int port, String username, String password,
            ObjectSerializer<String> objectSerializer) {
        if (host == null || username == null || password == null || objectSerializer == null) {
            throw new IllegalArgumentException("Arguments must not be null: host=" + host + ", port=" + port
                    + ", username=" + username + ", password=" + (password == null ? null : "****")
                    + ", objectSerializer=" + objectSerializer);
        }
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Creates a new <code>RabbitMQNotificationPublisher</code> that connects to RabbitMQ on the given host.
     * The {@link ObjectSerializer} to use is assumed to be injected by Spring.
     *
     * @param host the name of the host where RabbitMQ runs
     */
    public RabbitMQNotificationPublisher(String host) {
        this(host, ConnectionFactory.DEFAULT_AMQP_PORT);
    }

    /**
     * Creates a new <code>RabbitMQNotificationPublisher</code> that connects to RabbitMQ on the given host
     * and port. The {@link ObjectSerializer} to use is assumed to be injected by Spring.
     *
     * @param host the name of the host where RabbitMQ runs
     * @param port the port number that RabbitMQ listens to
     */
    public RabbitMQNotificationPublisher(String host, int port) {
        if (host == null) {
            throw new IllegalArgumentException("host must not be null");
        }
        this.host = host;
        this.port = port;
    }

    /**
     * Creates a new <code>RabbitMQNotificationPublisher</code> that connects to RabbitMQ on the given host
     * and port. The {@link ObjectSerializer} to use is assumed to be injected by Spring.
     *
     * @param host the name of the host where RabbitMQ runs
     * @param port the port number that RabbitMQ listens to
     * @param username the username to use when connecting to RabbitMQ
     * @param password the password to use when connecting to RabbitMQ
     */
    public RabbitMQNotificationPublisher(String host, int port, String username, String password) {
        if (host == null || username == null || password == null) {
            throw new IllegalArgumentException("Arguments must not be null: host=" + host + ", port=" + port
                    + ", username=" + username + ", password=" + (password == null ? null : "****"));
        }
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    RabbitMQNotificationPublisher() {
        super();
    }

    @Override
    public void publish(List<Notification> notifications, String publicationChannel) throws IOException {
        LOG.trace("publish: notifications={}, publicationChannel={}", notifications, publicationChannel);
        if (notifications == null || publicationChannel == null) {
            throw new IllegalArgumentException("Arguments must not be null: notifications=" + notifications
                    + ", publicationChannel=" + publicationChannel);
        }
        if (!notifications.isEmpty()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(this.host);
            factory.setPort(this.port);
            if (username != null) {
                factory.setUsername(username);
            }
            if (password != null) {
                factory.setPassword(password);
            }
            Connection connection = null;
            Channel channel = null;
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                for (Notification notification : notifications) {
                    String message = objectSerializer.serialize(notification);
                    channel.basicPublish(publicationChannel, "", null, message.getBytes("UTF-8"));
                }
            } catch (TimeoutException e) {
                throw new IOException("Timeout occurred", e);
            } finally {
                close(channel);
                close(connection);
            }
        }
    }

    private static void close(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                LOG.warn("Failed to close channel", e);
            }
        }
    }

    private static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                LOG.warn("Failed to close connection", e);
            }
        }
    }
}
