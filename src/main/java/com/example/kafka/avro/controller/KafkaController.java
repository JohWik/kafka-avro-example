package com.example.kafka.avro.controller;

import com.example.avro.schema.AvroDevice;
import com.example.avro.schema.AvroEvent;
import com.example.kafka.avro.integration.KafkaProducer;
import com.example.kafka.avro.dto.DeviceDto;
import com.example.kafka.avro.dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/devices")
@Slf4j
public class KafkaController {
    private Random rd = new Random();
    private final KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/")
    public void addDevice(@RequestBody DeviceDto deviceDto) {
        String path = "POST /api/v1/devices";
        log.info(path);
        long start = System.currentTimeMillis();

        kafkaProducer.send(getAvroDevice(deviceDto));

        long stop = System.currentTimeMillis();
        log.info(path + " - Time spent " + (stop - start) + " ms.");

    }

    AvroDevice getAvroDevice(DeviceDto deviceDto) {

        return AvroDevice.newBuilder()
                .setDeviceUuid(deviceDto.getDeviceUuid())
                .setEvents(getAvroEvents(deviceDto.getEvents()))
                .build();
    }

    private List<AvroEvent> getAvroEvents(List<EventDto> events) {
        return events.stream().map(eventDto -> AvroEvent.newBuilder()
                .setEventUuid(eventDto.getEventUuid())
                .setTimestamp(eventDto.getTimestamp().toInstant(ZoneOffset.UTC))
                .setValue(eventDto.getValue())
                .build()).toList();
    }
}
