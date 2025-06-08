package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class RabbitMQNotificationPublisherTest {

    private final ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @Test
    public void publishEmptyListDoesNothing() throws Exception {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password", objectSerializer);
        publisher.publish(Collections.emptyList(), "foo");
    }

    @Test
    public void publishNullNotifications() {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password", objectSerializer);
        assertThrows(IllegalArgumentException.class, () ->
                publisher.publish(null, "foo"), "Expected IllegalArgumentException for null notifications");
    }

    @Test
    public void publishNullPublicationChannel() {
        RabbitMQNotificationPublisher publisher =
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password", objectSerializer);
        assertThrows(IllegalArgumentException.class, () ->
                publisher.publish(Collections.emptyList(), null), "Expected IllegalArgumentException for null publication channel");
    }

    @Test
    public void constructorTwoArgumentsWithObjectSerializerNullHost() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher(null, objectSerializer), "Expected IllegalArgumentException for null host");
    }

    @Test
    public void constructorTwoArgumentsWithNullObjectSerializer() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher("localhost", null), "Expected IllegalArgumentException for null object serializer");
    }

    @Test
    public void constructorThreeArgumentsHostNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher(null, 4711, objectSerializer),
                 "Expected IllegalArgumentException for null host in three arguments");
    }

    @Test
    public void constructorThreeArgumentsObjectSerializerNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher("localhost", 4711, null),
                 "Expected IllegalArgumentException for null object serializer in three arguments");
    }

    @Test
    public void constructorFiveArgumentsHostNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher(null, 4711, "username", "password", objectSerializer),
                 "Expected IllegalArgumentException for null host in five arguments");
    }

    @Test
    public void constructorFiveArgumentsUsernameNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher("localhost", 4711, null, "password", objectSerializer),
                 "Expected IllegalArgumentException for null username in five arguments");
    }

    @Test
    public void constructorFiveArgumentsPasswordNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher("localhost", 4711, "username", null, objectSerializer),
                 "Expected IllegalArgumentException for null password in five arguments");
    }

    @Test
    public void constructorFiveArgumentsObjectSerializerNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new RabbitMQNotificationPublisher("localhost", 4711, "username", "password", null),
                 "Expected IllegalArgumentException for null object serializer in five arguments");
    }
}
