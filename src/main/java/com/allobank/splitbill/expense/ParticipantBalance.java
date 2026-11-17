package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantBalance {
    private UUID id;
    private String name;
    private BigDecimal totalPaid = BigDecimal.ZERO;
    private BigDecimal totalOwes = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
}