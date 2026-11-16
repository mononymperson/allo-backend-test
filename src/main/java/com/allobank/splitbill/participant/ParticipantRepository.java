package com.allobank.splitbill.participant;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    List<Participant> findAllByBillGroup_Id(UUID billGroupId);

    Optional<Participant> findByIdAndBillGroupId(UUID id, UUID billGroupId);

    boolean existsByIdInAndBillGroup_Id(Set<UUID> ids, UUID billGroupId);

    List<Participant> findAllByIdInAndBillGroup_Id(Set<UUID> ids, UUID billGroupId);
}
