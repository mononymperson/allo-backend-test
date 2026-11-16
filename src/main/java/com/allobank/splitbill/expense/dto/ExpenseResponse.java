package com.allobank.splitbill.expense.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseResponse {
    private UUID id;

    private String description;

    private UUID paidBy;

    private Set<UUID> forParticipants;

    private BigDecimal serviceChargeCpt;

    private BigDecimal serviceChargeAmount;

    private BigDecimal subtotal;

    private BigDecimal total;
}