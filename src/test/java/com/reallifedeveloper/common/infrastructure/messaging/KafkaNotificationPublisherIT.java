package com.reallifedeveloper.common.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.reallifedeveloper.common.application.notification.Notification;
import com.reallifedeveloper.common.domain.ObjectSerializer;
import com.reallifedeveloper.common.domain.event.TestEvent;
import com.reallifedeveloper.common.infrastructure.GsonObjectSerializer;

@Disabled
public class KafkaNotificationPublisherIT {

    private static final String KAFKA_TOPIC = "rld-test";

    private static final String KAFKA_BOOSTRAP_SERVERS = "localhost:9092";

    private static final Logger LOG = LoggerFactory.getLogger(KafkaNotificationPublisherIT.class);

    private static final KafkaTestConfiguration KAFKA_TEST_CONFIGURATION = new KafkaTestConfiguration();

    private static final KafkaTemplate<String, String> KAFKA_TEMPLATE = new KafkaTemplate<>(KAFKA_TEST_CONFIGURATION.producerFactory());

    @Test
    public void publish() throws Exception {
        KafkaConsumerThread kafkaConsumerThread = createKafkaConsumerThread(2);
        KafkaNotificationPublisher notificationPublisher = createNotificationPublisher();
        List<TestEvent> eventsSent = new ArrayList<>();
        for (int i = 0; i < kafkaConsumerThread.expectedNumberOfEvents; i++) {
            TestEvent event = new TestEvent(42 + i, "foo" + i);
            LOG.debug("Publishing {}", event);
            notificationPublisher.publish(KafkaNotificationPublisherTest.toNotifications(event), KAFKA_TOPIC);
            eventsSent.add(event);
        }
        waitForKafkaMessagesAndVerifyEvents(kafkaConsumerThread, eventsSent);
        stopKafkaConsumerThread(kafkaConsumerThread);
    }

    @Test
    public void publishWithNullEventType() throws Exception {
        KafkaConsumerThread kafkaConsumerThread = createKafkaConsumerThread(1);
        List<TestEvent> eventsSent = Arrays.asList(new TestEvent(42, "foo"));
        Notification notification = new Notification(null, 4711L, null, eventsSent.get(0));
        KafkaNotificationPublisher notificationPublisher = createNotificationPublisher();
        notificationPublisher.publish(Collections.singletonList(notification), KAFKA_TOPIC);
        waitForKafkaMessagesAndVerifyEvents(kafkaConsumerThread, eventsSent);
        stopKafkaConsumerThread(kafkaConsumerThread);
    }

    private KafkaConsumerThread createKafkaConsumerThread(int expectedNumberOfEvents) throws Exception {
        KafkaConsumerThread kafkaConsumerThread = new KafkaConsumerThread(KAFKA_TEST_CONFIGURATION, expectedNumberOfEvents);
        LOG.info("Starting kafkaConsumerThread");
        kafkaConsumerThread.start();
        while (!kafkaConsumerThread.isReady()) {
            Thread.sleep(100);
        }
        LOG.info("kafkaConsumerThread is ready");
        return kafkaConsumerThread;
    }

    private KafkaNotificationPublisher createNotificationPublisher() {
        ObjectSerializer<String> objectSerializer = new GsonObjectSerializer();
        return new KafkaNotificationPublisher(KAFKA_TEMPLATE, objectSerializer);
    }

    private void waitForKafkaMessagesAndVerifyEvents(KafkaConsumerThread kafkaConsumerThread, List<TestEvent> eventsSent)
            throws InterruptedException {
        boolean countZero = kafkaConsumerThread.countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(countZero, "Expected number of events not received");
        assertEquals(eventsSent, kafkaConsumerThread.eventsReceived, "Events recieved not equal to events sent: ");
    }

    private void stopKafkaConsumerThread(KafkaConsumerThread kafkaConsumerThread) throws Exception {
        LOG.info("Requesting kafkaConsumerThread to stop");
        kafkaConsumerThread.requestStop();
        while (kafkaConsumerThread.isAlive()) {
            Thread.sleep(100);
        }
        LOG.info("kafkaConsumerThread stopped");
    }

    /**
     * Kafka configuration used during this test. In a Spring Boot application, this class could be annotated with "@Configuration" and the
     * public methods annotated with "@Bean".
     */
    private static final class KafkaTestConfiguration {

        public ProducerFactory<String, String> producerFactory() {
            return new DefaultKafkaProducerFactory<>(producerConfig());
        }

        private Map<String, Object> producerConfig() {
            Map<String, Object> props = commonConfig();
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return props;
        }

        public ConsumerFactory<String, String> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfig());
        }

        private Map<String, Object> consumerConfig() {
            Map<String, Object> props = commonConfig();
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            return props;
        }

        private Map<String, Object> commonConfig() {
            Map<String, Object> props = new HashMap<>();
            props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOSTRAP_SERVERS);
            try {
                props.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName() + "-rld-test");

            } catch (IOException e) {
                throw new IllegalStateException("Failed to create common Kafka config", e);
            }

            return props;
        }

    }

    /**
     * A thread that reads the latest messages on KAFKA_TOPIC.
     */
    private static final class KafkaConsumerThread extends Thread {
        private volatile boolean running = true;

        private volatile boolean ready = false;

        private Consumer<String, String> kafkaConsumer;

        private List<TestEvent> eventsReceived = new ArrayList<>();

        private int expectedNumberOfEvents;

        private CountDownLatch countDownLatch;

        KafkaConsumerThread(KafkaTestConfiguration config, int expectedNumberOfEvents) {
            this.kafkaConsumer = config.consumerFactory().createConsumer();

            List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(KAFKA_TOPIC);
            List<TopicPartition> topicPartitions = new ArrayList<>();
            for (PartitionInfo partitionInfo : partitionInfos) {
                TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                topicPartitions.add(topicPartition);
            }
            kafkaConsumer.assign(topicPartitions);
            kafkaConsumer.seekToEnd(topicPartitions);

            this.expectedNumberOfEvents = expectedNumberOfEvents;
            this.countDownLatch = new CountDownLatch(expectedNumberOfEvents);
        }

        @Override
        public void run() {
            try {
                LOG.info("Starting polling thread");
                int numPolls = 0;
                while (running) {
                    LOG.debug("Polling kafkaConsumer...");
                    ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
                    numPolls++;
                    if (numPolls >= 3) {
                        // It seems to take three polls until the consumer is ready.
                        ready = true;
                    }
                    for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                        JsonObject jsonObject = JsonParser.parseString(consumerRecord.value()).getAsJsonObject();
                        GsonObjectSerializer objectSerializer = new GsonObjectSerializer();
                        TestEvent event = objectSerializer.deserialize(jsonObject.get("event").toString(), TestEvent.class);
                        LOG.debug("Recevied event: " + event);
                        eventsReceived.add(event);
                        countDownLatch.countDown();
                    }
                }
            } finally {
                kafkaConsumer.close();
            }
        }

        public void requestStop() {
            this.running = false;
        }

        public boolean isReady() {
            return ready;
        }
    }

}
