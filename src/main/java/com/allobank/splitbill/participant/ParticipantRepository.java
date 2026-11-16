package com.allobank.splitbill.participant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    List<Participant> findAllByBillGroup_Id(UUID billGroupId);

    Optional<Participant> findFirstByIdAndBillGroupId(UUID id, UUID bill_group_id);
}