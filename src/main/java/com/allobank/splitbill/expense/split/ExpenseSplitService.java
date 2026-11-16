package com.allobank.splitbill.expense.split;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.common.ValidationService;
import com.allobank.splitbill.expense.split.dto.CreateExpenseSplit;
import com.allobank.splitbill.expense.split.dto.ExpenseSplitInfo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseSplitService {
    private final ExpenseSplitRepository expenseSplitRepository;
    private final ValidationService validationService;

    @Transactional
    public List<ExpenseSplitInfo> createSplits(List<CreateExpenseSplit> splits) {

        List<ExpenseSplitInfo> expenseSplitInfos = new ArrayList<>();

        splits.forEach(split -> {
            validationService.validate(split);

            ExpenseSplit expenseSplit = new ExpenseSplit();

            expenseSplit.setExpense(split.expense());
            expenseSplit.setParticipant(split.participant());
            expenseSplit.setAmount(
                    split.amount().setScale(2, RoundingMode.DOWN));

            expenseSplitRepository.save(expenseSplit);

            expenseSplitInfos.add(ExpenseSplitInfo.from(expenseSplit));
        });

        return expenseSplitInfos;
    }
}