package com.allobank.splitbill.expense;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.allobank.splitbill.common.ApiResponse;
import com.allobank.splitbill.expense.dto.SettlementResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    @GetMapping(path = "/bill-groups/{billGroupId}/settlement")
    public ApiResponse<SettlementResponse> settlement(@PathVariable UUID billGroupId) {
        SettlementResponse settlementResponse = settlementService.calculateSettlement(billGroupId);
        return ApiResponse.success("Settlement calculated successfully", settlementResponse);
    }
}
