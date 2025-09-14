package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import com.reallifedeveloper.common.application.eventstore.EventStore;
import com.reallifedeveloper.common.application.eventstore.InMemoryStoredEventRepository;
import com.reallifedeveloper.common.application.eventstore.StoredEvent;
import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationFactory;
import com.reallifedeveloper.common.application.notification.NotificationReader;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonNotificationReader;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;
import com.reallifedeveloper.tools.test.TestUtil;

@Disabled("Since this requires RabbitMQ to be instaled on localhost, with the guest user and virtual host '/'")
public class RabbitMQNotificationPublisherIT {

    private static final String EXCHANGE_NAME = "reallifedeveloper.common.test";
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQNotificationPublisherIT.class);

    private final InMemoryStoredEventRepository storedEventRepository = new InMemoryStoredEventRepository();
    private final ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
    private final EventStore eventStore = new EventStore(objectSerializer, storedEventRepository);
    private final NotificationFactory notificationFactory = NotificationFactory.instance(eventStore);

    @Test
    public void singleNotificationSingleListener() throws Exception {
        QueueListener listener = new QueueListener("listener1");
        listener.start();
        List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
        RabbitMQNotificationPublisher publisher = createPublisher("localhost", 5672, "guest", "guest");
        publisher.publish(notifications, EXCHANGE_NAME);
        sleep();
        assertEquals(1, listener.messages().size(), "Wrong number of received messages");
        for (String message : listener.messages()) {
            NotificationReader reader = new GsonNotificationReader(message);
            assertEquals(TestEvent.class.getName(), reader.eventType(), "Wrong notification event type");
            assertEquals(42, reader.eventIntValue("id").orElseThrow(), "Wrong notification event id");
            assertEquals("foo", reader.eventStringValue("name").orElseThrow(), "Wrong notification event name");
        }
    }

    @Test
    public void multipleNotificationsMultipleListeners() throws Exception {
        QueueListener listener1 = new QueueListener("listener1");
        listener1.start();

        List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
        RabbitMQNotificationPublisher publisher = createPublisher("localhost", 5672, "guest", "guest");
        publisher.publish(notifications, EXCHANGE_NAME);
        sleep();
        assertEquals(1, listener1.messages().size(), "Wrong number of received messages for listener 1");

        QueueListener listener2 = new QueueListener("listener2");
        listener2.start();

        eventStore.add(new TestEvent(4711, "bar"));
        List<StoredEvent> storedEvents = eventStore.allEventsSince(1);
        assertEquals(1, storedEvents.size(), "Wrong number of stored events");
        notifications = notificationFactory.fromStoredEvents(storedEvents);
        publisher.publish(notifications, EXCHANGE_NAME);
        sleep();
        assertEquals(2, listener1.messages().size(), "Wrong number of received messages for listener 1");
        assertEquals(1, listener2.messages().size(), "Wrong number of received messages for listener 2");
    }

    @Test
    public void constructorSingleArgumentWrongHost() {
        assertThrows(UnknownHostException.class, () -> {
            RabbitMQNotificationPublisher publisher = createPublisher("localhostX", 5672, "guest", "guest");
            List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
            publisher.publish(notifications, EXCHANGE_NAME);
        });
    }

    @Test
    public void constructorHostPortWrongPort() {
        assertThrows(ConnectException.class, () -> {
            RabbitMQNotificationPublisher publisher = createPublisher("localhost", TestUtil.findFreePort(), "guest", "guest");
            List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
            publisher.publish(notifications, EXCHANGE_NAME);
        });
    }

    private RabbitMQNotificationPublisher createPublisher(String host, int port, String username, String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return new RabbitMQNotificationPublisher(factory, objectSerializer);
    }

    private static void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            LOG.warn("Thread.sleep interrupted", e);
        }
    }

    private List<Notification> createNotifications(DomainEvent... events) {
        for (DomainEvent event : events) {
            eventStore.add(event);
        }
        List<StoredEvent> storedEvents = eventStore.allEventsSince(0);
        assertEquals(events.length, storedEvents.size(), "Wrong number of stored events");
        return notificationFactory.fromStoredEvents(storedEvents);
    }

    private static class QueueListener extends Thread {

        private final DefaultConsumer consumer;
        private final List<String> messages = new ArrayList<>();

        QueueListener(String name) throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    LOG.debug(name + ": Message received: " + message);
                    messages.add(message);
                }
            };
            channel.basicConsume(queueName, true, consumer);
        }

        synchronized List<String> messages() {
            return Collections.unmodifiableList(messages);
        }
    }
}
