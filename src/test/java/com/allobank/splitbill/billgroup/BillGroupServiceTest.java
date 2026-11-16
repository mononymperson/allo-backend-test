package com.allobank.splitbill.billgroup;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.common.ValidationException;
import com.allobank.splitbill.participant.ParticipantService;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class BillGroupServiceTest {
    @Autowired
    private BillGroupService billGroupService;

    @Autowired
    private BillGroupRepository billGroupRepository;

    @Autowired
    private ParticipantService participantService;

    @Test
    void testCreateBillGroup() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest(
                "Trip ke bandung",
                List.of("Daus", "Firdaus", "UsDaus"));

        BillGroupResponse billGroupResponse = billGroupService.createBillGroup(createBillGroupRequest);

        Assertions.assertEquals("Trip ke bandung", billGroupResponse.name());
        Assertions.assertEquals("Daus", billGroupResponse.participants().get(0).name());
        Assertions.assertEquals("Firdaus", billGroupResponse.participants().get(1).name());
        Assertions.assertEquals("UsDaus", billGroupResponse.participants().get(2).name());
        Assertions.assertEquals(3, participantService.getParticipants(billGroupResponse.id()).size());
    }

    @Test
    void testCreateBillGroupFailed() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest(
                "Trip ke bandung",
                List.of());

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> billGroupService.createBillGroup(createBillGroupRequest));

        Assertions.assertEquals(
                "size must be between 2 and 2147483647",
                exception.getErrors().get("participants"));
        Assertions.assertEquals(0, billGroupRepository.count());
    }

    @Test
    void testCreateBillGroupFailedWhenParticipantsAreLessThanTwo() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest(
                "Trip ke bandung",
                List.of("Daus"));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> billGroupService.createBillGroup(createBillGroupRequest));

        Assertions.assertEquals(
                "size must be between 2 and 2147483647",
                exception.getErrors().get("participants"));
        Assertions.assertEquals(0, billGroupRepository.count());
    }

    @Test
    void testCreateBillGroupFailedWhenParticipantNameIsTooLong() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest(
                "Trip ke bandung",
                List.of("a".repeat(102), "daus"));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> billGroupService.createBillGroup(createBillGroupRequest));

        Assertions.assertEquals(
                "size must be between 0 and 100",
                exception.getErrors().get("participants[0]"));

        Assertions.assertEquals(0, billGroupRepository.count());
    }
}
