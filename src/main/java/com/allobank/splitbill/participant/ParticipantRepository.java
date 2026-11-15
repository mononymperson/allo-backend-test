package com.allobank.splitbill.participant;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    List<Participant> findAllByBillGroup_Id(UUID billGroupId);
}