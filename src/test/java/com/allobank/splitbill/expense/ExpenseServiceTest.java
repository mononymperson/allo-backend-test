
package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.allobank.splitbill.billgroup.BillGroupService;
import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.expense.dto.CreateExpenseRequest;
import com.allobank.splitbill.expense.dto.ExpenseResponse;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
public class ExpenseServiceTest {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private BillGroupService billGroupService;

    @Test
    void testCalculateServiceChargePct() {
        BigDecimal expected = BigDecimal.valueOf(4);

        BigDecimal actual = expenseService.calculateServiceChargePct();

        Assertions.assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void testCalculateServiceAmount() {
        BigDecimal chargePct = BigDecimal.valueOf(4);
        BigDecimal expenseAmount = new BigDecimal("20333000");
        BigDecimal expected = new BigDecimal("813320.00");

        BigDecimal actual = expenseService.calculateServiceAmount(
                expenseAmount, chargePct);

        Assertions.assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void testCalculateServiceAmountRounding() {
        BigDecimal chargePct = BigDecimal.valueOf(4);
        BigDecimal expenseAmount = new BigDecimal("20500999.99");

        // 20500999.99 * 4% = 820039.9996
        // DOWN = 820039.99
        BigDecimal expected = new BigDecimal("820039.99");

        BigDecimal actual = expenseService.calculateServiceAmount(
                expenseAmount, chargePct);

        Assertions.assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void testCalculateExpenseTotal() {
        BigDecimal expenseAmount = new BigDecimal("20333000.00");
        BigDecimal serviceAmount = new BigDecimal("813320.00");

        BigDecimal expected = new BigDecimal("21146320.00");

        BigDecimal actual = expenseService.calculateExpenseTotal(
                expenseAmount, serviceAmount);

        Assertions.assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void testCalculateExpenseTotalRounded() {
        BigDecimal expenseAmount = new BigDecimal("20500999.99");
        BigDecimal serviceAmount = new BigDecimal("820039.99");

        BigDecimal expected = new BigDecimal("21321039.98");

        BigDecimal actual = expenseService.calculateExpenseTotal(
                expenseAmount, serviceAmount);

        Assertions.assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void testCreateExpense() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest();

        createBillGroupRequest.setName("Trip ke bandung");

        List<String> participants = new ArrayList<>();

        participants.add("Daus");
        participants.add("Firdaus");
        participants.add("UsDaus");

        createBillGroupRequest.setParticipants(participants);

        BillGroupResponse billGroupResponse = billGroupService.createBillGroup(createBillGroupRequest);

        Assertions.assertNotNull(billGroupResponse);

        CreateExpenseRequest createExpenseRequest = new CreateExpenseRequest();

        createExpenseRequest.setDescription("Makan");
        createExpenseRequest.setAmount(new BigDecimal(50_500_000));
        createExpenseRequest.setPaidBy(
                billGroupResponse.getParticipants().get(0).getId());

        createExpenseRequest.setForParticipants(
                Set.of(
                        billGroupResponse.getParticipants().get(1).getId(),
                        billGroupResponse.getParticipants().get(2).getId()));

        ExpenseResponse expenseResponse = expenseService.createExpense(billGroupResponse.getId(), createExpenseRequest);

        BigDecimal expectedSubtotal = new BigDecimal("50500000.00");
        BigDecimal expectedServiceCpt = new BigDecimal(4);
        BigDecimal expectedServiceAmount = new BigDecimal("2020000.00");
        BigDecimal expectedTotal = new BigDecimal("52520000.00");

        Assertions.assertNotNull(expenseResponse);
        Assertions.assertEquals(1, expenseRepository.count());
        Assertions.assertEquals(expectedSubtotal, expenseResponse.getSubtotal());
        Assertions.assertEquals(expectedServiceCpt, expenseResponse.getServiceChargeCpt());
        Assertions.assertEquals(expectedServiceAmount, expenseResponse.getServiceChargeAmount());
        Assertions.assertEquals(expectedTotal, expenseResponse.getTotal());
    }
}
