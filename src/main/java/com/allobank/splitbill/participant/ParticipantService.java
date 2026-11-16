package com.allobank.splitbill.participant;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.common.ResourceNotFoundException;
import com.allobank.splitbill.common.ValidationService;
import com.allobank.splitbill.participant.dto.CreateParticipantRequest;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ValidationService validationService;

    @Transactional
    public ParticipantResponse createParticipant(BillGroup billGroup, String name) {
        validationService.validate(new CreateParticipantRequest(name));

        Participant participant = new Participant();
        participant.setName(name);
        participant.setBillGroup(billGroup);

        participantRepository.save(participant);

        return ParticipantResponse.from(participant);
    }

    public List<ParticipantResponse> getParticipants(UUID billGroupId) {
        return participantRepository.findAllByBillGroup_Id(billGroupId)
                .stream()
                .map(ParticipantResponse::from)
                .toList();
    }

    public Participant getParticipantBy(UUID groupId, UUID participantId) {
        return participantRepository.findByIdAndBillGroupId(participantId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant was not found"));
    }

    public List<Participant> getParticipantsBy(
            Set<UUID> participants,
            UUID groupId) {
        return participantRepository.findAllByIdInAndBillGroup_Id(participants, groupId);

    }
}
