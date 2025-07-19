package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

public class KafkaNotificationPublisherTest {

    private static Long nextStoredEventId = 0L;

    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    public void init() {
        kafkaTemplate = EasyMock.mock(KafkaTemplate.class);
    }

    @Test
    public void publishingNotificationShouldCallKafkaTemplateSend() throws Exception {
        ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
        KafkaNotificationPublisher notificationPublisher = new KafkaNotificationPublisher(kafkaTemplate, objectSerializer);
        TestEvent event1 = new TestEvent(42, "foo");
        TestEvent event2 = new TestEvent(4711, "bar");
        List<Notification> notifications = toNotifications(event1, event2);
        EasyMock.expect(
                kafkaTemplate.send("channel", event1.getClass().getCanonicalName(), objectSerializer.serialize(notifications.get(0))))
                .andReturn(null);
        EasyMock.expect(
                kafkaTemplate.send("channel", event1.getClass().getCanonicalName(), objectSerializer.serialize(notifications.get(1))))
                .andReturn(null);
        EasyMock.replay(kafkaTemplate);
        notificationPublisher.publish(notifications, "channel");
        EasyMock.verify(kafkaTemplate);
    }

    public static List<Notification> toNotifications(DomainEvent... domainEvents) {
        return Arrays.stream(domainEvents).map(de -> Notification.create(de, nextStoredEventId++)).toList();
    }

    @Test
    public void creatingPublisherWithNullKafkaTemplateShouldFail() {
        ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
        Exception e = assertThrows(IllegalArgumentException.class, () -> new KafkaNotificationPublisher(null, objectSerializer));
        assertEquals("Arguments must not be null: kafkaTemplate=null, objectSerializer=%s".formatted(objectSerializer), e.getMessage());
    }

    @Test
    public void creatingPublisherWithNullObjectSerializerShouldFail() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new KafkaNotificationPublisher(kafkaTemplate, null));
        assertEquals("Arguments must not be null: kafkaTemplate=%s, objectSerializer=null".formatted(kafkaTemplate), e.getMessage());
    }

}
