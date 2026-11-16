package com.allobank.splitbill.expense.validator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ParticipantGroupValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParticipantGroup {
    String message() default "Participant not found in this group";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
