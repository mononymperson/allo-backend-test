package com.allobank.splitbill.billgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.common.ResourceNotFoundException;
import com.allobank.splitbill.common.ValidationService;
import com.allobank.splitbill.participant.ParticipantService;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillGroupService {
    private final ParticipantService participantService;
    private final BillGroupRepository billGroupRepository;
    private final ValidationService validationService;

    public BillGroup getAndValidateGroupExists(UUID groupId) {
        return billGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill group was not found"));
    }

    @Transactional
    public BillGroupResponse createBillGroup(CreateBillGroupRequest request) {
        validationService.validate(request);

        BillGroup billGroup = new BillGroup();
        billGroup.setName(request.name());

        billGroupRepository.save(billGroup);

        List<ParticipantResponse> participants = new ArrayList<>();
        request.participants()
                .forEach(name -> participants.add(participantService.createParticipant(billGroup, name)));

        return new BillGroupResponse(
                billGroup.getId(),
                billGroup.getName(),
                billGroup.getCreatedAt(),
                billGroup.getLastModifiedAt(),
                participants);
    }

    public List<ParticipantResponse> getParticipants(UUID billGroupId) {
        getAndValidateGroupExists(billGroupId);

        return participantService.getParticipants(billGroupId);
    }
}
