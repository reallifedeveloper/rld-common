package com.reallifedeveloper.common.infrastructure.messaging;

import java.util.Collections;

import org.junit.Test;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class RabbitMQNotificationPublisherTest {

    private ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @Test
    public void publishEmptyListDoesNothing() throws Exception {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password");
        publisher.publish(Collections.emptyList(), "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void publishNullNotifications() throws Exception {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password");
        publisher.publish(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void publishNullPublicationChannel() throws Exception {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password", objectSerializer);
        publisher.publish(Collections.emptyList(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorTwoArgumentsWithObjectSerializerNullHost() {
        new RabbitMQNotificationPublisher(null, objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorTwoArgumentsWithNullObjectSerializer() {
        new RabbitMQNotificationPublisher("localhost", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThreeArgumentsHostNull() {
        new RabbitMQNotificationPublisher(null, 4711, objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThreeArgumentsObjectSerializerNull() {
        new RabbitMQNotificationPublisher("localhost", 4711, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFiveArgumentsHostNull() {
        new RabbitMQNotificationPublisher(null, 4711, "usrname", "password", objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFiveArgumentsUsernameNull() {
        new RabbitMQNotificationPublisher("localhost", 4711, null, "password", objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFiveArgumentsPasswordNull() {
        new RabbitMQNotificationPublisher("localhost", 4711, "username", null, objectSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFiveArgumentsObjectSerializerNull() {
        new RabbitMQNotificationPublisher("localhost", 4711, "username", "password", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorSingleArgumentHostNull() {
        new RabbitMQNotificationPublisher(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorHostPortHostNull() {
        new RabbitMQNotificationPublisher(null, 4711);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFourArgumentsHostNull() {
        new RabbitMQNotificationPublisher(null, 4711, "username", "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFourArgumentsUsernameNull() {
        new RabbitMQNotificationPublisher("localhost", 4711, null, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFourArgumentsPasswordNull() {
        new RabbitMQNotificationPublisher("localhost", 4711, "username", null);
    }

    @Test
    public void constructorNoArgumentsDoesNotThrowException() {
        new RabbitMQNotificationPublisher();
    }

}
