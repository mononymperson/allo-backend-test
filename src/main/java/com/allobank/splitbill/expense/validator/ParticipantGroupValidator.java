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
        if (request == null || request.getBillGroupId() == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (request.getPaidBy() != null) {
            try {
                participantService.getParticipantBy(request.getBillGroupId(), request.getPaidBy());
            } catch (Exception e) {
                context.buildConstraintViolationWithTemplate("Participant not found in this group")
                        .addPropertyNode("paidBy")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if (request.getForParticipants() != null) {
            for (UUID participantId : request.getForParticipants()) {
                try {
                    participantService.getParticipantBy(request.getBillGroupId(), participantId);
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
