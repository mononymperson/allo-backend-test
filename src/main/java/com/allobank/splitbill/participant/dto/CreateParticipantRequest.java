package com.allobank.splitbill.participant.dto;

import com.allobank.splitbill.billgroup.BillGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipantRequest {
    @NotNull
    private BillGroup billGroup;

    @NotBlank
    @Size(max = 100)
    private String name;
}