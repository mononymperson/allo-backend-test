package com.allobank.splitbill.billgroup;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillGroupRepository extends JpaRepository<BillGroup, UUID> {

}
