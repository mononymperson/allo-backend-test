package com.allobank.splitbill.expense;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ExpenseRepository extends JpaRepository<Expense, UUID> {

}
