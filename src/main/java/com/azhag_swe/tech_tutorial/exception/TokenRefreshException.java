package com.azhag_swe.tech_tutorial.exception;

public class TokenRefreshException extends ApplicationException {
    public TokenRefreshException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }

    public TokenRefreshException(String token) {
        super(ErrorCode.TOKEN_EXPIRED,
                String.format("Token [%s] error: %s", token, ErrorCode.TOKEN_EXPIRED.getMessage()));
    }
}
