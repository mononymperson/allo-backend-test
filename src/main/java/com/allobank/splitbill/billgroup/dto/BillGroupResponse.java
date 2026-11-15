package com.allobank.splitbill.billgroup.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.allobank.splitbill.participant.dto.ParticipantResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillGroupResponse {
    private UUID id;
    private String name;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private List<ParticipantResponse> participants;
}
