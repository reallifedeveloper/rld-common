package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.DomainEvent;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;
import com.reallifedeveloper.tools.test.LogbackTestUtil;

public class KafkaNotificationPublisherTest {

    private static Long nextStoredEventId = 0L;

    private final KafkaTemplate<String, String> kafkaTemplate = EasyMock.mock(KafkaTemplate.class);
    private final ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();

    @BeforeEach
    public void init() {
        // kafkaTemplate = EasyMock.mock(KafkaTemplate.class);
    }

    @Test
    public void publishingNotificationShouldCallKafkaTemplateSend() throws Exception {
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
        Exception e = assertThrows(IllegalArgumentException.class, () -> new KafkaNotificationPublisher(null, objectSerializer));
        assertEquals("Arguments must not be null: kafkaTemplate=null, objectSerializer=%s".formatted(objectSerializer), e.getMessage());
    }

    @Test
    public void creatingPublisherWithNullObjectSerializerShouldFail() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new KafkaNotificationPublisher(kafkaTemplate, null));
        assertEquals("Arguments must not be null: kafkaTemplate=%s, objectSerializer=null".formatted(kafkaTemplate), e.getMessage());
    }

    @Test
    public void verifyLogging() throws Exception {
        // Given
        LogbackTestUtil.clearLoggingEvents();
        Logger logger = (Logger) LoggerFactory.getLogger(KafkaNotificationPublisher.class);
        Level originalLevel = logger.getLevel();
        logger.setLevel(Level.TRACE);
        List<Notification> notifications = toNotifications(new TestEvent(42, "foo"));

        // When
        KafkaNotificationPublisher notificationPublisher = new KafkaNotificationPublisher(kafkaTemplate, objectSerializer);
        notificationPublisher.publish(notifications, "channel\n with newline");

        // Then
        List<ILoggingEvent> loggingEvents = LogbackTestUtil.getLoggingEvents();
        assertEquals(2, loggingEvents.size());
        assertLogEntry(loggingEvents.get(0), Level.INFO,
                "Creating new KafkaNotificationPublisher: kafkaTemplate=" + kafkaTemplate + ", objectSerializer=" + objectSerializer);
        assertLogEntry(loggingEvents.get(1), Level.TRACE,
                "publish: notifications=" + notifications + ", publicationChannel=channel with newline");

        logger.setLevel(originalLevel);
    }

    public static void assertLogEntry(ILoggingEvent loggingEvent, Level level, String message) {
        assertEquals(level, loggingEvent.getLevel());
        assertEquals(message, loggingEvent.getFormattedMessage());
    }

}
