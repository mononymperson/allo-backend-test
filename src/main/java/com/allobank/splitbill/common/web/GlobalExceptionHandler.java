package com.allobank.splitbill.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.allobank.splitbill.common.ApiResponse;
import com.allobank.splitbill.common.ResourceNotFoundException;
import com.allobank.splitbill.common.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ApiResponse<Void> handleValidationException(ValidationException exception) {
        return ApiResponse.failure(exception.getMessage(), exception.getErrors());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFound(ResourceNotFoundException exception) {
        return ApiResponse.success(exception.getMessage(), null);
    }
}
