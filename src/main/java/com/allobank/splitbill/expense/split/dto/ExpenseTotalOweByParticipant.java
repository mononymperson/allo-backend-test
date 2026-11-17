package com.allobank.splitbill.expense.split.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseTotalOweByParticipant(
                UUID id,
                String name,
                BigDecimal total) {
}