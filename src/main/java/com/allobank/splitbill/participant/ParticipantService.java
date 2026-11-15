package com.allobank.splitbill.participant;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.common.validation.ValidationService;
import com.allobank.splitbill.participant.dto.CreateParticipantRequest;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

import jakarta.transaction.Transactional;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ValidationService validationService;

    ParticipantService(
            ParticipantRepository participantRepository,
            ValidationService validationService
    ) {
        this.participantRepository = participantRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ParticipantResponse createParticipant(CreateParticipantRequest createParticipant) {
        validationService.validate(createParticipant);

        Participant participant = new Participant();

        participant.setName(createParticipant.getName());
        createParticipant.getBillGroup().addParticipant(participant);

        participantRepository.save(participant);

        return ParticipantResponse.builder()
                .id(participant.getId())
                .name(participant.getName())
                .build();
    }
}