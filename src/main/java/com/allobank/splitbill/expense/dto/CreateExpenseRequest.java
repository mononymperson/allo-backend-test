package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import com.allobank.splitbill.expense.validator.ParticipantGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@ParticipantGroup
@Data
public class CreateExpenseRequest {
    @JsonIgnore
    private UUID billGroupId;

    @NotNull
    @Size(max = 255)
    private String description;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private UUID paidBy;

    @NotNull
    @Size(min = 1)
    private Set<UUID> forParticipants;
}
