package com.reallifedeveloper.common.infrastructure.messaging;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationPublisher;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * An implementation of the {@link NotificationPublisher} interface which uses <a href="http://www.rabbitmq.com/">RabbiMQ</a>.
 *
 * @author RealLifeDeveloper
 */
public final class RabbitMQNotificationPublisher implements NotificationPublisher {

    private static final BasicProperties EMPTY_PROPERTIES = new BasicProperties();

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQNotificationPublisher.class);

    private final String host;
    private final int port;
    private final @Nullable String username;
    private final @Nullable String password;

    private final ObjectSerializer<String> objectSerializer;

    /**
     * Creates a new {@code RabbitMQNotificationPublisher} that connects to RabbitMQ on the given host, and uses the given
     * {@link ObjectSerializer} to serialize notifications.
     *
     * @param host             the name of the host where RabbitMQ runs
     * @param objectSerializer the {@code ObjectSerializer} to use to serialize notifications
     */
    public RabbitMQNotificationPublisher(String host, ObjectSerializer<String> objectSerializer) {
        this(host, ConnectionFactory.DEFAULT_AMQP_PORT, objectSerializer);
    }

    /**
     * Creates a new {@code RabbitMQNotificationPublisher} that connects to RabbitMQ on the given host and port, and uses the given
     * {@link ObjectSerializer} to serialize notifications.
     *
     * @param host             the name of the host where RabbitMQ runs
     * @param port             the port number that RabbitMQ listens to
     * @param objectSerializer the {@code ObjectSerializer} to use to serialize notifications
     */
    public RabbitMQNotificationPublisher(String host, int port, ObjectSerializer<String> objectSerializer) {
        ErrorHandling.checkNull("Arguments must not be null: host=%s, port=%s, objectSerializer=%s", host, port, objectSerializer);
        this.host = host;
        this.port = port;
        this.username = null;
        this.password = null;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Creates a new {@code RabbitMQNotificationPublisher} that connects to RabbitMQ on the given host and port using the given username and
     * password, and uses the given {@link ObjectSerializer} to serialize notifications.
     *
     * @param host             the name of the host where RabbitMQ runs
     * @param port             the port number that RabbitMQ listens to
     * @param username         the username to use when connecting to RabbitMQ
     * @param password         the password to use when connecting to RabbitMQ
     * @param objectSerializer the {@code ObjectSerializer} to use to serialize notifications
     */
    public RabbitMQNotificationPublisher(String host, int port, String username, String password,
            ObjectSerializer<String> objectSerializer) {
        ErrorHandling.checkNull("Arguments must not be null: host=%s, port=%s, username=%s, password=%s, objectSerializer=%s", host, port,
                username, password, objectSerializer);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void publish(List<Notification> notifications, String publicationChannel) throws IOException {
        LOG.trace("publish: notifications={}, publicationChannel={}", notifications, publicationChannel);
        ErrorHandling.checkNull("Arguments must not be null: notifications=%s, publicationChannel=%s", notifications, publicationChannel);
        if (!notifications.isEmpty()) {
            ConnectionFactory factory = createConnectionFactory();
            try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
                for (Notification notification : notifications) {
                    String message = objectSerializer.serialize(notification);
                    channel.basicPublish(publicationChannel, "", EMPTY_PROPERTIES, message.getBytes("UTF-8"));
                }
            } catch (TimeoutException e) {
                throw new IOException("Timeout occurred", e);
            }
        }
    }

    private ConnectionFactory createConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.host);
        factory.setPort(this.port);
        if (username != null) {
            factory.setUsername(username);
        }
        if (password != null) {
            factory.setPassword(password);
        }
        return factory;
    }
}
