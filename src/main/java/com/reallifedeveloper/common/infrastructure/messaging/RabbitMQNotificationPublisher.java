package com.reallifedeveloper.common.infrastructure.messaging;

import static com.reallifedeveloper.common.domain.LogUtil.removeCRLF;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationPublisher;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * An implementation of the {@link NotificationPublisher} interface that uses <a href="http://www.rabbitmq.com/">RabbiMQ</a>.
 *
 * @author RealLifeDeveloper
 */
public final class RabbitMQNotificationPublisher implements NotificationPublisher {

    private static final BasicProperties EMPTY_PROPERTIES = new BasicProperties();

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQNotificationPublisher.class);

    private final ConnectionFactory connectionFactory;

    private final ObjectSerializer<String> objectSerializer;

    /**
     * Creates a new {@code RabbitMQNotificationPublisher} that connects to RabbitMQ using the given {@code ConnectionFactory}.
     *
     * @param connectionFactory the {@code ConnectionFactory} to use to create connections to RabbitMQ
     * @param objectSerializer  the {@code ObjectSerializer} to use to serialize notifications
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The ConnectionFactory is mutable, but that is OK")
    public RabbitMQNotificationPublisher(ConnectionFactory connectionFactory, ObjectSerializer<String> objectSerializer) {
        ErrorHandling.checkNull("Arguments must not be null: connectionFactory=%s, objectSerializer=%s", connectionFactory,
                objectSerializer);
        this.connectionFactory = connectionFactory;
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void publish(List<Notification> notifications, String publicationChannel) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("publish: notifications={}, publicationChannel={}", removeCRLF(notifications), removeCRLF(publicationChannel));
        }
        ErrorHandling.checkNull("Arguments must not be null: notifications=%s, publicationChannel=%s", notifications, publicationChannel);
        if (!notifications.isEmpty()) {
            try (Connection connection = connectionFactory.newConnection(); Channel channel = connection.createChannel()) {
                for (Notification notification : notifications) {
                    String message = objectSerializer.serialize(notification);
                    channel.basicPublish(publicationChannel, "", EMPTY_PROPERTIES, message.getBytes("UTF-8"));
                }
            } catch (TimeoutException e) {
                throw new IOException("Timeout occurred", e);
            }
        }
    }
}
