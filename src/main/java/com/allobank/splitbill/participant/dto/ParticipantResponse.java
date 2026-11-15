package com.allobank.splitbill.participant.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantResponse {
    private UUID id;
    private String name;
}
