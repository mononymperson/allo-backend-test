package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;

public record SettlementSummary(
                BigDecimal totalExpenses,
                BigDecimal serviceChargePct,
                BigDecimal serviceChargeAmount,
                BigDecimal grandTotal) {
}
