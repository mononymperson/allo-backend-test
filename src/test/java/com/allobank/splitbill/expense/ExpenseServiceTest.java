package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.allobank.splitbill.billgroup.BillGroupService;
import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.expense.dto.CreateExpenseRequest;
import com.allobank.splitbill.expense.dto.ExpenseResponse;
import com.allobank.splitbill.expense.split.dto.ExpenseSplitInfo;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class ExpenseServiceTest {
        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        private ExpenseService expenseService;

        @Autowired
        private BillGroupService billGroupService;

        @Test
        void testCreateExpense() {
                CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest(
                                "Trip ke bandung",
                                List.of("Daus", "Firdaus", "UsDaus"));

                BillGroupResponse billGroupResponse = billGroupService.createBillGroup(createBillGroupRequest);

                Assertions.assertNotNull(billGroupResponse);

                CreateExpenseRequest createExpenseRequest = new CreateExpenseRequest(
                                null,
                                "Makan",
                                new BigDecimal(50_500_000),
                                billGroupResponse.participants().get(0).id(),
                                Set.of(
                                                billGroupResponse.participants().get(1).id(),
                                                billGroupResponse.participants().get(2).id()));

                ExpenseResponse expenseResponse = expenseService.createExpense(
                                billGroupResponse.id(), createExpenseRequest);

                BigDecimal expectedTotal = new BigDecimal("50500000");
                BigDecimal expectedSplitAmount = new BigDecimal("25250000.00");

                Assertions.assertNotNull(expenseResponse);
                Assertions.assertEquals(1, expenseRepository.count());
                Assertions.assertEquals(expectedTotal, expenseResponse.total());

                // expense split check
                Assertions.assertEquals(2, expenseResponse.forParticipants().size());
                Assertions.assertEquals(expectedSplitAmount, expenseResponse.forParticipants().get(0).amount());

                List<String> splitParticipantNames = expenseResponse.forParticipants().stream()
                                .map(ExpenseSplitInfo::name)
                                .toList();
                Assertions.assertTrue(
                                splitParticipantNames.containsAll(List.of("Firdaus", "UsDaus")),
                                "expected split participants [Firdaus, UsDaus] but was: " + splitParticipantNames);
        }
}
