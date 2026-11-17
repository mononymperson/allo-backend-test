package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseTotalPaidByParticipant(
        UUID id,
        String name,
        BigDecimal total) {
}