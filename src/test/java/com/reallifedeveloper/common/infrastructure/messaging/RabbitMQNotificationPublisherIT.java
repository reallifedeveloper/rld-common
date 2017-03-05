package com.reallifedeveloper.common.infrastructure.messaging;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

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

public class RabbitMQNotificationPublisherIT {

    private static final String EXCHANGE_NAME = "reallifedeveloper.common.test";
    private static final Logger LOG = Logger.getLogger(RabbitMQNotificationPublisherIT.class);

    private InMemoryStoredEventRepository storedEventRepository = new InMemoryStoredEventRepository();
    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
    private EventStore eventStore = new EventStore(objectSerializer, storedEventRepository);
    private NotificationFactory notificationFactory = NotificationFactory.instance(eventStore);

    @Test
    public void singleNotificationSingleListener() throws Exception {
        QueueListener listener = new QueueListener("listener1");
        listener.start();
        List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 5672, "guest", "guest", objectSerializer);
        publisher.publish(notifications, EXCHANGE_NAME);
        sleep();
        Assert.assertEquals("Wrong number of received messages: ", 1, listener.messages().size());
        for (String message : listener.messages()) {
            NotificationReader reader = new GsonNotificationReader(message);
            Assert.assertEquals("Wrong notification event type: ", TestEvent.class.getName(), reader.eventType());
            Assert.assertEquals("Wrong notification event id: ", 42, reader.eventIntValue("id").intValue());
            Assert.assertEquals("Wrong notification event name: ", "foo", reader.eventStringValue("name"));
        }
    }

    @Test
    public void multipleNotificationsMultipleListeners() throws Exception {
        QueueListener listener1 = new QueueListener("listener1");
        listener1.start();

        List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
        RabbitMQNotificationPublisher publisher = new RabbitMQNotificationPublisher("localhost", objectSerializer);
        publisher.publish(notifications, EXCHANGE_NAME);
        sleep();
        Assert.assertEquals("Wrong number of received messages for listener 1: ", 1, listener1.messages().size());

        QueueListener listener2 = new QueueListener("listener2");
        listener2.start();

        eventStore.add(new TestEvent(4711, "bar"));
        List<StoredEvent> storedEvents = eventStore.allEventsSince(1);
        Assert.assertEquals("Wrong number of stored events: ", 1, storedEvents.size());
        notifications = notificationFactory.fromStoredEvents(storedEvents);
        publisher.publish(notifications, EXCHANGE_NAME);
        sleep();
        Assert.assertEquals("Wrong number of received messages for listener 1: ", 2, listener1.messages().size());
        Assert.assertEquals("Wrong number of received messages for listener 2: ", 1, listener2.messages().size());
    }

    @Test(expected = UnknownHostException.class)
    public void constructorSingleArgumentWrongHost() throws Exception {
        RabbitMQNotificationPublisher publisher = new RabbitMQNotificationPublisher("localhostX");
        List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
        publisher.publish(notifications, EXCHANGE_NAME);
    }

    @Test(expected = ConnectException.class)
    public void constructorHostPortWrongPort() throws Exception {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", TestUtil.findFreePort());
        List<Notification> notifications = createNotifications(new TestEvent(42, "foo"));
        publisher.publish(notifications, EXCHANGE_NAME);
    }

    private void sleep() {
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
        Assert.assertEquals("Wrong number of stored events: ", events.length, storedEvents.size());
        return notificationFactory.fromStoredEvents(storedEvents);
    }

    private static class QueueListener extends Thread {

        private String name;
        private QueueingConsumer consumer;
        private List<String> messages = new ArrayList<>();

        QueueListener(String name) throws IOException {
            this.name = name;
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    LOG.debug(name + ": Waiting for messages");
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    String message = new String(delivery.getBody(), "UTF-8");
                    LOG.debug(name + ": Message received: " + message);
                    messages.add(message);
                }
            } catch (Exception e) {
                LOG.error(name + ": Unexpected problem in QueueListener", e);
            }
        }

        public synchronized List<String> messages() {
            return Collections.unmodifiableList(messages);
        }
    }
}
