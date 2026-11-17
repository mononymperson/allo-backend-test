package com.allobank.splitbill.expense;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.Length;
import org.hibernate.annotations.UuidGenerator;

import com.allobank.splitbill.billgroup.BillGroup;
import com.allobank.splitbill.participant.Participant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bill_group_id", nullable = false)
    private BillGroup billGroup;

    @Column(length = Length.LONG32)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paid_by_id", nullable = false)
    private Participant paidBy;

    @Column(precision = 15, scale = 2)
    private BigDecimal total;
}
