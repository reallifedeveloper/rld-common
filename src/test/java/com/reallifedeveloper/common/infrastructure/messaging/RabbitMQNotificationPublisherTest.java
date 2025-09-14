package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;
import com.reallifedeveloper.tools.test.LogbackTestUtil;

public class RabbitMQNotificationPublisherTest {

    private ConnectionFactory connectionFactory;
    private Channel channel;
    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @BeforeEach
    public void init() throws Exception {
        this.connectionFactory = EasyMock.mock(ConnectionFactory.class);
        Connection connection = EasyMock.mock(Connection.class);
        this.channel = EasyMock.mock(Channel.class);
        EasyMock.expect(connectionFactory.newConnection()).andReturn(connection);
        EasyMock.expect(connection.createChannel()).andReturn(channel);
        connection.close();
        EasyMock.replay(connectionFactory, connection);
    }

    @Test
    public void publishNotificationsShouldCallChannelBasicPublish() throws Exception {
        // Given
        RabbitMQNotificationPublisher notificationPublisher = new RabbitMQNotificationPublisher(connectionFactory, objectSerializer);
        List<Notification> notifications = testNotifications();

        channel.basicPublish("channel", "", new BasicProperties(),
                objectSerializer.serialize(notifications.get(0)).getBytes(StandardCharsets.UTF_8));
        channel.basicPublish("channel", "", new BasicProperties(),
                objectSerializer.serialize(notifications.get(1)).getBytes(StandardCharsets.UTF_8));
        channel.close();

        EasyMock.replay(channel);

        // When
        notificationPublisher.publish(Collections.emptyList(), "channel"); // An empty list does nothing
        notificationPublisher.publish(notifications, "channel");

        // Then
        EasyMock.verify(channel);
    }

    @Test
    public void publishNotificationsShouldThrowIOExceptionIfConnectionTimesOut() throws Exception {
        // Given
        TimeoutException originalException = new TimeoutException("foo");
        ConnectionFactory throwingConnectionFactory = EasyMock.mock(ConnectionFactory.class);
        EasyMock.expect(throwingConnectionFactory.newConnection()).andThrow(originalException);
        EasyMock.replay(throwingConnectionFactory);
        RabbitMQNotificationPublisher notificationPublisher = new RabbitMQNotificationPublisher(throwingConnectionFactory,
                objectSerializer);

        // When
        notificationPublisher.publish(Collections.emptyList(), "channel"); // An empty list does not throw
        IOException e = assertThrows(IOException.class, () -> notificationPublisher.publish(testNotifications(), "channel"));

        // Then
        assertEquals("Timeout occurred", e.getMessage());
        assertEquals(originalException, e.getCause());
    }

    @Test
    public void publishNotificationsShouldThrowOriginalExceptionIfBasicPublishFails() throws Exception {
        // Given
        List<Notification> notifications = testNotifications();
        IOException originalException = new IOException("foo");
        channel.basicPublish("channel", "", new BasicProperties(),
                objectSerializer.serialize(notifications.get(0)).getBytes(StandardCharsets.UTF_8));
        EasyMock.expectLastCall().andThrow(originalException);
        channel.close();
        EasyMock.replay(channel);
        RabbitMQNotificationPublisher notificationPublisher = new RabbitMQNotificationPublisher(connectionFactory, objectSerializer);

        // When
        notificationPublisher.publish(Collections.emptyList(), "channel"); // An empty list does not throw
        IOException e = assertThrows(IOException.class, () -> notificationPublisher.publish(notifications, "channel"));

        // Then
        assertEquals(originalException, e);
    }

    private static List<Notification> testNotifications() {
        TestEvent event1 = new TestEvent(42, "foo");
        TestEvent event2 = new TestEvent(4711, "bar");
        List<Notification> notifications = KafkaNotificationPublisherTest.toNotifications(event1, event2);
        return notifications;
    }

    @Test
    @SuppressWarnings("NullAway")
    public void creatingPublisherWithNullConnectionFactoryShouldFail() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new RabbitMQNotificationPublisher(null, objectSerializer));
        assertEquals("Arguments must not be null: connectionFactory=null, objectSerializer=" + objectSerializer, e.getMessage());
    }

    @Test
    @SuppressWarnings("NullAway")
    public void creatingPublisherWithNullObjectSerializerShouldFail() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new RabbitMQNotificationPublisher(connectionFactory, null));
        assertEquals("Arguments must not be null: connectionFactory=" + connectionFactory + ", objectSerializer=null", e.getMessage());
    }

    @Test
    public void verifyLogging() throws Exception {
        // Given
        LogbackTestUtil.clearLoggingEvents();
        Logger logger = (Logger) LoggerFactory.getLogger(RabbitMQNotificationPublisher.class);
        Level originalLevel = logger.getLevel();
        logger.setLevel(Level.TRACE);
        List<Notification> notifications = testNotifications();

        // When
        RabbitMQNotificationPublisher notificationPublisher = new RabbitMQNotificationPublisher(connectionFactory, objectSerializer);
        notificationPublisher.publish(notifications, "channel\n with newline");

        // Then
        List<ILoggingEvent> loggingEvents = LogbackTestUtil.getLoggingEvents();
        assertEquals(2, loggingEvents.size());
        KafkaNotificationPublisherTest.assertLogEntry(loggingEvents.get(0), Level.INFO,
                "Creating new RabbitMQNotificationPublisher: connectionFactory=" + connectionFactory + ", objectSerializer="
                        + objectSerializer);
        KafkaNotificationPublisherTest.assertLogEntry(loggingEvents.get(1), Level.TRACE,
                "publish: notifications=" + notifications + ", publicationChannel=channel with newline");

        logger.setLevel(originalLevel);
    }

}
