package com.allobank.splitbill.expense.split;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {

}