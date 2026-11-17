package com.allobank.splitbill.expense;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findAllByBillGroup_Id(UUID billGroupId);
}
