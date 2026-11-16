package com.allobank.splitbill.billgroup.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBillGroupRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(min = 2)
        List<@NotBlank @Size(max = 100) String> participants) {
}
