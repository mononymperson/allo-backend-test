package com.allobank.splitbill.expense.validator;

import java.util.UUID;

import com.allobank.splitbill.expense.dto.CreateExpenseRequest;
import com.allobank.splitbill.participant.ParticipantService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParticipantGroupValidator implements ConstraintValidator<ParticipantGroup, CreateExpenseRequest> {
    private final ParticipantService participantService;

    @Override
    public boolean isValid(CreateExpenseRequest request, ConstraintValidatorContext context) {
        if (request == null || request.billGroupId() == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (request.paidBy() != null) {
            try {
                participantService.getParticipantBy(request.billGroupId(), request.paidBy());
            } catch (Exception e) {
                context.buildConstraintViolationWithTemplate("Participant not found in this group")
                        .addPropertyNode("paidBy")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if (request.forParticipants() != null) {
            for (UUID participantId : request.forParticipants()) {
                try {
                    participantService.getParticipantBy(request.billGroupId(), participantId);
                } catch (Exception e) {
                    context.buildConstraintViolationWithTemplate("Participant not found in this group")
                            .addPropertyNode("forParticipants")
                            .addConstraintViolation();
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }
}
