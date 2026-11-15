package com.allobank.splitbill.common.validation;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;

@Service
public class ValidationService {
    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> T validate(T object) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (ConstraintViolation<T> violation : validator.validate(object)) {
            errors.putIfAbsent(toFieldPath(violation.getPropertyPath()), violation.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return object;
    }

    private String toFieldPath(Path propertyPath) {
        StringBuilder fieldPath = new StringBuilder();

        for (Path.Node node : propertyPath) {
            String nodeName = node.getName();
            boolean syntheticNode = nodeName == null || nodeName.startsWith("<");

            if (!syntheticNode) {
                if (!fieldPath.isEmpty()) {
                    fieldPath.append('.');
                }
                fieldPath.append(nodeName);
            }

            if (node.getIndex() != null) {
                fieldPath.append('[').append(node.getIndex()).append(']');
            } else if (node.getKey() != null) {
                fieldPath.append('[').append(node.getKey()).append(']');
            }
        }

        return fieldPath.toString();
    }
}
