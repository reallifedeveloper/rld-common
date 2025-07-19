package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

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
    public void publishNotificationShouldCallChannelBasicPublish() throws Exception {
        // Given
        RabbitMQNotificationPublisher notificationPublisher = new RabbitMQNotificationPublisher(connectionFactory, objectSerializer);
        TestEvent event1 = new TestEvent(42, "foo");
        TestEvent event2 = new TestEvent(4711, "bar");
        List<Notification> notifications = KafkaNotificationPublisherTest.toNotifications(event1, event2);

        channel.basicPublish("channel", "", new BasicProperties(),
                objectSerializer.serialize(notifications.get(0)).getBytes(StandardCharsets.UTF_8));
        channel.basicPublish("channel", "", new BasicProperties(),
                objectSerializer.serialize(notifications.get(1)).getBytes(StandardCharsets.UTF_8));
        channel.close();

        EasyMock.replay(channel);

        // When
        notificationPublisher.publish(notifications, "channel");

        // Then
        EasyMock.verify(channel);
    }

    @Test
    public void creatingPublisherWithNullConnectionFactoryShouldFail() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new RabbitMQNotificationPublisher(null, objectSerializer));
        assertEquals("Arguments must not be null: connectionFactory=null, objectSerializer=" + objectSerializer, e.getMessage());
    }

    @Test
    public void creatingPublisherWithNullObjectSerializerShouldFail() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new RabbitMQNotificationPublisher(connectionFactory, null));
        assertEquals("Arguments must not be null: connectionFactory=" + connectionFactory + ", objectSerializer=null", e.getMessage());
    }
}
