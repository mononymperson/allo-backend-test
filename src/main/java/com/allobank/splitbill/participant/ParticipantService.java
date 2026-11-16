package com.allobank.splitbill.participant;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.BillGroupRepository;
import com.allobank.splitbill.common.validation.ValidationService;
import com.allobank.splitbill.common.web.ResourceNotFoundException;
import com.allobank.splitbill.participant.dto.CreateParticipantRequest;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

import jakarta.transaction.Transactional;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ValidationService validationService;
    private final BillGroupRepository billGroupRepository;

    ParticipantService(
            ParticipantRepository participantRepository,
            ValidationService validationService,
            BillGroupRepository billGroupRepository) {
        this.participantRepository = participantRepository;
        this.validationService = validationService;
        this.billGroupRepository = billGroupRepository;
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

    public List<ParticipantResponse> getParticipants(UUID billGroupId) {
        if (!billGroupRepository.existsById(billGroupId)) {
            throw new ResourceNotFoundException("Bill group was not found");
        }

        return participantRepository.findAllByBillGroup_Id(billGroupId)
                .stream()
                .map(participant -> ParticipantResponse.builder()
                        .id(participant.getId())
                        .name(participant.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public Participant getParticipantBy(UUID groupId, UUID participantId) {
        Participant participant = participantRepository.findFirstByIdAndBillGroupId(participantId, groupId)
                .orElseThrow();

        return participant;
    }
}