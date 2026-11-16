package com.allobank.splitbill.billgroup.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.allobank.splitbill.participant.dto.ParticipantResponse;

public record BillGroupResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant lastModifiedAt,
        List<ParticipantResponse> participants) {
}
