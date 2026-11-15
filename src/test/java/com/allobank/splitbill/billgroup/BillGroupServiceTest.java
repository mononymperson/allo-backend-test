package com.allobank.splitbill.billgroup;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.common.validation.ValidationException;
import com.allobank.splitbill.participant.ParticipantRepository;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
public class BillGroupServiceTest {
    @Autowired
    private BillGroupService billGroupService;

    @Autowired
    private BillGroupRepository billGroupRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @BeforeEach
    void cleanDatabase() {
        participantRepository.deleteAll();
        billGroupRepository.deleteAll();
    }

    @Test
    void testCreateBillGroup() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest();

        createBillGroupRequest.setName("Trip ke bandung");

        List<String> participants = new ArrayList<>();

        participants.add("Daus");
        participants.add("Firdaus");
        participants.add("UsDaus");

        createBillGroupRequest.setParticipants(participants);

        BillGroupResponse billGroupResponse = billGroupService.createBillGroup(createBillGroupRequest);

        Assertions.assertEquals("Trip ke bandung", billGroupResponse.getName());
        Assertions.assertEquals("Daus", billGroupResponse.getParticipants().get(0).getName());
        Assertions.assertEquals("Firdaus", billGroupResponse.getParticipants().get(1).getName());
        Assertions.assertEquals("UsDaus", billGroupResponse.getParticipants().get(2).getName());
    }

    @Test
    void testCreateBillGroupFailed() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest();

        createBillGroupRequest.setName("Trip ke bandung");

        List<String> participants = new ArrayList<>();

        createBillGroupRequest.setParticipants(participants);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> billGroupService.createBillGroup(createBillGroupRequest));

        Assertions.assertEquals(
                "must not be empty",
                exception.getErrors().get("participants"));
        Assertions.assertEquals(0, billGroupRepository.count());
        Assertions.assertEquals(0, participantRepository.count());
    }

    @Test
    void testCreateBillGroupFailedWhenParticipantsAreLessThanTwo() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest();
        createBillGroupRequest.setName("Trip ke bandung");
        createBillGroupRequest.setParticipants(List.of("Daus"));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> billGroupService.createBillGroup(createBillGroupRequest));

        Assertions.assertEquals(
                "size must be between 2 and 2147483647",
                exception.getErrors().get("participants"));
        Assertions.assertEquals(0, billGroupRepository.count());
        Assertions.assertEquals(0, participantRepository.count());
    }

    @Test
    void testCreateBillGroupFailedWhenParticipantNameIsTooLong() {
        CreateBillGroupRequest createBillGroupRequest = new CreateBillGroupRequest();
        createBillGroupRequest.setName("Trip ke bandung");
        createBillGroupRequest.setParticipants(List.of("a".repeat(102), "daus"));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> billGroupService.createBillGroup(createBillGroupRequest));

        Assertions.assertEquals(
                "size must be between 0 and 100",
                exception.getErrors().get("participants[0]"));

        Assertions.assertEquals(0, billGroupRepository.count());
        Assertions.assertEquals(0, participantRepository.count());
    }
}
