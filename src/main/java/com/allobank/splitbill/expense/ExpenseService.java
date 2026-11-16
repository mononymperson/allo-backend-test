package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.billgroup.BillGroupService;
import com.allobank.splitbill.common.ValidationService;
import com.allobank.splitbill.expense.dto.CreateExpenseRequest;
import com.allobank.splitbill.expense.dto.ExpenseResponse;
import com.allobank.splitbill.expense.split.ExpenseSplitService;
import com.allobank.splitbill.expense.split.dto.CreateExpenseSplit;
import com.allobank.splitbill.expense.split.dto.ExpenseSplitInfo;
import com.allobank.splitbill.participant.Participant;
import com.allobank.splitbill.participant.ParticipantService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseSplitService expenseSplitService;
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

        List<Participant> participants = participantService.getParticipantsBy(expenseRequest.forParticipants(),
                billGroupId);

        BillGroup billGroup = billGroupService.getAndValidateGroupExists(billGroupId);

        Expense expense = new Expense();

        Participant payer = participantService.getParticipantBy(billGroupId, expenseRequest.paidBy());

        BigDecimal subtotal = expenseRequest.amount().setScale(2, RoundingMode.UNNECESSARY);

        BigDecimal serviceChargePct = calculateServiceChargePct();

        BigDecimal serviceChargeAmount = calculateServiceAmount(
                expenseRequest.amount(), serviceChargePct);

        BigDecimal totalExpense = expenseRequest.amount().add(serviceChargeAmount);

        BigDecimal splitsAmount = totalExpense
                .divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.DOWN);

        expense.setBillGroup(billGroup);
        expense.setDescription(expenseRequest.description());
        expense.setPaidBy(payer);
        expense.setSubtotal(subtotal);
        expense.setServiceChargeCpt(serviceChargePct);
        expense.setServiceChargeAmount(serviceChargeAmount);
        expense.setTotal(totalExpense);

        expenseRepository.save(expense);

        List<CreateExpenseSplit> splits = participants.stream().map(participant -> {
            return new CreateExpenseSplit(
                    expense,
                    participant,
                    splitsAmount);
        }).toList();

        List<ExpenseSplitInfo> expenseSplitInfos = expenseSplitService.createSplits(splits);

        return ExpenseResponse.from(expense, expenseSplitInfos);
    }
}
