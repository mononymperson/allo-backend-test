package com.allobank.splitbill.expense.split.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.allobank.splitbill.expense.split.ExpenseSplit;

public record ExpenseSplitInfo(
        UUID id,
        String name,
        BigDecimal amount) {
    public static ExpenseSplitInfo from(ExpenseSplit expenseSplit) {
        return new ExpenseSplitInfo(
                expenseSplit.getId(),
                expenseSplit.getParticipant().getName(),
                expenseSplit.getAmount());
    }
}
