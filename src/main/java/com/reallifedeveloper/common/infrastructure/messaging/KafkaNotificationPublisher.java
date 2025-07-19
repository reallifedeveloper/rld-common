package com.reallifedeveloper.common.infrastructure.messaging;

import static com.reallifedeveloper.common.domain.LogUtil.removeCRLF;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.application.notification.NotificationPublisher;
import com.reallifedeveloper.common.domain.ErrorHandling;
import com.reallifedeveloper.common.domain.ObjectSerializer;

/**
 * An implementation of the {@link NotificationPublisher} interface which uses <a href="https://kafka.apache.org/">Apache Kafka</a>.
 *
 * @author RealLifeDeveloper
 */
public final class KafkaNotificationPublisher implements NotificationPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaNotificationPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectSerializer<String> objectSerializer;

    /**
     * Creates a new {@code KafkaNotificationPublisher} which uses the given {@code ObjectSerializer} to serialize notifications, and the
     * given {@code KafkaTemplate} to send them to Kafka.
     *
     * @param kafkaTemplate    the {@code KafkaTemplate} to use
     * @param objectSerializer the {@code ObjectSerializer} to use
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The KafkaTemplate is mutable, but that is OK")
    public KafkaNotificationPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectSerializer<String> objectSerializer) {
        ErrorHandling.checkNull("Arguments must not be null: kafkaTemplate=%s, objectSerializer=%s", kafkaTemplate, objectSerializer);
        this.kafkaTemplate = kafkaTemplate;
        this.objectSerializer = objectSerializer;
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // TODO: Remove this
    public void publish(List<Notification> notifications, String publicationChannel) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("publish: notifications={}, publicationChannel={}", removeCRLF(notifications), removeCRLF(publicationChannel));
        }
        for (Notification notification : notifications) {
            String key = notification.eventType();
            String message = objectSerializer.serialize(notification);
            kafkaTemplate.send(publicationChannel, key, message);
        }
    }

}
