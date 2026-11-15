package com.allobank.splitbill.participant;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.allobank.splitbill.billgroup.BillGroup;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bill_group_id", nullable = false)
    private BillGroup billGroup;
}
