package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import com.allobank.splitbill.expense.Expense;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

public record ExpenseResponse(
        UUID id,
        String description,
        ParticipantResponse paidBy,
        Set<ParticipantResponse> forParticipants,
        BigDecimal serviceChargeCpt,
        BigDecimal serviceChargeAmount,
        BigDecimal subtotal,
        BigDecimal total) {

    public static ExpenseResponse from(Expense expense, Set<ParticipantResponse> forParticipants) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                ParticipantResponse.from(expense.getPaidBy()),
                forParticipants,
                expense.getServiceChargeCpt(),
                expense.getServiceChargeAmount(),
                expense.getSubtotal(),
                expense.getTotal());
    }
}
