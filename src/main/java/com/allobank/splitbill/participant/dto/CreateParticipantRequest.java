package com.allobank.splitbill.participant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateParticipantRequest(
        @NotBlank
        @Size(max = 100)
        String name) {
}
