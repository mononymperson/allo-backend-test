package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.allobank.splitbill.expense.Expense;
import com.allobank.splitbill.expense.split.dto.ExpenseSplitInfo;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

public record ExpenseResponse(
        UUID id,
        String description,
        ParticipantResponse paidBy,
        List<ExpenseSplitInfo> forParticipants,
        BigDecimal total) {

    public static ExpenseResponse from(Expense expense, List<ExpenseSplitInfo> forParticipants) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                ParticipantResponse.from(expense.getPaidBy()),
                forParticipants,
                expense.getTotal());
    }
}
