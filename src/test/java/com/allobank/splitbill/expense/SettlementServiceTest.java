package com.allobank.splitbill.expense;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class SettlementServiceTest {
    @Autowired
    private SettlementService settlementService;

    @Test
    void testCalculateServiceChargePct() {
        BigDecimal expected = BigDecimal.valueOf(4);

        BigDecimal actual = settlementService.calculateServiceChargePct();

        Assertions.assertEquals(0, expected.compareTo(actual));
    }
}
