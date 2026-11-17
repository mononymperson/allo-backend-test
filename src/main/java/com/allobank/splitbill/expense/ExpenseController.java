package com.allobank.splitbill.expense;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.allobank.splitbill.common.ApiResponse;
import com.allobank.splitbill.expense.dto.CreateExpenseRequest;
import com.allobank.splitbill.expense.dto.ExpenseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping(path = "/bill-groups/{billGroupId}/expenses")
    public ApiResponse<ExpenseResponse> create(
            @PathVariable UUID billGroupId,
            @RequestBody CreateExpenseRequest expenseRequest) {

        ExpenseResponse expenseResponse = expenseService.createExpense(billGroupId, expenseRequest);
        return ApiResponse.success("Expense created successfully", expenseResponse);
    }

}