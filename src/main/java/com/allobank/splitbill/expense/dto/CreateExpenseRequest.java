package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import com.allobank.splitbill.expense.validator.ParticipantGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ParticipantGroup
public record CreateExpenseRequest(
        @JsonIgnore
        UUID billGroupId,

        @NotNull
        @Size(max = 255)
        String description,

        @NotNull
        BigDecimal amount,

        @NotNull
        UUID paidBy,

        @NotNull
        @Size(min = 1)
        Set<UUID> forParticipants) {
}
