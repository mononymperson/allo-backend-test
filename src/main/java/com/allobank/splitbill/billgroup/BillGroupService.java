package com.allobank.splitbill.billgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.common.validation.ValidationService;
import com.allobank.splitbill.participant.ParticipantService;
import com.allobank.splitbill.participant.dto.CreateParticipantRequest;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

import jakarta.transaction.Transactional;

@Service
public class BillGroupService {
    private final ParticipantService participantService;
    private final BillGroupRepository billGroupRepository;
    private final ValidationService validationService;

    BillGroupService(
            BillGroupRepository billGroupRepository,
            ParticipantService participantService,
            ValidationService validationService) {
        this.billGroupRepository = billGroupRepository;
        this.participantService = participantService;
        this.validationService = validationService;
    }

    @Transactional
    public BillGroupResponse createBillGroup(CreateBillGroupRequest request) {
        validationService.validate(request);

        BillGroup billGroup = new BillGroup();

        billGroup.setName(request.getName());

        billGroupRepository.save(billGroup);

        List<ParticipantResponse> participants = new ArrayList<>();
        request.getParticipants().forEach(participant -> {
            CreateParticipantRequest createParticipant = new CreateParticipantRequest();

            createParticipant.setBillGroup(billGroup);
            createParticipant.setName(participant);

            participants.add(participantService.createParticipant(createParticipant));
        });

        return BillGroupResponse.builder()
                .name(billGroup.getName())
                .participants(participants).build();
    }

    public List<ParticipantResponse> getParticipants(UUID billGroupId) {
        return participantService.getParticipants(billGroupId);
    }
}
