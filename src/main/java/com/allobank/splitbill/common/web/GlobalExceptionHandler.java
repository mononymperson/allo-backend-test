package com.allobank.splitbill.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.allobank.splitbill.common.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ApiResponse<Void> handleValidationException(ValidationException exception) {
        return ApiResponse.<Void>builder()
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFound(ResourceNotFoundException exception) {
        return ApiResponse.<Void>builder()
                .message(exception.getMessage())
                .build();
    }
}
