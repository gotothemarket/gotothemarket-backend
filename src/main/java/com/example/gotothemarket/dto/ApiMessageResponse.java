package com.example.gotothemarket.dto;

public record ApiMessageResponse(boolean success, String message, int status) {
    public static ApiMessageResponse created(String message) {
        return new ApiMessageResponse(true, message, 201);
    }
}