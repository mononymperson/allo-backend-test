package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.allobank.splitbill.expense.dto.ExpenseTotalPaidByParticipant;

interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findAllByBillGroup_Id(UUID billGroupId);

    @Query("""
                SELECT e.paidBy.id, e.paidBy.name, SUM(e.total)
                    FROM Expense e WHERE e.billGroup.id = :billGroupId
                        GROUP BY e.paidBy.id, e.paidBy.name
            """)
    List<ExpenseTotalPaidByParticipant> findTotalPaidByParticipant(
            @Param("billGroupId") UUID billGroupId);

    @Query("""
                SELECT SUM(e.total) FROM Expense e
                    WHERE e.billGroup.id = :billGroupId
                        GROUP BY e.billGroup.id
            """)
    BigDecimal getTotalExpenseByBillGroupId(
            @Param("billGroupId") UUID billGroupId);
}
