package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;

import com.allobank.splitbill.participant.dto.ParticipantResponse;

public record TransferInfo(
        ParticipantResponse from,
        ParticipantResponse to,
        BigDecimal amount) {
}
