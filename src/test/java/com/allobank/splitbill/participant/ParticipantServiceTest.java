package com.allobank.splitbill.participant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.billgroup.BillGroupRepository;
import com.allobank.splitbill.common.validation.ValidationException;
import com.allobank.splitbill.participant.dto.CreateParticipantRequest;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
class ParticipantServiceTest {
    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private BillGroupRepository billGroupRepository;

    @BeforeEach
    void cleanDatabase() {
        participantRepository.deleteAll();
        billGroupRepository.deleteAll();
    }

    @Test
    void shouldCreateParticipant() {
        BillGroup billGroup = billGroupRepository.save(new BillGroup());
        CreateParticipantRequest request = new CreateParticipantRequest(billGroup, "Daus");

        ParticipantResponse response = participantService.createParticipant(request);

        assertEquals("Daus", response.getName());
        assertEquals(1, participantRepository.count());
        assertEquals(1, billGroup.getParticipants().size());
    }

    @Test
    void shouldRejectBlankParticipantNameWithoutSaving() {
        BillGroup billGroup = billGroupRepository.save(new BillGroup());
        CreateParticipantRequest request = new CreateParticipantRequest(billGroup, " ");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> participantService.createParticipant(request));

        assertEquals("must not be blank", exception.getErrors().get("name"));
        assertEquals(0, participantRepository.count());
    }

    @Test
    void shouldGetParticipantsByBillGroupId() {
        BillGroup billGroup = billGroupRepository.save(new BillGroup());

        participantService.createParticipant(new CreateParticipantRequest(billGroup, "Daus"));
        participantService.createParticipant(new CreateParticipantRequest(billGroup, "Duas"));

        List<ParticipantResponse> responses = participantService.getParticipants(billGroup.getId());

        assertEquals(2, responses.size());
        List<String> names = responses.stream()
                .map(ParticipantResponse::getName)
                .toList();
        assertTrue(names.contains("Daus"));
        assertTrue(names.contains("Duas"));
    }

    @Test
    void shouldRejectParticipantNameLongerThan100CharactersWithoutSaving() {
        BillGroup billGroup = billGroupRepository.save(new BillGroup());
        CreateParticipantRequest request = new CreateParticipantRequest(
                billGroup,
                "a".repeat(101));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> participantService.createParticipant(request));

        assertEquals(
                "size must be between 0 and 100",
                exception.getErrors().get("name"));
        assertEquals(0, participantRepository.count());
    }
}
