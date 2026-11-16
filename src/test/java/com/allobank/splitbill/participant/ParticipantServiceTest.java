package com.allobank.splitbill.participant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.billgroup.BillGroupService;
import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.common.ValidationException;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ParticipantServiceTest {
    @Autowired
    private ParticipantService participantService;

    @Autowired
    private BillGroupService billGroupService;

    private BillGroup createGroup() {
        BillGroupResponse response = billGroupService.createBillGroup(
                new CreateBillGroupRequest("Trip ke bandung", List.of("Daus", "Duas")));

        return billGroupService.getAndValidateGroupExists(response.id());
    }

    @Test
    void shouldCreateParticipant() {
        BillGroup billGroup = createGroup();

        ParticipantResponse response = participantService.createParticipant(billGroup, "Kuntos");

        assertEquals("Kuntos", response.name());
        assertEquals(3, participantService.getParticipants(billGroup.getId()).size());
    }

    @Test
    void shouldRejectBlankParticipantNameWithoutSaving() {
        BillGroup billGroup = createGroup();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> participantService.createParticipant(billGroup, " "));

        assertEquals("must not be blank", exception.getErrors().get("name"));
        assertEquals(2, participantService.getParticipants(billGroup.getId()).size());
    }

    @Test
    void shouldGetParticipantsByBillGroupId() {
        BillGroup billGroup = createGroup();

        List<ParticipantResponse> responses = participantService.getParticipants(billGroup.getId());

        assertEquals(2, responses.size());
        List<String> names = responses.stream()
                .map(ParticipantResponse::name)
                .toList();
        assertTrue(names.contains("Daus"));
        assertTrue(names.contains("Duas"));
    }

    @Test
    void shouldRejectParticipantNameLongerThan100CharactersWithoutSaving() {
        BillGroup billGroup = createGroup();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> participantService.createParticipant(billGroup, "a".repeat(101)));

        assertEquals(
                "size must be between 0 and 100",
                exception.getErrors().get("name"));
        assertEquals(2, participantService.getParticipants(billGroup.getId()).size());
    }
}
