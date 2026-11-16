package com.allobank.splitbill.common;

import java.util.Map;

public record ApiResponse<T>(String message, T data, Map<String, String> errors) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, null);
    }

    public static <T> ApiResponse<T> failure(String message, Map<String, String> errors) {
        return new ApiResponse<>(message, null, errors);
    }
}
