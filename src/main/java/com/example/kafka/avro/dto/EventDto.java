package com.example.kafka.avro.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventDto {
    private UUID eventUuid;
    private LocalDateTime timestamp;
    private String value;
}
