package com.allobank.splitbill.participant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.billgroup.BillGroupRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
public class ParticipantRepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private BillGroupRepository billGroupRepository;

    @Test
    void shouldCreateParticipant() {
        BillGroup billGroup = billGroupRepository.save(new BillGroup());

        assertThat(billGroup.getId()).isNotNull();

        Participant participant = new Participant();

        participant.setName("Daus");
        participant.setBillGroup(billGroup);

        participantRepository.saveAndFlush(participant);

        assertThat(participant.getId()).isNotNull();
        assertThat(participant.getBillGroup().getId()).isEqualTo(billGroup.getId());
    }

    @Test
    void shouldReadParticipant() {
        Participant savedParticipant = saveParticipant("Daus");

        Participant readParticipant = participantRepository
                .findById(savedParticipant.getId())
                .orElseThrow();

        assertThat(readParticipant.getName()).isEqualTo("Daus");
        assertThat(readParticipant.getBillGroup().getId())
                .isEqualTo(savedParticipant.getBillGroup().getId());
    }

    @Test
    void shouldUpdateParticipant() {
        Participant savedParticipant = saveParticipant("Daus");

        savedParticipant.setName("Firdaus");
        participantRepository.saveAndFlush(savedParticipant);

        Participant updatedParticipant = participantRepository
                .findById(savedParticipant.getId())
                .orElseThrow();

        assertThat(updatedParticipant.getName()).isEqualTo("Firdaus");
    }

    @Test
    void shouldDeleteParticipant() {
        Participant savedParticipant = saveParticipant("Daus");
        UUID participantId = savedParticipant.getId();

        participantRepository.deleteById(participantId);
        participantRepository.flush();

        assertThat(participantRepository.findById(participantId)).isEmpty();
    }

    private Participant saveParticipant(String name) {
        BillGroup billGroup = billGroupRepository.save(new BillGroup());

        Participant participant = new Participant();
        participant.setName(name);
        participant.setBillGroup(billGroup);

        return participantRepository.saveAndFlush(participant);
    }
}
