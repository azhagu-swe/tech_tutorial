package com.azhag_swe.tech_tutorial.exception;


public enum ErrorCode {
    // Example error codes
    AUTH_INVALID_CREDENTIALS("AUTH001", "Invalid username or password"),
    TOKEN_EXPIRED("AUTH002", "Refresh token has expired. Please sign in again."),
    TOKEN_NOT_FOUND("AUTH003", "Refresh token is not in database"),
    
    // Validation Errors
    VALIDATION_FAILED("VAL001", "Validation failed"),
    
    // Resource Errors
    RESOURCE_NOT_FOUND("RES404", "Requested resource not found"),
    
    // General/Internal Errors
    INTERNAL_ERROR("INT500", "An internal error occurred. Please try again later.");

    

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
