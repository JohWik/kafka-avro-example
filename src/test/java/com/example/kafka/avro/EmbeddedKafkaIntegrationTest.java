package com.example.kafka.avro;

import com.example.avro.schema.AvroDevice;
import com.example.avro.schema.AvroEvent;
import com.example.kafka.avro.integration.KafkaConsumer;
import com.example.kafka.avro.integration.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Slf4j
class EmbeddedKafkaIntegrationTest {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private KafkaProducer kafkaProducer;


    @Value("${test.topic}")
    private String topic;


    @BeforeEach
    void prepare() {
        kafkaConsumer.resetLatch();
    }

    @Test
    void givenEmbeddedKafkaBrokerWithSchemaRegistry_whenSendingAvroDeviceMessage_thenMessageReceived() throws Exception {
        AvroDevice a = AvroDevice.newBuilder()
                .setDeviceUuid(UUID.randomUUID())
                .setEvents(Collections.singletonList(AvroEvent.newBuilder()
                        .setEventUuid(UUID.randomUUID())
                        .setTimestamp(Instant.now())
                        .setValue("23.093").build()))
                .build();
        kafkaProducer.send(a);

        boolean messageConsumed = kafkaConsumer.getLatch()
                .await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertEquals(kafkaConsumer.getPayload().value().getDeviceUuid(), a.getDeviceUuid());
    }

}