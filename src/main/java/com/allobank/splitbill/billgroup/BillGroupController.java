package com.allobank.splitbill.billgroup;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.allobank.splitbill.billgroup.dto.BillGroupResponse;
import com.allobank.splitbill.billgroup.dto.CreateBillGroupRequest;
import com.allobank.splitbill.common.ApiResponse;
import com.allobank.splitbill.participant.dto.ParticipantResponse;

@RestController
@RequestMapping("/bill-groups")
public class BillGroupController {
    private final BillGroupService billGroupService;

    BillGroupController(BillGroupService billGroupService) {
        this.billGroupService = billGroupService;
    }

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BillGroupResponse> create(@RequestBody CreateBillGroupRequest createBillGroup) {
        BillGroupResponse response = billGroupService.createBillGroup(createBillGroup);

        return ApiResponse.success("Bill group created successfully", response);
    }

    @GetMapping("/{billGroupId}/participants")
    public ApiResponse<List<ParticipantResponse>> getParticipants(@PathVariable UUID billGroupId) {
        return ApiResponse.success(
                "Participants retrieved successfully",
                billGroupService.getParticipants(billGroupId));
    }
}
