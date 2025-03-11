package com.azhag_swe.tech_tutorial.dto.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;

    public ErrorResponse() { }

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // Getters and setters
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

