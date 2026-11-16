package com.allobank.splitbill.expense.split;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.allobank.splitbill.expense.Expense;
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
@Table(name = "expense_splits")
public class ExpenseSplit {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Expense expense;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Participant participant;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;
}
