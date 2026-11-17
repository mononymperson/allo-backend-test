package com.allobank.splitbill.expense.split;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.allobank.splitbill.expense.split.dto.ExpenseTotalOweByParticipant;

interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {
    List<ExpenseSplit> findAllByExpense_BillGroup_Id(UUID billGroupId);

    @Query("""
                SELECT
                    es.participant.id AS participantId,
                    es.participant.name AS participantName,
                    SUM(es.amount) AS totalOwes
                FROM ExpenseSplit es
                WHERE es.expense.billGroup.id = :billGroupId
                GROUP BY es.participant.id, es.participant.name
            """)
    List<ExpenseTotalOweByParticipant> findTotalOwesByParticipant(
            @Param("billGroupId") UUID billGroupId);
}