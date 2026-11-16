package com.allobank.splitbill.expense.split.dto;

import java.math.BigDecimal;

import com.allobank.splitbill.expense.Expense;
import com.allobank.splitbill.participant.Participant;

import jakarta.validation.constraints.NotNull;

public record CreateExpenseSplit(
        @NotNull Expense expense,
        @NotNull Participant participant,
        @NotNull BigDecimal amount) {
}