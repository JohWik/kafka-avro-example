package com.example.kafka.avro.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeviceDto {
    private UUID deviceUuid;
    List<EventDto> events;
}
