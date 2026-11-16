package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.billgroup.BillGroupService;
import com.allobank.splitbill.common.validation.ValidationService;
import com.allobank.splitbill.expense.dto.CreateExpenseRequest;
import com.allobank.splitbill.expense.dto.ExpenseResponse;
import com.allobank.splitbill.participant.Participant;
import com.allobank.splitbill.participant.ParticipantService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ValidationService validationService;
    private final ParticipantService participantService;
    private final BillGroupService billGroupService;

    BigDecimal calculateServiceChargePct() {
        String githubUsername = "mononymperson";
        int sum = githubUsername.chars().sum();

        return new BigDecimal(sum % 10);
    }

    BigDecimal calculateServiceAmount(BigDecimal amount, BigDecimal serviceChargePct) {
        return amount
                .multiply(serviceChargePct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
    }

    BigDecimal calculateExpenseTotal(BigDecimal amount, BigDecimal serviceAmount) {
        return amount
                .add(serviceAmount)
                .setScale(2, RoundingMode.DOWN);
    }

    @Transactional
    public ExpenseResponse createExpense(UUID billGroupId, CreateExpenseRequest expenseRequest) {
        validationService.validate(expenseRequest);

        BillGroup billGroup = billGroupService.getAndValidateGroupExists(billGroupId);

        Expense expense = new Expense();

        Participant payer = participantService.getParticipantBy(billGroupId, expenseRequest.getPaidBy());

        BigDecimal serviceChargePct = calculateServiceChargePct();
        BigDecimal serviceChargeAmount = calculateServiceAmount(expenseRequest.getAmount(), serviceChargePct);

        expense.setBillGroup(billGroup);
        expense.setDescription(expenseRequest.getDescription());
        expense.setPaidBy(payer);
        expense.setSubtotal(expenseRequest.getAmount().setScale(2, RoundingMode.UNNECESSARY));
        expense.setServiceChargeCpt(serviceChargePct);
        expense.setServiceChargeAmount(serviceChargeAmount);
        expense.setTotal(expenseRequest.getAmount().add(serviceChargeAmount));

        expenseRepository.save(expense);

        return ExpenseResponse.builder()
                .description(expense.getDescription())
                .serviceChargeCpt(expense.getServiceChargeCpt())
                .serviceChargeAmount(expense.getServiceChargeAmount())
                .subtotal(expense.getSubtotal())
                .total(expense.getTotal())
                .paidBy(expense.getPaidBy().getId())
                .forParticipants(expenseRequest.getForParticipants()).build();

    }
}