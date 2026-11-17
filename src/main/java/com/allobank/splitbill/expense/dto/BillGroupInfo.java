package com.allobank.splitbill.expense.dto;

import java.util.UUID;

import com.allobank.splitbill.billgroup.BillGroup;

public record BillGroupInfo(
        UUID id,
        String name) {
    public static BillGroupInfo from(BillGroup billGroup) {
        return new BillGroupInfo(billGroup.getId(), billGroup.getName());
    }
}
