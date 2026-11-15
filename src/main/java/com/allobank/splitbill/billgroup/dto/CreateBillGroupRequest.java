package com.allobank.splitbill.billgroup.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBillGroupRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotEmpty
    @Size(min = 2)
    private List<@NotBlank @Size(max = 100) String> participants;
}
