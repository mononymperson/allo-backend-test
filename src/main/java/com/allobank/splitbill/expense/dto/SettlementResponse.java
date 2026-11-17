package com.allobank.splitbill.expense.dto;

import java.util.List;

public record SettlementResponse(
        BillGroupInfo billGroup,
        SettlementSummary summary,
        List<TransferInfo> transfers) {
}
