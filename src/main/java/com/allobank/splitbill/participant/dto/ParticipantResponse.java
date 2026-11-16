package com.allobank.splitbill.participant.dto;

import java.util.UUID;

import com.allobank.splitbill.participant.Participant;

public record ParticipantResponse(UUID id, String name) {
    public static ParticipantResponse from(Participant participant) {
        return new ParticipantResponse(participant.getId(), participant.getName());
    }
}
