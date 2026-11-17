package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.billgroup.BillGroupService;
import com.allobank.splitbill.expense.dto.BillGroupInfo;
import com.allobank.splitbill.expense.dto.ExpenseTotalPaidByParticipant;
import com.allobank.splitbill.expense.dto.SettlementResponse;
import com.allobank.splitbill.expense.dto.SettlementSummary;
import com.allobank.splitbill.expense.dto.TransferInfo;
import com.allobank.splitbill.expense.split.ExpenseSplitService;
import com.allobank.splitbill.expense.split.dto.ExpenseTotalOweByParticipant;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class SettlementService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;
    private final ExpenseSplitService expenseSplitService;
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

    List<TransferInfo> generateTransferInfo(
            List<ParticipantBalance> participantBalances) {
        List<TransferInfo> transfers = new ArrayList<>();
        List<ParticipantBalance> creditors = new ArrayList<>();
        List<ParticipantBalance> debtors = new ArrayList<>();

        for (ParticipantBalance participant : participantBalances) {

            int comparison = participant.getBalance()
                    .compareTo(BigDecimal.ZERO);

            if (comparison > 0) {
                creditors.add(participant);
            } else if (comparison < 0) {
                debtors.add(participant);
            }
        }

        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size()
                && debtorIndex < debtors.size()) {

            ParticipantBalance creditor = creditors.get(creditorIndex);
            ParticipantBalance debtor = debtors.get(debtorIndex);

            BigDecimal amount = creditor.getBalance()
                    .min(debtor.getBalance().abs());

            creditor.setBalance(
                    creditor.getBalance().subtract(amount));

            debtor.setBalance(
                    debtor.getBalance().add(amount));

            if (creditor.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }

            if (debtor.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }

            ParticipantResponse transferFrom = new ParticipantResponse(
                    debtor.getId(),
                    debtor.getName());

            ParticipantResponse transferTo = new ParticipantResponse(
                    creditor.getId(),
                    creditor.getName());

            transfers.add(
                    new TransferInfo(
                            transferFrom,
                            transferTo,
                            amount));
        }

        return transfers;
    }

    List<ParticipantBalance> generateParticipantBalance(
            UUID billGroupId) {
        List<ExpenseTotalPaidByParticipant> expenseTotalPaidByParticipants = expenseRepository
                .findTotalPaidByParticipant(billGroupId);

        List<ExpenseTotalOweByParticipant> expenseTotalOweByParticipants = expenseSplitService
                .getExpenseTotalOweParticipantBy(billGroupId);

        HashMap<UUID, ParticipantBalance> participantBalances = new HashMap<>();

        expenseTotalPaidByParticipants.forEach(row -> {
            ParticipantBalance participantBalance = new ParticipantBalance();

            participantBalance.setId(row.id());
            participantBalance.setName(row.name());
            participantBalance.setTotalPaid(row.total());
            participantBalance.setBalance(row.total());

            participantBalances.put(
                    row.id(),
                    participantBalance);
        });

        expenseTotalOweByParticipants.forEach(row -> {
            ParticipantBalance participantBalance = participantBalances.get(row.id());

            if (participantBalance == null) {
                participantBalance = new ParticipantBalance();
            }

            participantBalance.setId(row.id());
            participantBalance.setName(row.name());
            participantBalance.setTotalOwes(row.total());
            participantBalance.setBalance(
                    participantBalance.getBalance()
                            .subtract(row.total()));

            participantBalances.put(
                    row.id(),
                    participantBalance);
        });

        return participantBalances.values().stream().toList();
    }

    public SettlementResponse calculateSettlement(UUID billGroupId) {
        BillGroup billGroup = billGroupService.getAndValidateGroupExists(billGroupId);

        BigDecimal subtotal = expenseRepository.getTotalExpenseByBillGroupId(billGroupId);
        BigDecimal serviceChargepct = calculateServiceChargePct();
        BigDecimal serviceChargeAmount = calculateServiceAmount(subtotal, serviceChargepct);
        BigDecimal total = calculateExpenseTotal(subtotal, serviceChargeAmount);

        List<ParticipantBalance> participantBalances = generateParticipantBalance(billGroupId);
        List<TransferInfo> transferInfos = generateTransferInfo(participantBalances);

        return new SettlementResponse(
                BillGroupInfo.from(billGroup),
                new SettlementSummary(
                        subtotal,
                        serviceChargepct,
                        serviceChargeAmount,
                        total),
                transferInfos);
    }
}
