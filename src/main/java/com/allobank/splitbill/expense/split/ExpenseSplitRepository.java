package com.allobank.splitbill.expense.split;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {
    List<ExpenseSplit> findAllByExpense_BillGroup_Id(UUID billGroupId);
}